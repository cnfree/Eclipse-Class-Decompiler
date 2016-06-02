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

import org.eclipse.jface.action.Action;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.editor.JavaDecompilerClassFileEditor;
import org.sf.feeling.decompiler.i18n.Messages;
import org.sf.feeling.decompiler.util.UIUtil;

public class DebugModeAction extends Action
{

	public DebugModeAction( )
	{
		super( Messages.getString("DebugModeAction.Action.Text"), AS_CHECK_BOX ); //$NON-NLS-1$
	}

	public void run( )
	{
		JavaDecompilerPlugin.getDefault( ).setDebugMode( !isChecked( ) );
		new DecompileAction( ).run( );
		JavaDecompilerClassFileEditor editor = UIUtil.getActiveEditor( );
		if ( editor != null )
		{
			editor.setFocus( );
			editor.notifyPropertiesChange( );
		}
	}

	public boolean isChecked( )
	{
		return JavaDecompilerPlugin.getDefault( ).isDebugMode( );
	}

}