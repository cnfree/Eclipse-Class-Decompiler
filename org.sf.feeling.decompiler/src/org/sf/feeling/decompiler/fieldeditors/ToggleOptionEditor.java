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

package org.sf.feeling.decompiler.fieldeditors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.sf.feeling.decompiler.i18n.Messages;
import org.sf.feeling.decompiler.jad.CommandOption;

public class ToggleOptionEditor extends CommandLineOptionEditor
{

	protected Button cbox;

	/**
	 * @param optionName
	 * @param parent
	 */
	public ToggleOptionEditor( String optionName, String label, Composite parent )
	{
		super( optionName, label, parent );
	}

	protected void adjustForNumColumns( int numColumns )
	{
		( (GridData) cbox.getLayoutData( ) ).horizontalSpan = numColumns;
	}

	protected void doAccept( CommandOption option )
	{
		boolean select = false;
		if ( option != null )
		{
			select = true;
			if ( option.getValue( ) != null && getPreferencePage( ) != null )
			{
				getPreferencePage( ).setErrorMessage( Messages.getString( "ToggleOptionEditor.Message.Label.Option" ) //$NON-NLS-1$
						+ getOptionName( )
						+ Messages.getString( "ToggleOptionEditor.Message.Label.Error" ) ); //$NON-NLS-1$
			}
		}
		cbox.setSelection( select );
	}

	protected void doFillIntoGrid( Composite parent, int numColumns )
	{
		cbox = new Button( parent, SWT.CHECK );
		cbox.setText( getLabel( ) );
		cbox.setLayoutData( new GridData( ) );
	}

	public int getNumberOfControls( )
	{
		return 1;
	}

	public CommandOption toOption( )
	{
		return cbox.getSelection( ) ? new CommandOption( getOptionName( ), null )
				: null;
	}

}
