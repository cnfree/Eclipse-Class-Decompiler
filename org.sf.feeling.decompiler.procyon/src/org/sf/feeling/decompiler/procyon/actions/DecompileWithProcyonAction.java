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

package org.sf.feeling.decompiler.procyon.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.sf.feeling.decompiler.i18n.Messages;
import org.sf.feeling.decompiler.procyon.ProcyonDecompilerPlugin;
import org.sf.feeling.decompiler.util.UIUtil;

public class DecompileWithProcyonAction extends Action
{

	public DecompileWithProcyonAction( )
	{
		super( Messages.getString( "JavaDecompilerActionBarContributor.Action.DecompileWithProcyon" ) ); //$NON-NLS-1$
		this.setImageDescriptor( ProcyonDecompilerPlugin.getImageDescriptor( "icons/procyon_16.png" ) ); //$NON-NLS-1$
	}

	public void run( )
	{
		try
		{
			new DecompileWithProcyonHandler( ).execute( null );
		}
		catch ( ExecutionException e )
		{
		}
	}

	public boolean isEnabled( )
	{
		return UIUtil.getActiveEditor( ) != null
				|| UIUtil.getActiveSelection( ) != null;
	}
}