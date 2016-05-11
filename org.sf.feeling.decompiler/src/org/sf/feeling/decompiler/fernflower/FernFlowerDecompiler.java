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

package org.sf.feeling.decompiler.fernflower;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.editor.DecompilerType;
import org.sf.feeling.decompiler.editor.IDecompiler;
import org.sf.feeling.decompiler.jad.JarClassExtractor;
import org.sf.feeling.decompiler.util.FileUtil;
import org.sf.feeling.decompiler.util.UIUtil;

public class FernFlowerDecompiler implements IDecompiler
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

		final String classPathStr = new File( workingDir, className )
				.getAbsolutePath( );
		Map<String, Object> mapOptions = new HashMap<String, Object>( );

		mapOptions.put( IFernflowerPreferences.REMOVE_SYNTHETIC, "1" );
		mapOptions.put( IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES,
				"1" );
		mapOptions.put( IFernflowerPreferences.REMOVE_SYNTHETIC, "1" );
		mapOptions.put( IFernflowerPreferences.LOG_LEVEL,
				IFernflowerLogger.Severity.ERROR.name( ) );

		if ( UIUtil.isDebugPerspective( )
				|| JavaDecompilerPlugin.getDefault( ).isDisplayLineNumber( ) )
		{
			mapOptions.put( IFernflowerPreferences.DUMP_ORIGINAL_LINES, "1" );
			mapOptions.put( IFernflowerPreferences.BYTECODE_SOURCE_MAPPING,
					"1" );
		}
		ConsoleDecompiler decompiler = new ConsoleDecompiler( workingDir,
				mapOptions );
		decompiler.addSpace( new File( classPathStr ), true );
		decompiler.decompileContext( );

		File classFile = new File( workingDir,
				className.replaceAll( "(?i)\\.class", ".java" ) );

		source = FileUtil.getContent( classFile );

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
		return DecompilerType.FernFlower;
	}

}