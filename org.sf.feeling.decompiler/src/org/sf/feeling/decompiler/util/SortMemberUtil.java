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

package org.sf.feeling.decompiler.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.corext.codemanipulation.SortMembersOperation;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;

public class SortMemberUtil
{

	public static String getContent( File file )
	{
		if ( file == null || !file.exists( ) )
			return null;
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream( 4096 );
			byte[] tmp = new byte[4096];
			InputStream is = new BufferedInputStream( new FileInputStream( file ) );
			while ( true )
			{
				int r = is.read( tmp );
				if ( r == -1 )
					break;
				out.write( tmp, 0, r );
			}
			byte[] bytes = out.toByteArray( );
			is.close( );
			out.close( );
			String content = new String( bytes );
			return content.trim( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		return null;
	}

	public static String sortMember( String packageName, String className,
			String code )
	{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace( ).getRoot( );
		IProject project = root.getProject( ".decompiler" //$NON-NLS-1$
				+ System.currentTimeMillis( ) );
		try
		{
			project.create( null );
			project.open( null );
			IProjectDescription description = project.getDescription( );
			String[] natures = description.getNatureIds( );
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy( natures, 0, newNatures, 0, natures.length );
			newNatures[natures.length] = JavaCore.NATURE_ID;
			description.setNatureIds( newNatures );
			project.setDescription( description, null );
		}
		catch ( CoreException e1 )
		{
			e1.printStackTrace( );
		}

		if ( !project.isOpen( ) )
			return code;

		className = className.replaceAll( "(?i).class", //$NON-NLS-1$
				System.currentTimeMillis( ) + ".java" ); //$NON-NLS-1$

		IJavaProject javaProject = JavaCore.create( project );

		try
		{
			List entries = new ArrayList( );
			IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall( );
			LibraryLocation[] locations = JavaRuntime.getLibraryLocations( vmInstall );
			for ( int i = 0; i < locations.length; i++ )
			{
				LibraryLocation element = locations[i];
				entries.add( JavaCore.newLibraryEntry( element.getSystemLibraryPath( ),
						null,
						null ) );
			}
			// add libs to project class path
			javaProject.setRawClasspath( (IClasspathEntry[]) entries.toArray( new IClasspathEntry[entries.size( )] ),
					null );

			IFolder sourceFolder = project.getFolder( "src" ); //$NON-NLS-1$
			sourceFolder.create( false, true, null );

			IPackageFragmentRoot codeGenFolder = javaProject.getPackageFragmentRoot( sourceFolder );
			IClasspathEntry[] oldEntries = javaProject.getRawClasspath( );
			IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
			System.arraycopy( oldEntries, 0, newEntries, 0, oldEntries.length );
			newEntries[oldEntries.length] = JavaCore.newSourceEntry( codeGenFolder.getPath( ) );
			javaProject.setRawClasspath( newEntries, null );

			IPackageFragment fragment = javaProject.getPackageFragmentRoot( sourceFolder )
					.createPackageFragment( packageName, false, null );

			File locationFile = new File( sourceFolder.getFolder( packageName.replace( '.',
					'/' ) )
					.getFile( className )
					.getLocation( )
					.toString( ) );

			if ( !locationFile.getParentFile( ).exists( ) )
				locationFile.getParentFile( ).mkdirs( );

			PrintWriter writer = new PrintWriter( new BufferedWriter( new FileWriter( locationFile,
					false ) ) );
			writer.println( code );
			writer.close( );

			project.refreshLocal( IResource.DEPTH_INFINITE, null );

			ICompilationUnit iCompilationUnit = fragment.getCompilationUnit( className );
			iCompilationUnit.becomeWorkingCopy( null );
			new SortMembersOperation( iCompilationUnit, null, true ).run( null );
			iCompilationUnit.commitWorkingCopy( true, null );
			iCompilationUnit.save( null, true );
			String content = getContent( locationFile );
			iCompilationUnit.delete( true, null );
			if ( content != null )
				code = content;
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		try
		{
			if ( project != null )
				project.delete( true, true, null );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		return code;
	}
}
