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

public class CommandOption
{

	private String name;
	private String value;

	public CommandOption( String name, String value )
	{
		if ( name == null || name.trim( ).length( ) == 0 )
		{
			throw new IllegalArgumentException( "bogus option" ); //$NON-NLS-1$
		}
		this.name = name;
		this.value = value;
	}

	/**
	 * @return
	 */
	public String getName( )
	{
		return name;
	}

	/**
	 * @return
	 */
	public String getValue( )
	{
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString( )
	{
		StringBuffer buf = new StringBuffer( name );
		if ( value != null )
		{
			buf.append( ' ' ).append( value );
		}
		return buf.toString( );
	}

}
