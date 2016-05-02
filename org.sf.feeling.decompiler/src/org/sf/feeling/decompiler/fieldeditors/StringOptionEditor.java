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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.sf.feeling.decompiler.i18n.Messages;
import org.sf.feeling.decompiler.jad.CommandOption;

public class StringOptionEditor extends CommandLineOptionEditor
{

	public static int ALLOW_EMPTY = 1;
	public static int ALLOW_WS = 2;

	private Text value;
	private int allowMask;

	/**
	 * @param optionName
	 * @param parent
	 */
	public StringOptionEditor( String optionName, String label, int allowMask,
			Composite parent )
	{
		this.allowMask = allowMask;
		init( optionName, label );
		createControl( parent );
	}

	protected void adjustForNumColumns( int numColumns )
	{
		GridData gd = (GridData) value.getLayoutData( );
		gd.horizontalSpan = numColumns - 1;
		gd.grabExcessHorizontalSpace = true;// (gd.horizontalSpan == 1);
	}

	protected void doAccept( CommandOption option )
	{
		if ( option == null )
		{
			value.setText( "" ); //$NON-NLS-1$
		}
		else
		{
			String val = option.getValue( );
			if ( val != null && ( val = val.trim( ) ).length( ) > 0 )
			{
				value.setText( val );
				if ( ( allowMask & ALLOW_WS ) == 0
						&& ( val.indexOf( ' ' ) > 0 || val.indexOf( '\t' ) > 0 )
						&& getPreferencePage( ) != null )
				{
					getPreferencePage( ).setErrorMessage( Messages.getString( "StringOptionEditor.Message.Label.Option" ) //$NON-NLS-1$
							+ getOptionName( )
							+ Messages.getString( "StringOptionEditor.Message.Label.NotSupportWhitespace" ) ); //$NON-NLS-1$
				}
			}
			else
			{
				value.setText( "" ); //$NON-NLS-1$
				if ( ( allowMask & ALLOW_EMPTY ) == 0
						&& getPreferencePage( ) != null )
				{
					getPreferencePage( ).setErrorMessage( Messages.getString( "StringOptionEditor.Message.Label.Option" ) //$NON-NLS-1$
							+ getOptionName( )
							+ Messages.getString( "StringOptionEditor.Message.Label.MustHaveValue" ) ); //$NON-NLS-1$
				}
			}
		}
	}

	protected void doFillIntoGrid( Composite parent, int numColumns )
	{
		Label label = new Label( parent, SWT.LEFT );
		label.setText( getLabel( ) );
		value = new Text( parent, SWT.BORDER );
		value.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
	}

	public int getNumberOfControls( )
	{
		return 2;
	}

	public CommandOption toOption( )
	{
		String val = value.getText( );
		if ( val != null && ( val = val.trim( ) ).length( ) > 0 )
		{
			if ( ( val.indexOf( ' ' ) < 0 && val.indexOf( '\t' ) < 0 )
					|| ( allowMask & ALLOW_WS ) != 0 )
			{
				return new CommandOption( getOptionName( ), val );
			}
		}
		if ( ( allowMask & ALLOW_EMPTY ) != 0 )
		{
			return new CommandOption( getOptionName( ), null );
		}
		return null;
	}

}
