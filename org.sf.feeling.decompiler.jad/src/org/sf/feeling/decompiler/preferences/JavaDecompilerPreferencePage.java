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

package org.sf.feeling.decompiler.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.fieldeditors.StringChoiceFieldEditor;
import org.sf.feeling.decompiler.i18n.Messages;

public class JavaDecompilerPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{

	private CheckFieldEditor optionLncEditor;
	private CheckFieldEditor alignEditor;
	private CheckFieldEditor eclipseFormatter;
	private CheckFieldEditor eclipseSorter;
	private Group basicGroup;
	private Group formatGroup;
	private Group debugGroup;

	public JavaDecompilerPreferencePage( )
	{
		super( 1 );
		setPreferenceStore( JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( ) );
	}

	public void createControl( Composite parent )
	{
		super.createControl( parent );
	}

	protected void createFieldEditors( )
	{
		StringChoiceFieldEditor defaultDecompiler = new StringChoiceFieldEditor( "net.sf.feeling.decompiler.type",
				Messages.getString( "JavaDecompilerPreferencePage.Label.DefaultClassDecompiler" ),
				getFieldEditorParent( ) ) {

			protected void doFillIntoGrid( Composite parent, int numColumns )
			{
				super.doFillIntoGrid( parent, numColumns );
				GridData gd = (GridData) getControl( ).getLayoutData( );
				gd.widthHint = 200;
				gd.grabExcessHorizontalSpace = false;
				gd.horizontalAlignment = 1;
				getControl( ).setLayoutData( gd );
			}
		};

		defaultDecompiler.addItem( "Jad", "Jad" );
		defaultDecompiler.addItem( "JD-Core", "JD-Core" );
		addField( defaultDecompiler );

		this.basicGroup = new Group( getFieldEditorParent( ), 0 );
		this.basicGroup.setText( Messages.getString( "JavaDecompilerPreferencePage.Label.DecompilerSettings" ) );
		GridData gd = new GridData( 768 );
		gd.horizontalSpan = defaultDecompiler.getNumberOfControls( );
		this.basicGroup.setLayoutData( gd );

		BooleanFieldEditor reusebuf = new BooleanFieldEditor( "net.sf.feeling.decompiler.reusebuff",
				Messages.getString( "JavaDecompilerPreferencePage.Label.ReuseCodeBuffer" ),
				this.basicGroup );
		addField( reusebuf );

		BooleanFieldEditor alwaysUse = new BooleanFieldEditor( "net.sf.feeling.decompiler.alwaysuse",
				Messages.getString( "JavaDecompilerPreferencePage.Label.IgnoreExistSource" ),
				this.basicGroup );
		addField( alwaysUse );

		BooleanFieldEditor showReport = new BooleanFieldEditor( "jd.ide.eclipse.prefs.DisplayMetadata",
				Messages.getString( "JavaDecompilerPreferencePage.Label.ShowDecompilerReport" ),
				this.basicGroup );
		addField( showReport );

		GridLayout layout = (GridLayout) this.basicGroup.getLayout( );
		layout.marginWidth = ( layout.marginHeight = 5 );
		this.basicGroup.layout( );

		this.formatGroup = new Group( getFieldEditorParent( ), 0 );
		this.formatGroup.setText( Messages.getString( "JavaDecompilerPreferencePage.Label.FormatSettings" ) );
		gd = new GridData( 768 );
		gd.horizontalSpan = defaultDecompiler.getNumberOfControls( );
		this.formatGroup.setLayoutData( gd );

		this.eclipseFormatter = new CheckFieldEditor( "net.sf.feeling.decompiler.use_eclipse_formatter",
				Messages.getString( "JavaDecompilerPreferencePage.Label.UseEclipseFormat.Jad" ),
				this.formatGroup );
		addField( this.eclipseFormatter );

		this.eclipseSorter = new CheckFieldEditor( "net.sf.feeling.decompiler.use_eclipse_sorter",
				Messages.getString( "JavaDecompilerPreferencePage.Lable.UseEclipseSorter.Jad" ),
				this.formatGroup );
		addField( this.eclipseSorter );

		layout = (GridLayout) this.formatGroup.getLayout( );
		layout.marginWidth = ( layout.marginHeight = 5 );
		this.formatGroup.layout( );

		this.debugGroup = new Group( getFieldEditorParent( ), 0 );
		this.debugGroup.setText( Messages.getString( "JavaDecompilerPreferencePage.Label.DebugSettings" ) );
		gd = new GridData( 768 );
		gd.horizontalSpan = defaultDecompiler.getNumberOfControls( );
		this.debugGroup.setLayoutData( gd );

		this.optionLncEditor = new CheckFieldEditor( "jd.ide.eclipse.prefs.DisplayLineNumbers",
				Messages.getString( "JavaDecompilerPreferencePage.Label.OutputLineNumber" ),
				this.debugGroup );
		addField( this.optionLncEditor );

		this.alignEditor = new CheckFieldEditor( "jd.ide.eclipse.prefs.Align",
				Messages.getString( "JavaDecompilerPreferencePage.Label.AlignCode" ),
				this.debugGroup );
		addField( this.alignEditor );

		layout = (GridLayout) this.debugGroup.getLayout( );
		layout.marginWidth = ( layout.marginHeight = 5 );
		this.debugGroup.layout( );

		Group startupGroup = new Group( getFieldEditorParent( ), 0 );
		startupGroup.setText( Messages.getString( "JavaDecompilerPreferencePage.Label.Startup" ) );
		gd = new GridData( 768 );
		gd.horizontalSpan = defaultDecompiler.getNumberOfControls( );
		startupGroup.setLayoutData( gd );

		CheckFieldEditor defaultViewerEditor = new CheckFieldEditor( "net.sf.feeling.decompiler.default_editor",
				Messages.getString( "JavaDecompilerPreferencePage.Label.DefaultEditor" ),
				startupGroup );
		addField( defaultViewerEditor );

		layout = (GridLayout) startupGroup.getLayout( );
		layout.marginWidth = ( layout.marginHeight = 5 );
		startupGroup.layout( );

		getFieldEditorParent( ).layout( );
	}

	public void init( IWorkbench arg0 )
	{
	}

	protected void initialize( )
	{
		super.initialize( );
		boolean enabled = getPreferenceStore( ).getBoolean( "jd.ide.eclipse.prefs.DisplayLineNumbers" );
		this.alignEditor.setEnabled( enabled, this.debugGroup );
	}

	protected void performDefaults( )
	{
		super.performDefaults( );
		boolean enabled = Boolean.valueOf( this.optionLncEditor.getBooleanValue( ) )
				.equals( Boolean.TRUE );
		this.alignEditor.setEnabled( enabled, this.debugGroup );
	}

	public void propertyChange( PropertyChangeEvent event )
	{
		if ( event.getSource( ) == this.optionLncEditor )
		{
			boolean enabled = event.getNewValue( ).equals( Boolean.TRUE );
			this.alignEditor.setEnabled( enabled, this.debugGroup );
			if ( enabled )
			{
				this.eclipseFormatter.getChangeControl( this.formatGroup )
						.setSelection( false );
				this.eclipseSorter.getChangeControl( this.formatGroup )
						.setSelection( false );
				this.eclipseFormatter.handleSelection( this.formatGroup );
				this.eclipseSorter.handleSelection( this.formatGroup );
			}
			if ( !( enabled ) )
			{
				this.alignEditor.getChangeControl( this.debugGroup )
						.setSelection( false );
				this.alignEditor.handleSelection( this.debugGroup );
			}
		}
		if ( event.getSource( ) == this.alignEditor )
		{
			boolean enabled = event.getNewValue( ).equals( Boolean.TRUE );
			if ( enabled )
			{
				this.eclipseFormatter.getChangeControl( this.formatGroup )
						.setSelection( false );
				this.eclipseSorter.getChangeControl( this.formatGroup )
						.setSelection( false );
				this.eclipseFormatter.handleSelection( this.formatGroup );
				this.eclipseSorter.handleSelection( this.formatGroup );
			}
		}
		if ( ( event.getSource( ) == this.eclipseFormatter )
				|| ( event.getSource( ) == this.eclipseSorter ) )
		{
			boolean enabled = event.getNewValue( ).equals( Boolean.TRUE );
			if ( enabled )
			{
				this.alignEditor.getChangeControl( this.debugGroup )
						.setSelection( false );
				this.optionLncEditor.getChangeControl( this.debugGroup )
						.setSelection( false );
				this.alignEditor.setEnabled( !( enabled ), this.debugGroup );
				this.alignEditor.handleSelection( this.debugGroup );
				this.optionLncEditor.handleSelection( this.debugGroup );
			}
		}
		super.propertyChange( event );
	}

	class CheckFieldEditor extends BooleanFieldEditor
	{

		public CheckFieldEditor( String paramString1, String paramString2,
				Composite paramComposite )
		{
			super( paramString1, paramString2, paramComposite );
		}

		protected void fireStateChanged( String property, boolean oldValue,
				boolean newValue )
		{
			fireValueChanged( property, ( oldValue ) ? Boolean.TRUE
					: Boolean.FALSE, ( newValue ) ? Boolean.TRUE
					: Boolean.FALSE );
		}

		public void handleSelection( Composite parent )
		{
			boolean isSelected = getChangeControl( parent ).getSelection( );
			valueChanged( false, isSelected );
		}

		protected void valueChanged( boolean oldValue, boolean newValue )
		{
			setPresentsDefaultValue( false );
			fireStateChanged( "field_editor_value", oldValue, newValue );
		}

		public Button getChangeControl( Composite parent )
		{
			return super.getChangeControl( parent );
		}
	}
}