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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
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
import org.sf.feeling.decompiler.JavaDecompilerPlugin;

public class SortMemberUtil
{

	private static IPackageFragmentRoot decompilerSourceFolder = null;

	public static String sortMember( String packageName, String className,
			String code )
	{

		IPackageFragmentRoot sourceRootFragment = getDecompilerSourceFolder( );
		if ( sourceRootFragment == null )
			return code;

		try
		{
			if ( !sourceRootFragment.getJavaProject( ).isOpen( ) )
			{
				sourceRootFragment.getJavaProject( ).open( null );
			}

			if ( !sourceRootFragment.getPackageFragment( packageName )
					.exists( ) )
			{
				sourceRootFragment.createPackageFragment( packageName,
						false,
						null );
			}

			IPackageFragment packageFragment = sourceRootFragment
					.getPackageFragment( packageName );
			IProject project = sourceRootFragment.getJavaProject( )
					.getProject( );
			IFolder sourceFolder = project.getFolder( "src" ); //$NON-NLS-1$

			long time = System.currentTimeMillis( );

			File locationFile = new File( sourceFolder
					.getFolder( packageName.replace( '.', '/' ) )
					.getFile( className )
					.getLocation( )
					.toString( )
					.replaceAll( "(?i)\\.class", time + ".java" ) ); //$NON-NLS-1$ //$NON-NLS-2$

			if ( !locationFile.getParentFile( ).exists( ) )
				locationFile.getParentFile( ).mkdirs( );

			PrintWriter writer = new PrintWriter( new BufferedWriter(
					new FileWriter( locationFile, false ) ) );
			writer.println( code );
			writer.close( );
			project.refreshLocal( IResource.DEPTH_INFINITE, null );

			ICompilationUnit iCompilationUnit = packageFragment
					.getCompilationUnit( className.replaceAll( "(?i)\\.class", //$NON-NLS-1$
							time + ".java" ) ); //$NON-NLS-1$
			iCompilationUnit.makeConsistent( null );
			iCompilationUnit.getResource( )
					.setLocalTimeStamp( new Date( ).getTime( ) );
			iCompilationUnit.becomeWorkingCopy( null );
			new SortMembersOperation( iCompilationUnit, null, true )
					.run( null );
			iCompilationUnit.commitWorkingCopy( true, null );
			iCompilationUnit.save( null, true );
			String content = iCompilationUnit.getSource( );
			iCompilationUnit.delete( true, null );
			if ( content != null )
				code = content;
			sourceRootFragment.getJavaProject( ).close( );
		}
		catch ( IOException e )
		{
			JavaDecompilerPlugin.logError( e, "" ); //$NON-NLS-1$
		}
		catch ( CoreException e )
		{
			JavaDecompilerPlugin.logError( e, "" ); //$NON-NLS-1$
		}
		return code;
	}

	public static void deleteDecompilerProject( )
	{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace( ).getRoot( );
		IProject project = root.getProject( ".decompiler" ); //$NON-NLS-1$
		if ( project != null && project.exists( ) )
		{
			try
			{
				project.delete( true, true, null );
			}
			catch ( CoreException e )
			{
				JavaDecompilerPlugin.logError( e, "" ); //$NON-NLS-1$
			}
		}
	}

	public static IPackageFragmentRoot getDecompilerSourceFolder( )
	{
		if ( decompilerSourceFolder != null )
			return decompilerSourceFolder;

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace( ).getRoot( );
		IProject project = root.getProject( ".decompiler" ); //$NON-NLS-1$
		if ( project == null )
			return null;
		if ( !project.exists( ) )
		{
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
				JavaDecompilerPlugin.logError( e1, "" ); //$NON-NLS-1$
				return null;
			}

			if ( !project.isOpen( ) )
				return null;
		}

		IJavaProject javaProject = JavaCore.create( project );

		try
		{
			List entries = new ArrayList( );
			IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall( );
			LibraryLocation[] locations = JavaRuntime
					.getLibraryLocations( vmInstall );
			for ( int i = 0; i < locations.length; i++ )
			{
				LibraryLocation element = locations[i];
				entries.add( JavaCore.newLibraryEntry(
						element.getSystemLibraryPath( ), null, null ) );
			}
			// add libs to project class path
			javaProject.setRawClasspath(
					(IClasspathEntry[]) entries
							.toArray( new IClasspathEntry[entries.size( )] ),
					null );

			IFolder sourceFolder = project.getFolder( "src" ); //$NON-NLS-1$
			sourceFolder.create( false, true, null );

			IPackageFragmentRoot codeGenFolder = javaProject
					.getPackageFragmentRoot( sourceFolder );
			IClasspathEntry[] oldEntries = javaProject.getRawClasspath( );
			IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length
					+ 1];
			System.arraycopy( oldEntries, 0, newEntries, 0, oldEntries.length );
			newEntries[oldEntries.length] = JavaCore
					.newSourceEntry( codeGenFolder.getPath( ) );
			javaProject.setRawClasspath( newEntries, null );
			javaProject.open( null );
			decompilerSourceFolder = javaProject
					.getPackageFragmentRoot( sourceFolder );
			return decompilerSourceFolder;
		}
		catch ( CoreException e )
		{
			JavaDecompilerPlugin.logError( e, "" ); //$NON-NLS-1$
		}
		return null;
	}
}
