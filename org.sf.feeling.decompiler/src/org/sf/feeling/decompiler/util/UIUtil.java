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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.navigator.IExtensionStateConstants.Values;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonNavigatorManager;
import org.eclipse.ui.navigator.IExtensionStateModel;
import org.eclipse.ui.navigator.INavigatorContentService;
import org.sf.feeling.decompiler.editor.JavaDecompilerClassFileEditor;

public class UIUtil
{

	public static JavaDecompilerClassFileEditor getActiveEditor( )
	{
		final JavaDecompilerClassFileEditor[] editors = new JavaDecompilerClassFileEditor[1];
		Display.getDefault( ).syncExec( new Runnable( ) {

			public void run( )
			{
				IWorkbenchPart editor = getActiveEditor( true );
				if ( editor instanceof JavaDecompilerClassFileEditor )
				{
					editors[0] = (JavaDecompilerClassFileEditor) editor;
				}
				else
				{
					IWorkbenchWindow window = PlatformUI.getWorkbench( )
							.getActiveWorkbenchWindow( );

					if ( window != null )
					{
						IWorkbenchPage pg = window.getActivePage( );

						if ( pg != null )
						{
							IEditorPart editorPart = pg.getActiveEditor( );
							if ( editorPart instanceof JavaDecompilerClassFileEditor )
							{
								editors[0] = (JavaDecompilerClassFileEditor) editorPart;
							}
						}
					}
				}
			}
		} );
		return editors[0];
	}

	public static JavaDecompilerClassFileEditor getActiveDecompilerEditor( )
	{
		final JavaDecompilerClassFileEditor[] editors = new JavaDecompilerClassFileEditor[1];
		Display.getDefault( ).syncExec( new Runnable( ) {

			public void run( )
			{
				IWorkbenchPart editor = getActiveEditor( true );
				if ( editor instanceof JavaDecompilerClassFileEditor )
				{
					editors[0] = (JavaDecompilerClassFileEditor) editor;
				}
			}
		} );
		return editors[0];
	}

	public static List getActiveSelection( )
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench( )
				.getActiveWorkbenchWindow( );
		final List classes = getSelectedElements( window.getSelectionService( ),
				IClassFile.class );
		if ( classes != null && !classes.isEmpty( ) )
		{
			return classes;
		}
		return null;
	}

	public static String getActivePerspectiveId( )
	{
		IWorkbench wb = PlatformUI.getWorkbench( );
		if ( wb == null )
			return null;

		IWorkbenchWindow win = wb.getActiveWorkbenchWindow( );
		if ( win == null )
			return null;

		IWorkbenchPage page = win.getActivePage( );
		if ( page == null )
			return null;

		IPerspectiveDescriptor perspective = page.getPerspective( );
		if ( perspective == null )
			return null;

		return perspective.getId( );
	}

	public static boolean isDebugPerspective( )
	{
		return "org.eclipse.debug.ui.DebugPerspective" //$NON-NLS-1$
		.equals( getActivePerspectiveId( ) );
	}

	public static List getSelectedElements( ISelectionService selService,
			Class eleClass )
	{

		Iterator selections = getSelections( selService );
		List elements = new ArrayList( );

		while ( ( selections != null ) && selections.hasNext( ) )
		{
			Object select = selections.next( );

			if ( eleClass.isInstance( select ) )
				elements.add( select );
		}

		return elements;
	}

	public static Iterator getSelections( ISelectionService selService )
	{
		ISelection selection = selService.getSelection( );

		if ( selection != null )
		{
			if ( selection instanceof IStructuredSelection )
			{
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				return structuredSelection.iterator( );
			}
		}

		return null;
	}

	public static IWorkbenchPart getActiveEditor( boolean activePageOnly )
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench( )
				.getActiveWorkbenchWindow( );

		if ( window != null )
		{
			if ( activePageOnly )
			{
				IWorkbenchPage pg = window.getActivePage( );

				if ( pg != null )
				{
					return pg.getActivePart( );
				}
			}
			else
			{
				IWorkbenchPage[] pgs = window.getPages( );

				for ( int i = 0; i < pgs.length; i++ )
				{
					IWorkbenchPage pg = pgs[i];

					if ( pg != null )
					{
						IWorkbenchPart part = pg.getActivePart( );

						if ( part != null )
						{
							return part;
						}
					}
				}
			}
		}

		return null;
	}

	public static boolean isOpenClassEditor( )
	{
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench( )
				.getWorkbenchWindows( );

		if ( windows != null )
		{
			for ( int i = 0; i < windows.length; i++ )
			{
				IWorkbenchWindow window = windows[i];
				IWorkbenchPage[] pgs = window.getPages( );
				for ( int j = 0; j < pgs.length; j++ )
				{
					IWorkbenchPage pg = pgs[j];
					if ( pg != null )
					{
						IEditorPart[] parts = pg.getEditors( );
						if ( parts != null )
						{
							for ( int k = 0; k < parts.length; k++ )
							{
								if ( parts[i] instanceof JavaDecompilerClassFileEditor )
									return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public static List getExportSelections( )
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench( )
				.getActiveWorkbenchWindow( );
		final List selectedJars = getSelectedElements( window.getSelectionService( ),
				IPackageFragmentRoot.class );
		if ( selectedJars.size( ) == 1 )
		{
			return selectedJars;
		}

		if ( selectedJars.size( ) > 1 )
			return null;

		final List selectedPackages = getSelectedElements( window.getSelectionService( ),
				IPackageFragment.class );
		final List selectedClasses = getSelectedElements( window.getSelectionService( ),
				IClassFile.class );
		selectedClasses.addAll( selectedPackages );
		if ( !selectedClasses.isEmpty( ) )
		{
			return selectedClasses;
		}

		return null;
	}

	public static boolean isPackageFlat( )
	{
		boolean isFlat = false;
		try
		{
			IWorkbenchPart view = getActiveEditor( true );
			if ( view != null )
			{
				if ( view.getSite( )
						.getId( )
						.equals( "org.eclipse.ui.navigator.ProjectExplorer" ) ) //$NON-NLS-1$
				{
					CommonNavigator explorer = (CommonNavigator) view;
					Field field = CommonNavigator.class.getDeclaredField( "commonManager" ); //$NON-NLS-1$
					if ( field != null )
					{
						field.setAccessible( true );
						CommonNavigatorManager manager = (CommonNavigatorManager) field.get( explorer );

						field = CommonNavigatorManager.class.getDeclaredField( "contentService" ); //$NON-NLS-1$
						if ( field != null )
						{
							field.setAccessible( true );
							INavigatorContentService service = (INavigatorContentService) field.get( manager );
							IExtensionStateModel model = service.findStateModel( "org.eclipse.jdt.java.ui.javaContent" ); //$NON-NLS-1$
							isFlat = model.getBooleanProperty( Values.IS_LAYOUT_FLAT );
						}
					}
				}
				else if ( view.getSite( )
						.getId( )
						.equals( "org.eclipse.jdt.ui.PackageExplorer" ) ) //$NON-NLS-1$
				{
					PackageExplorerPart explorer = (PackageExplorerPart) view;
					isFlat = explorer.isFlatLayout( );
				}
			}
		}
		catch ( Exception e )
		{
		}
		return isFlat;
	}

	public static boolean isWin32( )
	{
		return Platform.OS_WIN32.equalsIgnoreCase( Platform.getOS( ) );
	}

	public static String getPathLocation( IPath path )
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace( );
		IWorkspaceRoot root = workspace.getRoot( );
		IResource resource = root.findMember( path );
		if ( resource != null )
		{
			return resource.getLocation( ).toOSString( );
		}
		return null;
	}

	public static boolean requestFromJavadocHover( )
	{
		StackTraceElement[] stacks = Thread.currentThread( ).getStackTrace( );
		for ( int i = 0; i < stacks.length; i++ )
		{
			if ( stacks[i].getClassName( ).indexOf( "BinaryType" ) != -1 //$NON-NLS-1$
					&& stacks[i].getMethodName( ).equals( "getJavadocRange" ) ) //$NON-NLS-1$
				return false;

			if ( stacks[i].getClassName( ).indexOf( "JavadocHover" ) != -1 //$NON-NLS-1$
					&& stacks[i].getMethodName( ).equals( "getHoverInfo" ) ) //$NON-NLS-1$
				return true;
		}
		return false;
	}
}
