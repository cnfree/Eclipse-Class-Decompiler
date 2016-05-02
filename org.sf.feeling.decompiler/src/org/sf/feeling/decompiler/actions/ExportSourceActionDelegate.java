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

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionDelegate;
import org.sf.feeling.decompiler.editor.JavaDecompilerClassFileEditor;

public class ExportSourceActionDelegate extends ActionDelegate implements
		IEditorActionDelegate
{

	JavaDecompilerClassFileEditor editor;

	public void setActiveEditor( IAction action, IEditorPart targetEditor )
	{
		if ( targetEditor instanceof JavaDecompilerClassFileEditor )
		{
			editor = (JavaDecompilerClassFileEditor) targetEditor;
		}
	}

	public void run( IAction action )
	{
		new ExportEditorSourceAction( ).run( );
	}

}
