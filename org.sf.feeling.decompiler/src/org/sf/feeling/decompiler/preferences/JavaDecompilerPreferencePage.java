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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.editor.DecompilerType;
import org.sf.feeling.decompiler.fieldeditors.StringChoiceFieldEditor;
import org.sf.feeling.decompiler.i18n.Messages;
import org.sf.feeling.decompiler.jad.JadDecompiler;

public class JavaDecompilerPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{

	class CheckFieldEditor extends BooleanFieldEditor
	{

		public CheckFieldEditor( String name, String label, Composite parent )
		{
			super( name, label, parent );
		}

		protected void fireStateChanged( String property, boolean oldValue,
				boolean newValue )
		{
			fireValueChanged( property,
					oldValue ? Boolean.TRUE : Boolean.FALSE,
					newValue ? Boolean.TRUE : Boolean.FALSE );
		}

		public void handleSelection( Composite parent )
		{
			boolean isSelected = getChangeControl( parent ).getSelection( );
			valueChanged( false, isSelected );
		}

		protected void valueChanged( boolean oldValue, boolean newValue )
		{
			setPresentsDefaultValue( false );
			fireStateChanged( VALUE, oldValue, newValue );
		}

		public Button getChangeControl( Composite parent )
		{
			return super.getChangeControl( parent );
		}
	}

	private CheckFieldEditor optionLncEditor;
	private CheckFieldEditor alignEditor;
	private CheckFieldEditor eclipseFormatter;
	private CheckFieldEditor eclipseSorter;
	private Group basicGroup;
	private Group formatGroup;
	private Group debugGroup;

	public JavaDecompilerPreferencePage( )
	{
		super( FieldEditorPreferencePage.GRID );
		setPreferenceStore( JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( ) );
	}

	public void createControl( Composite parent )
	{
		super.createControl( parent );
	}

	protected void createFieldEditors( )
	{

		StringChoiceFieldEditor defaultDecompiler = new StringChoiceFieldEditor( JavaDecompilerPlugin.DECOMPILER_TYPE,
				Messages.getString( "JavaDecompilerPreferencePage.Label.DefaultClassDecompiler" ), //$NON-NLS-1$
				getFieldEditorParent( ) ) {

			protected void doFillIntoGrid( Composite parent, int numColumns )
			{
				super.doFillIntoGrid( parent, numColumns );
				GridData gd = (GridData) getControl( ).getLayoutData( );
				gd.widthHint = 200;
				gd.grabExcessHorizontalSpace = false;
				gd.horizontalAlignment = SWT.BEGINNING;
				getControl( ).setLayoutData( gd );
			}
		};

		defaultDecompiler.addItem( DecompilerType.JAD, "Jad" ); //$NON-NLS-1$
		defaultDecompiler.addItem( DecompilerType.JDCORE, "JD-Core" ); //$NON-NLS-1$
		addField( defaultDecompiler );

		basicGroup = new Group( getFieldEditorParent( ), SWT.NONE );
		basicGroup.setText( Messages.getString( "JavaDecompilerPreferencePage.Label.DecompilerSettings" ) ); //$NON-NLS-1$
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = defaultDecompiler.getNumberOfControls( );
		basicGroup.setLayoutData( gd );

		BooleanFieldEditor reusebuf = new BooleanFieldEditor( JavaDecompilerPlugin.REUSE_BUFFER,
				Messages.getString( "JavaDecompilerPreferencePage.Label.ReuseCodeBuffer" ), //$NON-NLS-1$
				basicGroup );
		addField( reusebuf );

		BooleanFieldEditor alwaysUse = new BooleanFieldEditor( JavaDecompilerPlugin.IGNORE_EXISTING,
				Messages.getString( "JavaDecompilerPreferencePage.Label.IgnoreExistSource" ), //$NON-NLS-1$
				basicGroup );
		addField( alwaysUse );

		BooleanFieldEditor showReport = new BooleanFieldEditor( JavaDecompilerPlugin.PREF_DISPLAY_METADATA,
				Messages.getString( "JavaDecompilerPreferencePage.Label.ShowDecompilerReport" ), //$NON-NLS-1$
				basicGroup );
		addField( showReport );

		GridLayout layout = (GridLayout) basicGroup.getLayout( );
		layout.marginWidth = layout.marginHeight = 5;
		basicGroup.layout( );

		formatGroup = new Group( getFieldEditorParent( ), SWT.NONE );
		formatGroup.setText( Messages.getString( "JavaDecompilerPreferencePage.Label.FormatSettings" ) ); //$NON-NLS-1$
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = defaultDecompiler.getNumberOfControls( );
		formatGroup.setLayoutData( gd );

		eclipseFormatter = new CheckFieldEditor( JavaDecompilerPlugin.USE_ECLIPSE_FORMATTER,
				Messages.getString( "JavaDecompilerPreferencePage.Label.UseEclipseFormat" ), //$NON-NLS-1$
				formatGroup );
		addField( eclipseFormatter );

		eclipseSorter = new CheckFieldEditor( JavaDecompilerPlugin.USE_ECLIPSE_SORTER,
				Messages.getString( "JavaDecompilerPreferencePage.Lable.UseEclipseSorter" ), //$NON-NLS-1$
				formatGroup );
		addField( eclipseSorter );

		layout = (GridLayout) formatGroup.getLayout( );
		layout.marginWidth = layout.marginHeight = 5;
		formatGroup.layout( );

		debugGroup = new Group( getFieldEditorParent( ), SWT.NONE );
		debugGroup.setText( Messages.getString( "JavaDecompilerPreferencePage.Label.DebugSettings" ) ); //$NON-NLS-1$
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = defaultDecompiler.getNumberOfControls( );
		debugGroup.setLayoutData( gd );

		optionLncEditor = new CheckFieldEditor( JavaDecompilerPlugin.PREF_DISPLAY_LINE_NUMBERS,
				Messages.getString( "JavaDecompilerPreferencePage.Label.OutputLineNumber" ), //$NON-NLS-1$
				debugGroup );
		addField( optionLncEditor );

		alignEditor = new CheckFieldEditor( JavaDecompilerPlugin.ALIGN,
				Messages.getString( "JavaDecompilerPreferencePage.Label.AlignCode" ), //$NON-NLS-1$
				debugGroup );
		addField( alignEditor );

		layout = (GridLayout) debugGroup.getLayout( );
		layout.marginWidth = layout.marginHeight = 5;
		debugGroup.layout( );

		Group startupGroup = new Group( getFieldEditorParent( ), SWT.NONE );
		startupGroup.setText( Messages.getString( "JavaDecompilerPreferencePage.Label.Startup" ) ); //$NON-NLS-1$ );
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = defaultDecompiler.getNumberOfControls( );
		startupGroup.setLayoutData( gd );

		CheckFieldEditor defaultViewerEditor = new CheckFieldEditor( JavaDecompilerPlugin.DEFAULT_EDITOR,
				Messages.getString( "JavaDecompilerPreferencePage.Label.DefaultEditor" ), //$NON-NLS-1$
				startupGroup );
		addField( defaultViewerEditor );

		layout = (GridLayout) startupGroup.getLayout( );
		layout.marginWidth = layout.marginHeight = 5;
		startupGroup.layout( );

		getFieldEditorParent( ).layout( );
	}

	public void init( IWorkbench arg0 )
	{
	}

	protected void initialize( )
	{
		super.initialize( );
		boolean enabled = getPreferenceStore( ).getBoolean( JadDecompiler.OPTION_LNC );
		alignEditor.setEnabled( enabled, debugGroup );
	}

	protected void performDefaults( )
	{
		super.performDefaults( );
		boolean enabled = Boolean.valueOf( optionLncEditor.getBooleanValue( ) )
				.equals( Boolean.TRUE );
		alignEditor.setEnabled( enabled, debugGroup );
	}

	public void propertyChange( PropertyChangeEvent event )
	{
		if ( event.getSource( ) == optionLncEditor )
		{
			boolean enabled = event.getNewValue( ).equals( Boolean.TRUE );
			alignEditor.setEnabled( enabled, debugGroup );
			if ( enabled )
			{
				( (Button) eclipseFormatter.getChangeControl( formatGroup ) ).setSelection( false );
				( (Button) eclipseSorter.getChangeControl( formatGroup ) ).setSelection( false );
				eclipseFormatter.handleSelection( formatGroup );
				eclipseSorter.handleSelection( formatGroup );
			}
			if ( !enabled )
			{
				( (Button) alignEditor.getChangeControl( debugGroup ) ).setSelection( false );
				alignEditor.handleSelection( debugGroup );
			}
		}
		if ( event.getSource( ) == alignEditor )
		{
			boolean enabled = event.getNewValue( ).equals( Boolean.TRUE );
			if ( enabled )
			{
				( (Button) eclipseFormatter.getChangeControl( formatGroup ) ).setSelection( false );
				( (Button) eclipseSorter.getChangeControl( formatGroup ) ).setSelection( false );
				eclipseFormatter.handleSelection( formatGroup );
				eclipseSorter.handleSelection( formatGroup );
			}
		}
		if ( event.getSource( ) == eclipseFormatter
				|| event.getSource( ) == eclipseSorter )
		{
			boolean enabled = event.getNewValue( ).equals( Boolean.TRUE );
			if ( enabled )
			{
				( (Button) alignEditor.getChangeControl( debugGroup ) ).setSelection( false );
				( (Button) optionLncEditor.getChangeControl( debugGroup ) ).setSelection( false );
				alignEditor.setEnabled( !enabled, debugGroup );
				alignEditor.handleSelection( debugGroup );
				optionLncEditor.handleSelection( debugGroup );
			}
		}
		super.propertyChange( event );
	}

}
