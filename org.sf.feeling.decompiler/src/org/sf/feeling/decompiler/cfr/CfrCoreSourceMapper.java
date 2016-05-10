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

package org.sf.feeling.decompiler.cfr;

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
import org.sf.feeling.decompiler.jad.IDecompiler;
import org.sf.feeling.decompiler.util.DecompilerOutputUtil;
import org.sf.feeling.decompiler.util.SortMemberUtil;
import org.sf.feeling.decompiler.util.UIUtil;

public class CfrCoreSourceMapper extends DecompilerSourceMapper
{

	private IDecompiler decompiler;

	public CfrCoreSourceMapper( )
	{
		super( new Path( "." ), "", new HashMap( ) ); // per //$NON-NLS-1$ //$NON-NLS-2$
														// Rene's e-mail
		decompiler = new CfrDecompiler( );
	}

	public char[] findSource( IType type, IBinaryType info )
	{
		IPreferenceStore prefs = JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( );
		boolean always = prefs
				.getBoolean( JavaDecompilerPlugin.IGNORE_EXISTING );

		Collection exceptions = new LinkedList( );
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

		String pkg = type.getPackageFragment( ).getElementName( ).replace( '.',
				'/' );
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
			JavaDecompilerPlugin.getDefault( )
					.displayLineNumber( Boolean.TRUE );
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

		String code = JavaDecompilerClassFileEditor.MARK
				+ "\r\n" //$NON-NLS-1$
				+ decompiler.getSource( );

		boolean showReport = prefs
				.getBoolean( JavaDecompilerPlugin.PREF_DISPLAY_METADATA );
		if ( !showReport )
		{
			code = removeComment( code );
		}

		boolean showLineNumber = prefs
				.getBoolean( JavaDecompilerPlugin.PREF_DISPLAY_LINE_NUMBERS );
		boolean align = prefs.getBoolean( JavaDecompilerPlugin.ALIGN );
		if ( ( showLineNumber && align ) || UIUtil.isDebugPerspective( ) )
		{
			if ( showReport )
				code = removeComment( code );
			DecompilerOutputUtil decompilerOutputUtil = new DecompilerOutputUtil(
					DecompilerType.JAD, code );
			code = decompilerOutputUtil.realign( );
		}

		StringBuffer source = new StringBuffer( );

		if ( !UIUtil.isDebugPerspective( ) )
		{
			boolean useSorter = prefs
					.getBoolean( JavaDecompilerPlugin.USE_ECLIPSE_SORTER );
			if ( useSorter )
			{
				className = new String( info.getName( ) );
				fullName = new String( info.getFileName( ) );
				if ( fullName.lastIndexOf( className ) != -1 )
				{
					className = fullName
							.substring( fullName.lastIndexOf( className ) );
				}
				else
				{

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
				source.append( location ).append( "\n" ); //$NON-NLS-1$
				source.append( "\tTotal time: " ) //$NON-NLS-1$
						.append( decompiler.getDecompilationTime( ) )
						.append( " ms\n" ); //$NON-NLS-1$
				source.append( "\t"
						+ decompiler.getLog( )
								.replaceAll( "\t", "" )
								.replaceAll( "\n\\s*", "\n\t" ) );
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
		Pattern wp = Pattern.compile( "/\\*\\s+.+?\\s+\\*/", Pattern.DOTALL );
		Matcher m = wp.matcher( code );
		return m.replaceAll( "" );
	}

	private void logExceptions( Collection exceptions, StringBuffer buffer )
	{
		if ( !exceptions.isEmpty( ) )
		{
			buffer.append( "\n\tCaught exceptions:" ); //$NON-NLS-1$
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
			JavaDecompilerPlugin.getDefault( )
					.displayLineNumber( Boolean.TRUE );
		}

		decompiler.decompile( file.getParentFile( ).getAbsolutePath( ),
				"", //$NON-NLS-1$
				file.getName( ) );

		if ( displayNumber != null )
		{
			JavaDecompilerPlugin.getDefault( )
					.displayLineNumber( displayNumber );
		}

		if ( decompiler.getSource( ) == null
				|| decompiler.getSource( ).length( ) == 0 )
			return null;

		String code = JavaDecompilerClassFileEditor.MARK
				+ "\r\n" //$NON-NLS-1$
				+ decompiler.getSource( );

		boolean showReport = prefs
				.getBoolean( JavaDecompilerPlugin.PREF_DISPLAY_METADATA );
		if ( !showReport )
		{
			code = removeComment( code );
		}

		boolean showLineNumber = prefs
				.getBoolean( JavaDecompilerPlugin.PREF_DISPLAY_LINE_NUMBERS );
		boolean align = prefs.getBoolean( JavaDecompilerPlugin.ALIGN );
		if ( ( showLineNumber && align ) || UIUtil.isDebugPerspective( ) )
		{
			if ( showReport )
				code = removeComment( code );
			DecompilerOutputUtil decompilerOutputUtil = new DecompilerOutputUtil(
					DecompilerType.JAD, code );
			code = decompilerOutputUtil.realign( );
		}

		StringBuffer source = new StringBuffer( );

		if ( !UIUtil.isDebugPerspective( ) )
		{
			source.append( formatSource( code ) );

			if ( showReport )
			{
				String location = "\tDecompiled from: " //$NON-NLS-1$
						+ file.getAbsolutePath( );

				source.append( "\n\n/*" ); //$NON-NLS-1$
				source.append( "\n\tDECOMPILATION REPORT\n\n" ); //$NON-NLS-1$
				source.append( location ).append( "\n" ); //$NON-NLS-1$
				source.append( "\tTotal time: " ) //$NON-NLS-1$
						.append( decompiler.getDecompilationTime( ) )
						.append( " ms\n" ); //$NON-NLS-1$
				source.append( "\t"
						+ decompiler.getLog( )
								.replaceAll( "\t", "" )
								.replaceAll( "\n\\s*", "\n\t" ) );
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