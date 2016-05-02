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

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.sf.feeling.decompiler.i18n.Messages;

/**
 * Field editor that allows no white space
 */
public class NoWSFieldEditor extends StringFieldEditor
{

	public NoWSFieldEditor( String name, String labelText, Composite parent )
	{
		super( name, labelText, parent );
		setErrorMessage( Messages.getString( "NoWSFieldEditor.Error.NotAllowWhitespace" ) ); //$NON-NLS-1$
	}

	protected boolean doCheckState( )
	{
		String value = getStringValue( );

		if ( value == null )
			return true;

		return value.indexOf( ' ' ) == -1
				&& value.indexOf( '\t' ) == -1
				&& value.indexOf( '\r' ) == -1
				&& value.indexOf( '\n' ) == -1;
	}
}
