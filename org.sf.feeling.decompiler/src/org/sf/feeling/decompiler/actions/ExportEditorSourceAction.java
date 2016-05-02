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

package org.sf.feeling.decompiler.actions;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.editor.JavaDecompilerClassFileEditor;
import org.sf.feeling.decompiler.i18n.Messages;
import org.sf.feeling.decompiler.util.FileUtil;
import org.sf.feeling.decompiler.util.UIUtil;

public class ExportEditorSourceAction extends Action
{

	public ExportEditorSourceAction( )
	{
		super( Messages.getString( "JavaDecompilerActionBarContributor.Action.ExportSource" ) ); //$NON-NLS-1$
		this.setImageDescriptor( JavaDecompilerPlugin.getImageDescriptor( "icons/etool16/export_wiz.gif" ) ); //$NON-NLS-1$
		this.setDisabledImageDescriptor( JavaDecompilerPlugin.getImageDescriptor( "icons/dtool16/export_wiz.gif" ) ); //$NON-NLS-1$
	}

	public void run( )
	{
		JavaDecompilerClassFileEditor classEditor = null;
		JavaDecompilerClassFileEditor editor = UIUtil.getActiveEditor( );
		if ( editor != null )
			classEditor = editor;
		if ( classEditor != null )
		{
			IClassFile cf = ( (IClassFileEditorInput) classEditor.getEditorInput( ) ).getClassFile( );

			FileDialog dialog = new FileDialog( classEditor.getEditorSite( )
					.getShell( ), SWT.SAVE | SWT.SHEET );
			dialog.setFileName( cf.getElementName( ).replaceAll( "\\..+", //$NON-NLS-1$
					"" ) ); //$NON-NLS-1$
			dialog.setFilterExtensions( new String[]{
				"*.java" //$NON-NLS-1$
			} );
			String file = dialog.open( );
			if ( file != null && file.trim( ).length( ) > 0 )
			{
				String projectFile = file.trim( );
				try
				{

					FileUtil.writeToFile( new File( projectFile ),
							cf.getSource( ) );
				}
				catch ( CoreException e )
				{
					ExceptionHandler.handle( e,
							Messages.getString( "JavaDecompilerActionBarContributor.ErrorDialog.Title" ), //$NON-NLS-1$
							Messages.getString( "JavaDecompilerActionBarContributor.ErrorDialog.Message.ExportFailed" ) ); //$NON-NLS-1$
				}
			}
			else
			{
				return;
			}
		}
	}

	public boolean isEnabled( )
	{
		JavaDecompilerClassFileEditor editor = UIUtil.getActiveEditor( );
		return editor != null;
	}
}