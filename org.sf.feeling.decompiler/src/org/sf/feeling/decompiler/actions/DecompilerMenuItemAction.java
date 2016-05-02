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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;

public class DecompilerMenuItemAction implements
		IWorkbenchWindowPulldownDelegate,
		IWorkbenchWindowPulldownDelegate2
{

	public DecompilerMenuItemAction( )
	{
		super( );
	}

	public Menu getMenu( Control parent )
	{
		return new SubMenuCreator( ).getMenu( parent );
	}

	public Menu getMenu( Menu parent )
	{
		return new SubMenuCreator( ).getMenu( parent );
	}

	public void init( IWorkbenchWindow window )
	{

	}

	public void dispose( )
	{
	}

	public void run( IAction action )
	{
		new DecompileAction( ).run( );
	}

	public void selectionChanged( IAction action, ISelection selection )
	{
		action.setEnabled( isEnable( ) );
	}

	private boolean isEnable( )
	{
		return true;
	}
}