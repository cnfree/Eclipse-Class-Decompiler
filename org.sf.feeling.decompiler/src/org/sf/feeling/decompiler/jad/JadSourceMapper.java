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

package org.sf.feeling.decompiler.jad;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Path;
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
import org.sf.feeling.decompiler.util.SortMemberUtil;
import org.sf.feeling.decompiler.util.UIUtil;

public class JadSourceMapper extends DecompilerSourceMapper
{

	private IJadDecompiler decompiler;

	public JadSourceMapper( )
	{
		super( new Path( "." ), "", new HashMap( ) ); // per Rene's e-mail //$NON-NLS-1$ //$NON-NLS-2$
		decompiler = new JadDecompiler( );
	}

	public char[] findSource( IType type, IBinaryType info )
	{
		IPreferenceStore prefs = JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( );
		boolean always = prefs.getBoolean( JavaDecompilerPlugin.IGNORE_EXISTING );

		Collection exceptions = new LinkedList( );
		IPackageFragment pkgFrag = type.getPackageFragment( );
		IPackageFragmentRoot root = (IPackageFragmentRoot) pkgFrag.getParent( );

		if ( originalSourceMapper.containsKey( root ) && !always )
		{
			char[] attachedSource = ( (SourceMapper) originalSourceMapper.get( root ) ).findSource( type,
					info );
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

				SourceMapper sourceMapper = pfr.getSourceMapper( );
				if ( sourceMapper != null
						&& !always
						&& !( sourceMapper instanceof DecompilerSourceMapper ) )
				{
					char[] attachedSource = sourceMapper.findSource( type, info );
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

		String pkg = type.getPackageFragment( )
				.getElementName( )
				.replace( '.', '/' );
		String location = "\tDecompiled from: "; //$NON-NLS-1$

		String className = new String( info.getName( ) );
		String fullName = new String( info.getFileName( ) );
		className = fullName.substring( fullName.lastIndexOf( className ) );

		int p = className.lastIndexOf( '/' );
		className = className.substring( p + 1 );

		Boolean displayNumber = null;
		if ( UIUtil.isDebugPerspective( ) )
		{
			displayNumber = JavaDecompilerPlugin.getDefault( )
					.isDisplayLineNumber( );
			JavaDecompilerPlugin.getDefault( ).displayLineNumber( Boolean.TRUE );
		}

		if ( root.isArchive( ) )
		{
			String archivePath = getArchivePath( root );
			location += archivePath;
			decompiler.decompileFromArchive( archivePath, pkg, className );
		}
		else
		{
			try
			{
				location += root.getUnderlyingResource( )
						.getLocation( )
						.toOSString( )
						+ "/" //$NON-NLS-1$
						+ pkg
						+ "/" //$NON-NLS-1$
						+ className;
				decompiler.decompile( root.getUnderlyingResource( )
						.getLocation( )
						.toOSString( ), pkg, className );
			}
			catch ( JavaModelException e )
			{
				exceptions.add( e );
			}
		}

		if ( displayNumber != null )
		{
			JavaDecompilerPlugin.getDefault( )
					.displayLineNumber( displayNumber );
		}

		if ( decompiler.getSource( ) == null
				|| decompiler.getSource( ).length( ) == 0 )
			return null;

		String code = JavaDecompilerClassFileEditor.MARK + "\r\n" //$NON-NLS-1$
				+ decompiler.getSource( );

		boolean showReport = prefs.getBoolean( JavaDecompilerPlugin.PREF_DISPLAY_METADATA );
		if ( !showReport )
		{
			code = removeComment( code );
		}

		boolean showLineNumber = prefs.getBoolean( JavaDecompilerPlugin.PREF_DISPLAY_LINE_NUMBERS );
		boolean align = prefs.getBoolean( JavaDecompilerPlugin.ALIGN );
		if ( ( showLineNumber && align ) || UIUtil.isDebugPerspective( ) )
		{
			if ( showReport )
				code = removeComment( code );
			DecompilerOutputUtil decompilerOutputUtil = new DecompilerOutputUtil( DecompilerType.JAD,
					code );
			code = decompilerOutputUtil.realign( );
		}

		StringBuffer source = new StringBuffer( );

		if ( !UIUtil.isDebugPerspective( ) )
		{
			boolean useSorter = prefs.getBoolean( JavaDecompilerPlugin.USE_ECLIPSE_SORTER );
			if ( useSorter )
			{
				code = SortMemberUtil.sortMember( type.getPackageFragment( )
						.getElementName( ), className, code );
			}

			source.append( formatSource( code ) );

			if ( showReport )
			{
				source.append( "\n\n/*" ); //$NON-NLS-1$
				source.append( "\n\tDECOMPILATION REPORT\n\n" ); //$NON-NLS-1$
				source.append( location ).append( "\n" ); //$NON-NLS-1$
				source.append( "\tTotal time: " ) //$NON-NLS-1$
						.append( decompiler.getDecompilationTime( ) )
						.append( " ms\n" ); //$NON-NLS-1$
				source.append( decompiler.getLog( ) );
				exceptions.addAll( decompiler.getExceptions( ) );
				logExceptions( exceptions, source );
				source.append( "\n*/" ); //$NON-NLS-1$
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

	private String removeComment( String code )
	{
		String[] spilts = code.replaceAll( "\r\n", "\n" ).split( "\n" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		StringBuffer buffer = new StringBuffer( );
		for ( int i = 0; i < spilts.length; i++ )
		{
			if ( i > 0 && i < 5 )
				continue;
			String string = spilts[i];
			Pattern pattern = Pattern.compile( "\\s*/\\*\\s*\\S*\\*/", //$NON-NLS-1$
					Pattern.CASE_INSENSITIVE );
			Matcher matcher = pattern.matcher( string );
			if ( matcher.find( ) )
			{
				if ( matcher.start( ) == 0 )
				{
					buffer.append( string ).append( "\r\n" ); //$NON-NLS-1$
					continue;
				}
			}

			boolean refer = false;

			pattern = Pattern.compile( "\\s*// Referenced", //$NON-NLS-1$
					Pattern.CASE_INSENSITIVE );
			matcher = pattern.matcher( string );
			if ( matcher.find( ) )
			{
				refer = true;

				while ( true )
				{
					i++;
					if ( spilts[i].trim( ).startsWith( "//" ) ) //$NON-NLS-1$
					{
						continue;
					}
					else if ( i >= spilts.length )
					{
						break;
					}
					else
					{
						i--;
						break;
					}
				}
			}

			if ( !refer )
				buffer.append( string + "\r\n" ); //$NON-NLS-1$
		}
		return buffer.toString( );
	}

	private void logExceptions( Collection exceptions, StringBuffer buffer )
	{
		buffer.append( "\tCaught exceptions:" ); //$NON-NLS-1$
		if ( exceptions == null || exceptions.size( ) == 0 )
			return; // nothing to do
		buffer.append( "\n" ); //$NON-NLS-1$
		StringWriter stackTraces = new StringWriter( );
		PrintWriter stackTracesP = new PrintWriter( stackTraces );

		Iterator i = exceptions.iterator( );
		while ( i.hasNext( ) )
		{
			( (Exception) i.next( ) ).printStackTrace( stackTracesP );
			stackTracesP.println( "" ); //$NON-NLS-1$
		}

		stackTracesP.flush( );
		stackTracesP.close( );
		buffer.append( stackTraces.toString( ) );
	}

	public String decompile( File file )
	{
		IPreferenceStore prefs = JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( );

		Boolean displayNumber = null;
		if ( UIUtil.isDebugPerspective( ) )
		{
			displayNumber = JavaDecompilerPlugin.getDefault( )
					.isDisplayLineNumber( );
			JavaDecompilerPlugin.getDefault( ).displayLineNumber( Boolean.TRUE );
		}

		decompiler.decompile( file.getParentFile( ).getAbsolutePath( ),
				"", file.getName( ) ); //$NON-NLS-1$

		if ( displayNumber != null )
		{
			JavaDecompilerPlugin.getDefault( )
					.displayLineNumber( displayNumber );
		}

		if ( decompiler.getSource( ) == null
				|| decompiler.getSource( ).length( ) == 0 )
			return null;

		String code = JavaDecompilerClassFileEditor.MARK + "\r\n" //$NON-NLS-1$
				+ decompiler.getSource( );

		boolean showReport = prefs.getBoolean( JavaDecompilerPlugin.PREF_DISPLAY_METADATA );
		if ( !showReport )
		{
			code = removeComment( code );
		}

		boolean showLineNumber = prefs.getBoolean( JavaDecompilerPlugin.PREF_DISPLAY_LINE_NUMBERS );
		boolean align = prefs.getBoolean( JavaDecompilerPlugin.ALIGN );
		if ( ( showLineNumber && align ) || UIUtil.isDebugPerspective( ) )
		{
			if ( showReport )
				code = removeComment( code );
			DecompilerOutputUtil decompilerOutputUtil = new DecompilerOutputUtil( DecompilerType.JAD,
					code );
			code = decompilerOutputUtil.realign( );
		}

		StringBuffer source = new StringBuffer( );

		if ( !UIUtil.isDebugPerspective( ) )
		{
			source.append( formatSource( code ) );

			if ( showReport )
			{
				String location = "\tDecompiled from: " + file.getAbsolutePath( ); //$NON-NLS-1$

				source.append( "\n\n/*" ); //$NON-NLS-1$
				source.append( "\n\tDECOMPILATION REPORT\n\n" ); //$NON-NLS-1$
				source.append( location ).append( "\n" ); //$NON-NLS-1$
				source.append( "\tTotal time: " ) //$NON-NLS-1$
						.append( decompiler.getDecompilationTime( ) )
						.append( " ms\n" ); //$NON-NLS-1$
				source.append( decompiler.getLog( ) );
				Collection exceptions = new LinkedList( );
				exceptions.addAll( decompiler.getExceptions( ) );
				logExceptions( exceptions, source );
				source.append( "\n*/" ); //$NON-NLS-1$
			}
		}
		else
		{
			source.append( code );
		}

		return source.toString( );
	}

}