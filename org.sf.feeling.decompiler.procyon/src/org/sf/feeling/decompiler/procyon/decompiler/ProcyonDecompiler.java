/*******************************************************************************
 * Copyright (c) 2016 Chen Chao(cnfree2000@hotmail.com).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Chen Chao  - initial API and implementation
 *******************************************************************************/

package org.sf.feeling.decompiler.procyon.decompiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.editor.IDecompiler;
import org.sf.feeling.decompiler.procyon.ProcyonDecompilerPlugin;
import org.sf.feeling.decompiler.util.FileUtil;
import org.sf.feeling.decompiler.util.JarClassExtractor;
import org.sf.feeling.decompiler.util.UIUtil;
import org.sf.feeling.decompiler.util.UnicodeUtil;

import com.strobel.assembler.metadata.DeobfuscationUtilities;
import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.LineNumberFormatter;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.decompiler.languages.TypeDecompilationResults;

public class ProcyonDecompiler implements IDecompiler
{

	private String source = ""; // $NON-NLS-1$ //$NON-NLS-1$
	private long time, start;
	private String log = ""; //$NON-NLS-1$

	/**
	 * Performs a <code>Runtime.exec()</code> on jad executable with selected
	 * options.
	 * 
	 * @see IDecompiler#decompile(String, String, String)
	 */
	public void decompile( String root, String packege, String className )
	{
		start = System.currentTimeMillis( );
		log = ""; //$NON-NLS-1$
		source = ""; //$NON-NLS-1$
		File workingDir = new File( root + "/" + packege ); //$NON-NLS-1$

		final String classPathStr = new File( workingDir, className ).getAbsolutePath( );

		boolean includeLineNumbers = false;
		boolean stretchLines = false;
		if ( UIUtil.isDebugPerspective( )
				|| JavaDecompilerPlugin.getDefault( ).isDisplayLineNumber( ) )
		{
			includeLineNumbers = true;
			stretchLines = true;
		}

		DecompilationOptions decompilationOptions = new DecompilationOptions( );

		DecompilerSettings settings = DecompilerSettings.javaDefaults( );
		settings.setTypeLoader( new com.strobel.assembler.InputTypeLoader( ) );
		settings.setForceExplicitImports( true );

		decompilationOptions.setSettings( settings );
		decompilationOptions.setFullDecompilation( true );

		MetadataSystem metadataSystem = new NoRetryMetadataSystem( decompilationOptions.getSettings( )
				.getTypeLoader( ) );
		metadataSystem.setEagerMethodLoadingEnabled( false );

		TypeReference type = metadataSystem.lookupType( classPathStr );

		TypeDefinition resolvedType;
		if ( ( type == null ) || ( ( resolvedType = type.resolve( ) ) == null ) )
		{
			System.err.printf( "!!! ERROR: Failed to load class %s.\n", //$NON-NLS-1$
					new Object[]{
						classPathStr
					} );
			return;
		}

		DeobfuscationUtilities.processType( resolvedType );

		String property = "java.io.tmpdir"; //$NON-NLS-1$
		String tempDir = System.getProperty( property );
		File classFile = new File( tempDir, System.currentTimeMillis( )
				+ className );
		Writer writer = null;
		try
		{
			writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( classFile ) ) );

			PlainTextOutput output = new PlainTextOutput( writer );

			output.setUnicodeOutputEnabled( decompilationOptions.getSettings( )
					.isUnicodeOutputEnabled( ) );

			TypeDecompilationResults results = decompilationOptions.getSettings( )
					.getLanguage( )
					.decompileType( resolvedType, output, decompilationOptions );

			writer.flush( );
			writer.close( );

			writer = null;

			List lineNumberPositions = results.getLineNumberPositions( );

			if ( includeLineNumbers || stretchLines )
			{
				EnumSet lineNumberOptions = EnumSet.noneOf( LineNumberFormatter.LineNumberOption.class );

				if ( includeLineNumbers )
				{
					lineNumberOptions.add( LineNumberFormatter.LineNumberOption.LEADING_COMMENTS );
				}

				if ( stretchLines )
				{
					lineNumberOptions.add( LineNumberFormatter.LineNumberOption.STRETCHED );
				}

				LineNumberFormatter lineFormatter = new LineNumberFormatter( classFile,
						lineNumberPositions,
						lineNumberOptions );

				lineFormatter.reformatFile( );
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		finally
		{
			if ( writer != null )
			{
				try
				{
					writer.close( );
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
			}
		}

		source = UnicodeUtil.decode( FileUtil.getContent( classFile ) );

		classFile.delete( );

		Pattern wp = Pattern.compile( "/\\*.+?\\*/", Pattern.DOTALL ); //$NON-NLS-1$
		Matcher m = wp.matcher( source );
		while ( m.find( ) )
		{
			if ( m.group( ).matches( "/\\*\\s*\\d*\\s*\\*/" ) ) //$NON-NLS-1$
				continue;
			String group = m.group( );
			group = group.replace( "/*", "" ); //$NON-NLS-1$ //$NON-NLS-2$
			group = group.replace( "*/", "" ); //$NON-NLS-1$ //$NON-NLS-2$
			group = group.replace( "*", "" ); //$NON-NLS-1$ //$NON-NLS-2$
			if ( log.length( ) > 0 )
				log += "\n"; //$NON-NLS-1$
			log += group;

			source = source.replace( m.group( ), "" ); //$NON-NLS-1$
		}

		time = System.currentTimeMillis( ) - start;
	}

	/**
	 * Jad doesn't support decompilation from archives. This methods extracts
	 * request class file from the specified archive into temp directory and
	 * then calls <code>decompile</code>.
	 * 
	 * @see IDecompiler#decompileFromArchive(String, String, String)
	 */
	public void decompileFromArchive( String archivePath, String packege,
			String className )
	{
		start = System.currentTimeMillis( );
		File workingDir = new File( JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( )
				.getString( JavaDecompilerPlugin.TEMP_DIR )
				+ "/" //$NON-NLS-1$
				+ System.currentTimeMillis( ) );

		try
		{
			workingDir.mkdirs( );
			JarClassExtractor.extract( archivePath,
					packege,
					className,
					true,
					workingDir.getAbsolutePath( ) );
			decompile( workingDir.getAbsolutePath( ), "", className ); //$NON-NLS-1$
		}
		catch ( Exception e )
		{
			JavaDecompilerPlugin.logError( e, e.getMessage( ) );
			return;
		}
		finally
		{
			deltree( workingDir );
		}
	}

	void deltree( File root )
	{
		if ( root.isFile( ) )
		{
			root.delete( );
			return;
		}

		File[] children = root.listFiles( );
		for ( int i = 0; i < children.length; i++ )
		{
			deltree( children[i] );
		}

		root.delete( );
	}

	public long getDecompilationTime( )
	{
		return time;
	}

	public List getExceptions( )
	{
		return Collections.EMPTY_LIST;
	}

	/**
	 * @see IDecompiler#getLog()
	 */
	public String getLog( )
	{
		return log;
	}

	/**
	 * @see IDecompiler#getSource()
	 */
	public String getSource( )
	{
		return source;
	}

	public String getDecompilerType( )
	{
		return ProcyonDecompilerPlugin.decompilerType;
	}

	public String removeComment( String source )
	{
		return source;
	}

	public boolean supportLevel( int level )
	{
		return true;
	}

	public boolean supportDebugLevel( int level )
	{
		return true;
	}

	public boolean supportDebug( )
	{
		return true;
	}
}