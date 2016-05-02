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

package org.sf.feeling.decompiler.jad;

public class NumericOption extends CommandOption
{

	private int numericValue;

	/**
	 * @param name
	 * @param value
	 */
	public NumericOption( String name, int numericValue )
	{
		super( name, String.valueOf( numericValue ) );
		this.numericValue = numericValue;
	}

	/**
	 * @return
	 */
	public int getNumericValue( )
	{
		return numericValue;
	}

	/**
	 * No whitespace is inserted between option name and value.
	 * 
	 * @see org.sf.feeling.decompiler.jad.CommandOption#toString()
	 */
	public String toString( )
	{
		return new StringBuffer( getName( ) ).append( getValue( ) ).toString( );
	}

}
