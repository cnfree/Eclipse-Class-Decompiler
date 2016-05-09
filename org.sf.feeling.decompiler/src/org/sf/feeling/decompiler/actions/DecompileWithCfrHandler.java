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

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PlatformUI;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.actions.OpenClassWithContributionFactory.OpenClassesAction;
import org.sf.feeling.decompiler.editor.DecompilerType;
import org.sf.feeling.decompiler.editor.JavaDecompilerClassFileEditor;
import org.sf.feeling.decompiler.util.UIUtil;

public class DecompileWithCfrHandler extends DecompileHandler
{

	public Object execute( ExecutionEvent event ) throws ExecutionException
	{

		final List classes = UIUtil.getActiveSelection( );
		if ( classes != null && !classes.isEmpty( ) )
		{
			IEditorRegistry registry = PlatformUI.getWorkbench( )
					.getEditorRegistry( );
			IEditorDescriptor editor = registry.findEditor( JavaDecompilerPlugin.EDITOR_ID );
			new OpenClassesAction( editor, classes, DecompilerType.CFR ).run( );
		}
		else
		{
			JavaDecompilerClassFileEditor editor = UIUtil.getActiveEditor( );
			if ( editor != null )
			{
				if ( editor != null )
					editor.doSetInput( DecompilerType.CFR, true );
			}
		}
		return null;
	}
}
