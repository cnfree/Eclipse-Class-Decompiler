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

import org.eclipse.swt.widgets.Composite;
import org.sf.feeling.decompiler.jad.CommandOption;

public class NumericOptionEditor extends CommandLineOptionEditor
{

	/**
	 * @param optionName
	 * @param parent
	 */
	public NumericOptionEditor( String optionName, String label,
			Composite parent )
	{
		super( optionName, label, parent );
	}

	public NumericOptionEditor( String optionName, String label,
			Composite parent, int[] allowedValues )
	{
		super( optionName, label, parent );
	}

	protected void adjustForNumColumns( int numColumns )
	{
		// TODO Auto-generated method stub

	}

	protected void doAccept( CommandOption option )
	{
	}

	protected void doFillIntoGrid( Composite parent, int numColumns )
	{
		// TODO Auto-generated method stub

	}

	public int getNumberOfControls( )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public CommandOption toOption( )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
