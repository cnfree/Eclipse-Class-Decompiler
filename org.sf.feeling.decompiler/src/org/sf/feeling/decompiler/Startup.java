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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.EditorRegistry;
import org.eclipse.ui.internal.registry.FileEditorMapping;
import org.sf.feeling.decompiler.extension.DecompilerAdapterManager;
import org.sf.feeling.decompiler.update.IDecompilerUpdateHandler;
import org.sf.feeling.decompiler.util.ReflectionUtils;

public class Startup implements IStartup
{

	// External plug-in IDs
	public static final String JDT_EDITOR_ID = "org.eclipse.jdt.ui.ClassFileEditor"; //$NON-NLS-1$

	public void earlyStartup( )
	{
		Display.getDefault( ).syncExec( new SetupRunnable( ) );
	}

	public static class SetupRunnable implements Runnable
	{

		public void run( )
		{
			checkDecompilerUpdate( );
			checkClassFileAssociation( );
		}

		private void checkDecompilerUpdate( )
		{

			IPreferenceStore prefs = JavaDecompilerPlugin.getDefault( )
					.getPreferenceStore( );
			if ( prefs.getBoolean( JavaDecompilerPlugin.CHECK_UPDATE ) )
			{
				Object updateAdapter = DecompilerAdapterManager.getAdapter( JavaDecompilerPlugin.getDefault( ),
						IDecompilerUpdateHandler.class );

				if ( updateAdapter instanceof IDecompilerUpdateHandler )
				{
					( (IDecompilerUpdateHandler) updateAdapter ).execute( );
				}
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

}
