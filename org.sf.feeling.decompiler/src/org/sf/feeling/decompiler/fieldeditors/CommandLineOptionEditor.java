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

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.sf.feeling.decompiler.jad.CommandLine;
import org.sf.feeling.decompiler.jad.CommandOption;

public abstract class CommandLineOptionEditor
{

	private String optionName;
	private String label;
	private PreferencePage preferencePage;
	private Composite parent;

	protected CommandLineOptionEditor( )
	{
	}

	public CommandLineOptionEditor( String optionName, String label,
			Composite parent )
	{
		init( optionName, label );
		createControl( parent );
	}

	public void accept( CommandLine cmdLine )
	{
		doAccept( cmdLine.getOption( optionName ) );
	}

	protected abstract void adjustForNumColumns( int numColumns );

	protected void createControl( Composite parent )
	{
		this.parent = parent;
		GridLayout layout = new GridLayout( );
		layout.numColumns = getNumberOfControls( );
		// layout.marginWidth = 0;
		// layout.marginHeight = 0;
		layout.makeColumnsEqualWidth = false;
		parent.setLayout( layout );
		doFillIntoGrid( parent, layout.numColumns );
	}

	protected void doAccept( CommandOption option )
	{
	}

	protected abstract void doFillIntoGrid( Composite parent, int numColumns );

	/**
	 * @return
	 */
	public String getLabel( )
	{
		return label;
	}

	public abstract int getNumberOfControls( );

	/**
	 * @return
	 */
	public String getOptionName( )
	{
		return optionName;
	}

	public Composite getParent( )
	{
		return parent;
	}

	/**
	 * @return
	 */
	public PreferencePage getPreferencePage( )
	{
		return preferencePage;
	}

	protected void init( String optionName, String label )
	{
		if ( optionName == null )
		{
			throw new IllegalArgumentException( "null option name" ); //$NON-NLS-1$
		}
		this.optionName = optionName;
		this.label = ( label != null ) ? label : optionName;
	}

	/**
	 * @param page
	 */
	public void setPreferencePage( PreferencePage page )
	{
		this.preferencePage = page;
	}

	public abstract CommandOption toOption( );

}
