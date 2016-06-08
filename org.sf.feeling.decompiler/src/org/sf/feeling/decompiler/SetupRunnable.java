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

package org.sf.feeling.decompiler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.EditorRegistry;
import org.eclipse.ui.internal.registry.FileEditorMapping;
import org.sf.feeling.decompiler.editor.JavaDecompilerClassFileEditor;
import org.sf.feeling.decompiler.extension.DecompilerAdapterManager;
import org.sf.feeling.decompiler.update.IDecompilerUpdateHandler;
import org.sf.feeling.decompiler.util.ClassUtil;
import org.sf.feeling.decompiler.util.ReflectionUtils;

public class SetupRunnable implements Runnable
{

	public void run( )
	{
		checkDecompilerUpdate( );
		checkClassFileAssociation( );
		setupPartListener( );
		queryLocation( );
	}

	private void queryLocation( )
	{
		Job job = new Job( "Decompiler Location Query" ) { //$NON-NLS-1$

			protected IStatus run( IProgressMonitor monitor )
			{
				monitor.beginTask( "start task", 100 ); //$NON-NLS-1$
				try
				{
					URL location = new URL( "http://test.ip138.com/query/?datatype=xml" ); //$NON-NLS-1$
					HttpURLConnection con = (HttpURLConnection) location.openConnection( );
					con.setRequestMethod( "GET" ); //$NON-NLS-1$
					con.setRequestProperty( "User-Agent", "Mozilla/5.0" ); //$NON-NLS-1$ //$NON-NLS-2$
					int responseCode = con.getResponseCode( );
					if ( responseCode == HttpURLConnection.HTTP_OK )
					{
						BufferedReader in = new BufferedReader( new InputStreamReader( con.getInputStream( ),
								"UTF-8" ) ); //$NON-NLS-1$
						StringBuffer response = new StringBuffer( );
						String inputLine;
						while ( ( inputLine = in.readLine( ) ) != null )
						{
							response.append( inputLine );
						}
						in.close( );
						Pattern pattern = Pattern.compile( "<country>.+</country>" ); //$NON-NLS-1$
						Matcher matcher = pattern.matcher( response.toString( ) );
						if ( matcher.find( ) )
						{
							String country = matcher.group( )
									.replace( "<country>", "" ) //$NON-NLS-1$ //$NON-NLS-2$
									.replace( "</country>", "" ) //$NON-NLS-1$ //$NON-NLS-2$
									.trim( );
							JavaDecompilerPlugin.getDefault( )
									.setFromChina( "中国".equals( country ) ); //$NON-NLS-1$
						}
					}
					monitor.worked( 100 );
					return Status.OK_STATUS;
				}
				catch ( Exception e )
				{
					monitor.worked( 100 );
					return Status.CANCEL_STATUS;
				}
			}
		};

		job.setPriority( Job.DECORATE );
		job.setSystem( true );
		job.schedule( );
	}

	private void setupPartListener( )
	{
		IWorkbenchPage page = PlatformUI.getWorkbench( )
				.getActiveWorkbenchWindow( )
				.getActivePage( );
		page.addPartListener( new IPartListener( ) {

			public void partOpened( IWorkbenchPart part )
			{

			}

			public void partDeactivated( IWorkbenchPart part )
			{

			}

			public void partClosed( IWorkbenchPart part )
			{

			}

			public void partBroughtToTop( IWorkbenchPart part )
			{
				if ( part instanceof JavaDecompilerClassFileEditor )
				{
					String code = ( (JavaDecompilerClassFileEditor) part ).getViewer( )
							.getDocument( )
							.get( );
					if ( ClassUtil.isDebug( ) != JavaDecompilerClassFileEditor.isDebug( code ) )
					{
						( (JavaDecompilerClassFileEditor) part ).doSetInput( false );
					}
				}
			}

			public void partActivated( IWorkbenchPart part )
			{

			}
		} );
	}

	private void checkDecompilerUpdate( )
	{
		final IPreferenceStore prefs = JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( );

		final Object updateAdapter = DecompilerAdapterManager.getAdapter( JavaDecompilerPlugin.getDefault( ),
				IDecompilerUpdateHandler.class );

		if ( updateAdapter instanceof IDecompilerUpdateHandler )
		{
			final IDecompilerUpdateHandler updateHandler = (IDecompilerUpdateHandler) updateAdapter;
			Job job = new Job( "Decompiler update job" ) { //$NON-NLS-1$

				protected IStatus run( IProgressMonitor monitor )
				{
					monitor.beginTask( "start task", 100 ); //$NON-NLS-1$
					try
					{
						if ( updateHandler.isForce( monitor )
								|| prefs.getBoolean( JavaDecompilerPlugin.CHECK_UPDATE ) )
							Display.getDefault( ).asyncExec( new Runnable( ) {

								public void run( )
								{
									updateHandler.execute( );
								}
							} );
						monitor.worked( 100 );
						return Status.OK_STATUS;
					}
					catch ( Exception e )
					{
						monitor.worked( 100 );
						return Status.CANCEL_STATUS;
					}
				}
			};

			job.setPriority( Job.DECORATE );
			job.setSystem( true );
			job.schedule( );

		}
	}

	private void checkClassFileAssociation( )
	{
		IPreferenceStore prefs = JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( );
		if ( prefs.getBoolean( JavaDecompilerPlugin.DEFAULT_EDITOR ) )
		{
			updateClassDefaultEditor( );

			IPreferenceStore store = WorkbenchPlugin.getDefault( )
					.getPreferenceStore( );
			store.addPropertyChangeListener( new IPropertyChangeListener( ) {

				public void propertyChange( PropertyChangeEvent event )
				{
					if ( IPreferenceConstants.RESOURCES.equals( event.getProperty( ) ) )
					{
						updateClassDefaultEditor( );
					}
				}
			} );
		}
	}

	protected void updateClassDefaultEditor( )
	{
		EditorRegistry registry = (EditorRegistry) PlatformUI.getWorkbench( )
				.getEditorRegistry( );

		IFileEditorMapping[] mappings = registry.getFileEditorMappings( );

		IFileEditorMapping classNoSource = null;
		IFileEditorMapping classPlain = null;

		for ( int i = 0; i < mappings.length; i++ )
		{
			IFileEditorMapping mapping = mappings[i];
			if ( mapping.getExtension( ).equals( "class without source" ) ) //$NON-NLS-1$
			{
				classNoSource = mapping;
			}
			else if ( mapping.getExtension( ).equals( "class" ) ) //$NON-NLS-1$
			{
				classPlain = mapping;
			}
		}

		IFileEditorMapping[] classMappings = new IFileEditorMapping[]{
				classNoSource, classPlain
		};

		boolean needUpdate = checkDefaultEditor( classMappings );
		if ( needUpdate )
		{
			for ( int i = 0; i < classMappings.length; i++ )
			{
				IFileEditorMapping mapping = classMappings[i];
				for ( int j = 0; j < mapping.getEditors( ).length; j++ )
				{
					IEditorDescriptor editor = mapping.getEditors( )[j];
					if ( editor.getId( )
							.equals( JavaDecompilerPlugin.EDITOR_ID ) )
					{
						try
						{
							ReflectionUtils.invokeMethod( (FileEditorMapping) mapping,
									"setDefaultEditor", //$NON-NLS-1$
									new Class[]{
										Class.forName( "org.eclipse.ui.IEditorDescriptor" ) //$NON-NLS-1$
									},
									new Object[]{
										editor
									} );
						}
						catch ( ClassNotFoundException e )
						{
						}

						try
						{
							ReflectionUtils.invokeMethod( (FileEditorMapping) mapping,
									"setDefaultEditor", //$NON-NLS-1$
									new Class[]{
										Class.forName( "org.eclipse.ui.internal.registry.EditorDescriptor" ) //$NON-NLS-1$
									},
									new Object[]{
										editor
									} );
						}
						catch ( ClassNotFoundException e )
						{
						}
					}
				}
			}

			registry.setFileEditorMappings( (FileEditorMapping[]) mappings );
			registry.saveAssociations( );
		}
	}

	protected boolean checkDefaultEditor( IFileEditorMapping[] classMappings )
	{
		for ( int i = 0; i < classMappings.length; i++ )
		{
			IFileEditorMapping mapping = classMappings[i];
			if ( mapping.getDefaultEditor( ) != null
					&& !mapping.getDefaultEditor( )
							.getId( )
							.equals( JavaDecompilerPlugin.EDITOR_ID ) )
				return true;
		}
		return false;
	}
}
