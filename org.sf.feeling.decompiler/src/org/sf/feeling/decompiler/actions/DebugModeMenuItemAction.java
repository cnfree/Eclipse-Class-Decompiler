
package org.sf.feeling.decompiler.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;

public class DebugModeMenuItemAction implements
		IWorkbenchWindowPulldownDelegate,
		IWorkbenchWindowPulldownDelegate2
{

	public DebugModeMenuItemAction( )
	{
		super( );
	}

	public Menu getMenu( Control parent )
	{
		return null;
	}

	public Menu getMenu( Menu parent )
	{
		return null;
	}

	public void init( IWorkbenchWindow window )
	{
	}

	public void dispose( )
	{
	}

	public void run( IAction action )
	{
		new DebugModeAction( ).run( );
	}

	public void selectionChanged( IAction action, ISelection selection )
	{
		action.setChecked( JavaDecompilerPlugin.getDefault( ).isDebugMode( ) );
	}
}