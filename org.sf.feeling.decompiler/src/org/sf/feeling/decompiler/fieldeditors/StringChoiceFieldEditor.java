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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class StringChoiceFieldEditor extends FieldEditor
{

	private Combo fCombo;
	private List fKeys = new ArrayList( 5 );
	private List fLabels = new ArrayList( 5 );
	private String fOldKey;
	private boolean fIsValid;

	public StringChoiceFieldEditor( String name, String label, Composite parent )
	{
		super( name, label, parent );
		initList( );
	}

	public void addItem( String key, String label )
	{
		fKeys.add( key );
		fLabels.add( label );
		fCombo.add( label );
	}

	public Combo getControl( )
	{
		return fCombo;
	}

	protected void adjustForNumColumns( int numColumns )
	{
		if ( fCombo != null )
		{
			GridData gd = (GridData) fCombo.getLayoutData( );
			gd.horizontalSpan = numColumns - 1;
		}
	}

	protected void doFillIntoGrid( Composite parent, int numColumns )
	{
		Label l = new Label( parent, SWT.NULL );
		l.setText( getLabelText( ) );
		GridData gd = new GridData( );
		fCombo = new Combo( parent, SWT.READ_ONLY );
		fCombo.addSelectionListener( new SelectionAdapter( ) {

			public void widgetSelected( SelectionEvent e )
			{
				valueChanged( );
			}

		} );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = numColumns - 1;
		fCombo.setLayoutData( gd );

	}

	protected void doLoad( )
	{
		String value = getPreferenceStore( ).getString( getPreferenceName( ) );
		int index = fCombo.indexOf( value );
		if ( index >= 0 )
			fCombo.select( index );
	}

	protected void doLoadDefault( )
	{
		String value = getPreferenceStore( ).getDefaultString( getPreferenceName( ) );
		int index = fCombo.indexOf( value );
		if ( index >= 0 )
			fCombo.select( index );
	}

	protected void doStore( )
	{
		String value = ""; //$NON-NLS-1$
		if ( fCombo.getSelectionIndex( ) >= 0 )
		{
			value = fCombo.getItem( fCombo.getSelectionIndex( ) );
		}
		getPreferenceStore( ).setValue( getPreferenceName( ), value );
	}

	public int getNumberOfControls( )
	{
		return 2;
	}

	protected String getSelectedKey( )
	{
		int index = fCombo.getSelectionIndex( );
		if ( index >= 0 )
			return (String) fKeys.get( index );
		return null;
	}

	protected void initList( )
	{
		for ( int i = 0; i < fLabels.size( ); i++ )
			fCombo.add( (String) fLabels.get( i ) );
		fOldKey = getSelectedKey( );
	}

	public boolean isValid( )
	{
		return fIsValid;
	}

	protected void refreshValidState( )
	{
		fIsValid = fCombo.getSelectionIndex( ) >= 0;
	}

	public void removeItem( String key )
	{
		int index = fKeys.indexOf( key );
		if ( index >= 0 )
		{
			fKeys.remove( index );
			String label = (String) fLabels.get( index );
			fLabels.remove( index );
			fCombo.remove( label );
		}
	}

	/**
	 * Set the focus to this field editor.
	 */
	public void setFocus( )
	{
		if ( fCombo != null )
		{
			fCombo.setFocus( );
		}
	}

	protected void valueChanged( )
	{
		setPresentsDefaultValue( false );
		boolean oldState = fIsValid;
		refreshValidState( );

		if ( fIsValid != oldState )
			fireStateChanged( IS_VALID, oldState, fIsValid );

		String newKey = getSelectedKey( );
		if ( newKey == null ? newKey != fOldKey : !newKey.equals( fOldKey ) )
		{
			fireValueChanged( VALUE, fOldKey, newKey );
			fOldKey = newKey;
		}
	}
}