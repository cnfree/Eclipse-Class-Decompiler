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

package org.sf.feeling.decompiler.jdcore;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jd.ide.eclipse.editors.JDSourceMapper;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.core.SourceMapper;
import org.eclipse.jface.preference.IPreferenceStore;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.editor.DecompilerSourceMapper;
import org.sf.feeling.decompiler.editor.DecompilerType;
import org.sf.feeling.decompiler.editor.JavaDecompilerClassFileEditor;
import org.sf.feeling.decompiler.util.DecompilerOutputUtil;
import org.sf.feeling.decompiler.util.FileUtil;
import org.sf.feeling.decompiler.util.SortMemberUtil;
import org.sf.feeling.decompiler.util.UIUtil;

public class JDCoreSourceMapper extends JDSourceMapper
{

	public char[] findSource( IPath path, String javaClassPath )
	{
		if ( path == null )
			return null;

		IResource resource = ResourcesPlugin.getWorkspace( )
				.getRoot( )
				.findMember( path );
		path = ( resource == null ) ? path : resource.getRawLocation( );
		File baseFile = path.toFile( );

		if ( !checkBaseFile( baseFile, javaClassPath ) )
			return null;

		Boolean displayNumber = null;
		try
		{
			loadLibrary( );
			String baseName = baseFile.getAbsolutePath( );
			if ( UIUtil.isDebugPerspective( ) )
			{
				displayNumber = JavaDecompilerPlugin.getDefault( )
						.isDisplayLineNumber( );
				JavaDecompilerPlugin.getDefault( )
						.displayLineNumber( Boolean.TRUE );
			}
			String result = decompile( baseName, javaClassPath );

			if ( displayNumber != null )
			{
				JavaDecompilerPlugin.getDefault( )
						.displayLineNumber( displayNumber );
			}

			if ( result != null )
				return result.toCharArray( );
		}
		catch ( Exception e )
		{
			JavaDecompilerPlugin.getDefault( )
					.getLog( )
					.log( new Status( Status.ERROR,
							JavaDecompilerPlugin.PLUGIN_ID,
							0,
							e.getMessage( ),
							e ) );
		}
		finally
		{
			if ( displayNumber != null )
			{
				JavaDecompilerPlugin.getDefault( )
						.displayLineNumber( displayNumber );
			}
		}

		return null;
	}

	private static boolean checkBaseFile( File baseFile, String javaClassPath )
	{
		if ( !baseFile.exists( ) )
			return false;

		if ( baseFile.isDirectory( ) )
		{
			File file = new File( baseFile, javaClassPath );
			return file.exists( ) && file.isFile( );
		}

		if ( baseFile.isFile( ) )
		{
			String absolutePath = baseFile.getAbsolutePath( );

			if ( endsWithIgnoreCase( absolutePath, JAR_SUFFIX )
					|| endsWithIgnoreCase( absolutePath, ZIP_SUFFIX ) )
			{
				ZipFile zipFile = null;

				try
				{
					String zipEntryPath = javaClassPath
							.replace( File.separatorChar, '/' );
					zipFile = new ZipFile( baseFile );
					ZipEntry zipEntry = zipFile.getEntry( zipEntryPath );
					return ( zipEntry != null ) && ( !zipEntry.isDirectory( ) );
				}
				catch ( IOException e )
				{
					JavaDecompilerPlugin.getDefault( )
							.getLog( )
							.log( new Status( Status.ERROR,
									JavaDecompilerPlugin.PLUGIN_ID,
									0,
									e.getMessage( ),
									e ) );
				}
				finally
				{
					if ( zipFile != null )
						try
						{
							zipFile.close( );
						}
						catch ( IOException ignore )
						{
						}
				}
			}
		}

		return false;
	}

	private static boolean endsWithIgnoreCase( String s, String suffix )
	{
		int suffixLength = suffix.length( );
		int index = s.length( ) - suffixLength;
		return ( s.regionMatches( true, index, suffix, 0, suffixLength ) );
	}

	public JDCoreSourceMapper( )
	{
		super( new Path( "." ), "", new HashMap( ) ); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public char[] findSource( IType type, IBinaryType info )
	{
		long startTime = System.currentTimeMillis( );
		IPreferenceStore prefs = JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( );
		boolean always = prefs
				.getBoolean( JavaDecompilerPlugin.IGNORE_EXISTING );

		IPackageFragment pkgFrag = type.getPackageFragment( );
		IPackageFragmentRoot root = (IPackageFragmentRoot) pkgFrag.getParent( );

		if ( originalSourceMapper.containsKey( root ) && !always )
		{
			char[] attachedSource = ( (SourceMapper) originalSourceMapper
					.get( root ) ).findSource( type, info );
			if ( attachedSource != null )
			{
				isAttachedSource = true;
				return attachedSource;
			}
		}

		if ( info == null )
		{
			return null;
		}

		try
		{
			if ( root instanceof PackageFragmentRoot )
			{
				PackageFragmentRoot pfr = (PackageFragmentRoot) root;

				// try
				SourceMapper sourceMapper = pfr.getSourceMapper( );
				if ( sourceMapper != null
						&& !always
						&& !( sourceMapper instanceof DecompilerSourceMapper ) )
				{
					char[] attachedSource = sourceMapper.findSource( type,
							info );
					if ( attachedSource != null )
					{
						isAttachedSource = true;
						return attachedSource;
					}
				}

				if ( !originalSourceMapper.containsKey( root ) )
				{
					originalSourceMapper.put( root, sourceMapper );
				}

				if ( sourceMapper != this )
				{
					pfr.setSourceMapper( this );
				}
			}
		}
		catch ( JavaModelException e )
		{
			JavaDecompilerPlugin.logError( e, "Could not set source mapper." ); //$NON-NLS-1$
		}

		isAttachedSource = false;

		IPath classePath = root.getPath( );

		String className = new String( info.getName( ) );
		String fullName = new String( info.getFileName( ) );
		if ( fullName.lastIndexOf( className ) != -1 )
		{
			className = fullName.substring( fullName.lastIndexOf( className ) );
		}
		else
		{

		}

		char[] returnSource = findSource( classePath, className );
		if ( returnSource == null )
			return null;
		String code = JavaDecompilerClassFileEditor.MARK
				+ "\r\n" //$NON-NLS-1$
				+ new String( returnSource );

		boolean showReport = prefs
				.getBoolean( JavaDecompilerPlugin.PREF_DISPLAY_METADATA );

		boolean showLineNumber = prefs
				.getBoolean( JavaDecompilerPlugin.PREF_DISPLAY_LINE_NUMBERS );
		boolean align = prefs.getBoolean( JavaDecompilerPlugin.ALIGN );
		if ( ( showLineNumber && align ) || UIUtil.isDebugPerspective( ) )
		{
			DecompilerOutputUtil decompilerOutputUtil = new DecompilerOutputUtil(
					DecompilerType.JDCORE, code );
			code = decompilerOutputUtil.realign( );
		}

		StringBuffer source = new StringBuffer( );

		if ( !UIUtil.isDebugPerspective( ) )
		{
			boolean useSorter = prefs
					.getBoolean( JavaDecompilerPlugin.USE_ECLIPSE_SORTER );

			String report = null;

			if ( useSorter )
			{
				Pattern wp = Pattern.compile( "/\\*\\s+.+?\\s+\\*/",
						Pattern.DOTALL );
				Matcher m = wp.matcher( code );
				if ( m.find( ) )
				{
					report = m.group( );
					report = report.replace( "/* ", "\t" );
					report = report.replace( " */", "" );
					report = report.replace( " * ", "\t" );
				}

				code = SortMemberUtil.sortMember(
						type.getPackageFragment( ).getElementName( ),
						className,
						code );
			}

			source.append( formatSource( code ) );

			if ( showReport )
			{
				source.append( "\n\n/*" ); //$NON-NLS-1$
				source.append( "\n\tDECOMPILATION REPORT\n\n" ); //$NON-NLS-1$
				source.append( "\tTotal time: " ) //$NON-NLS-1$
						.append( ""
								+ ( System.currentTimeMillis( ) - startTime ) )
						.append( " ms\n" ); //$NON-NLS-1$
				if ( report != null )
				{
					source.append( report );
				}
				source.append( "*/" ); //$NON-NLS-1$
			}
		}
		else
		{
			source.append( code );
		}

		if ( originalSourceMapper.containsKey( root ) )
		{
			( (SourceMapper) originalSourceMapper.get( root ) ).mapSource( type,
					source.toString( ).toCharArray( ),
					null );
		}
		return source.toString( ).toCharArray( );
	}

	public String decompile( File file )
	{
		long startTime = System.currentTimeMillis( );
		IPreferenceStore prefs = JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( );
		String returnSource = ""; //$NON-NLS-1$
		Boolean displayNumber = null;
		try
		{
			loadLibrary( );
			File zipFile = new File( System.getProperty( "java.io.tmpdir" ), //$NON-NLS-1$
					file.getName( ).replaceAll( "(?i)\\.class", ".jar" ) ); //$NON-NLS-1$ //$NON-NLS-2$
			String zipFileName = zipFile.getAbsolutePath( );
			FileUtil.zipFile( file, zipFileName );
			if ( UIUtil.isDebugPerspective( ) )
			{
				displayNumber = JavaDecompilerPlugin.getDefault( )
						.isDisplayLineNumber( );
				JavaDecompilerPlugin.getDefault( )
						.displayLineNumber( Boolean.TRUE );
			}
			returnSource = decompile( zipFileName, file.getName( ) );
			zipFile.delete( );

			if ( displayNumber != null )
			{
				JavaDecompilerPlugin.getDefault( )
						.displayLineNumber( displayNumber );
			}

			String code = JavaDecompilerClassFileEditor.MARK
					+ "\r\n" //$NON-NLS-1$
					+ new String( returnSource );

			if ( !UIUtil.isDebugPerspective( ) )
			{
				boolean showLineNumber = prefs.getBoolean(
						JavaDecompilerPlugin.PREF_DISPLAY_LINE_NUMBERS );
				boolean align = prefs.getBoolean( JavaDecompilerPlugin.ALIGN );
				if ( showLineNumber && align )
				{
					DecompilerOutputUtil decompilerOutputUtil = new DecompilerOutputUtil(
							DecompilerType.JDCORE, code );
					code = decompilerOutputUtil.realign( );
				}

				String report = null;

				Pattern wp = Pattern.compile( "/\\*\\s+.+?\\s+\\*/",
						Pattern.DOTALL );
				Matcher m = wp.matcher( code );
				if ( m.find( ) )
				{
					report = m.group( );
					report = report.replace( "/* ", "\t" );
					report = report.replace( " */", "" );
					report = report.replace( " * ", "\t" );

					code = m.replaceAll( "" );
				}

				StringBuffer source = new StringBuffer( );
				source.append( formatSource( code ) );

				boolean showReport = prefs.getBoolean(
						JavaDecompilerPlugin.PREF_DISPLAY_METADATA );
				if ( showReport )
				{
					source.append( "\n\n/*" ); //$NON-NLS-1$
					source.append( "\n\tDECOMPILATION REPORT\n\n" ); //$NON-NLS-1$
					source.append( "\tTotal time: " ) //$NON-NLS-1$
							.append( ""
									+ ( System.currentTimeMillis( )
											- startTime ) )
							.append( " ms\n" ); //$NON-NLS-1$
					if ( report != null )
					{
						source.append( report );
					}
					source.append( "*/" ); //$NON-NLS-1$
				}

				return source.toString( );
			}
			else
				return code;
		}
		catch ( Exception e )
		{
			JavaDecompilerPlugin.getDefault( )
					.getLog( )
					.log( new Status( Status.ERROR,
							JavaDecompilerPlugin.PLUGIN_ID,
							0,
							e.getMessage( ),
							e ) );
		}
		finally
		{
			if ( displayNumber != null )
			{
				JavaDecompilerPlugin.getDefault( )
						.displayLineNumber( displayNumber );
			}
		}
		return null;
	}
}
