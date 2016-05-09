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

package org.sf.feeling.decompiler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.sf.feeling.decompiler.editor.DecompilerType;
import org.sf.feeling.decompiler.editor.JavaDecompilerBufferManager;
import org.sf.feeling.decompiler.jad.JadDecompiler;
import org.sf.feeling.decompiler.jad.JadLoader;
import org.sf.feeling.decompiler.util.SortMemberUtil;

public class JavaDecompilerPlugin extends AbstractUIPlugin
		implements IPropertyChangeListener
{

	public static final String EDITOR_ID = "net.sf.feeling.decompiler.ClassFileEditor"; //$NON-NLS-1$

	public static final String PLUGIN_ID = "net.sf.feeling.decompiler"; //$NON-NLS-1$

	public static final String TEMP_DIR = "net.sf.feeling.decompiler.tempd"; //$NON-NLS-1$
	public static final String CMD = "net.sf.feeling.decompiler.cmd"; //$NON-NLS-1$
	public static final String REUSE_BUFFER = "net.sf.feeling.decompiler.reusebuff"; //$NON-NLS-1$
	public static final String IGNORE_EXISTING = "net.sf.feeling.decompiler.alwaysuse"; //$NON-NLS-1$
	public static final String USE_ECLIPSE_FORMATTER = "net.sf.feeling.decompiler.use_eclipse_formatter"; //$NON-NLS-1$
	public static final String USE_ECLIPSE_SORTER = "net.sf.feeling.decompiler.use_eclipse_sorter"; //$NON-NLS-1$
	public static final String DECOMPILER_TYPE = "net.sf.feeling.decompiler.type"; //$NON-NLS-1$
	public static final String PREF_DISPLAY_LINE_NUMBERS = jd.ide.eclipse.JavaDecompilerPlugin.PREF_DISPLAY_LINE_NUMBERS;
	public static final String PREF_DISPLAY_METADATA = jd.ide.eclipse.JavaDecompilerPlugin.PREF_DISPLAY_METADATA;
	public static final String ALIGN = jd.ide.eclipse.JavaDecompilerPlugin.ALIGN;
	public static final String DEFAULT_EDITOR = "net.sf.feeling.decompiler.default_editor"; //$NON-NLS-1$ ;

	private static JavaDecompilerPlugin plugin;

	public static JavaDecompilerPlugin getDefault( )
	{
		return plugin;
	}

	public static void logError( Throwable t, String message )
	{
		JavaDecompilerPlugin.getDefault( ).getLog( ).log(
				new Status( Status.ERROR, PLUGIN_ID, 0, message, t ) );
	}

	public static void log( int severity, Throwable t, String message )
	{
		JavaDecompilerPlugin.getDefault( ).getLog( ).log(
				new Status( severity, PLUGIN_ID, 0, message, t ) );
	}

	public static ImageDescriptor getImageDescriptor( String path )
	{
		URL base = JavaDecompilerPlugin.getDefault( )
				.getBundle( )
				.getEntry( "/" ); //$NON-NLS-1$
		URL url = null;
		try
		{
			url = new URL( base, path ); // $NON-NLS-1$
		}
		catch ( MalformedURLException e )
		{
			e.printStackTrace( );
		}
		ImageDescriptor actionIcon = null;
		if ( url != null )
			actionIcon = ImageDescriptor.createFromURL( url );
		return actionIcon;
	}

	public JavaDecompilerPlugin( )
	{
		plugin = this;
	}

	protected void initializeDefaultPreferences( IPreferenceStore store )
	{
		String jad = JadLoader.loadJad( );
		if ( jad != null )
			store.setDefault( CMD, jad );
		store.setDefault( TEMP_DIR, System.getProperty( "java.io.tmpdir" ) //$NON-NLS-1$
				+ File.separator
				+ ".net.sf.feeling.decompiler" ); //$NON-NLS-1$
		store.setDefault( REUSE_BUFFER, true );
		store.setDefault( IGNORE_EXISTING, false );
		store.setDefault( USE_ECLIPSE_FORMATTER, true );
		store.setDefault( USE_ECLIPSE_SORTER, false );
		store.setDefault( PREF_DISPLAY_METADATA, false );
		store.setDefault( DECOMPILER_TYPE, DecompilerType.JDCORE );
		store.setDefault( DEFAULT_EDITOR, true );

		store.setDefault( JadDecompiler.OPTION_INDENT_SPACE, 4 );
		store.setDefault( JadDecompiler.OPTION_IRADIX, 10 );
		store.setDefault( JadDecompiler.OPTION_LRADIX, 10 );
		store.setDefault( JadDecompiler.OPTION_SPLITSTR_MAX, 0 );
		store.setDefault( JadDecompiler.OPTION_PI, 0 );
		store.setDefault( JadDecompiler.OPTION_PV, 0 );
		store.setDefault( JadDecompiler.OPTION_FIELDSFIRST, true );
		store.setDefault( JadDecompiler.OPTION_NOCTOR, true );
	}

	public void propertyChange( PropertyChangeEvent event )
	{
		if ( event.getProperty( ).equals( IGNORE_EXISTING )
				&& event.getNewValue( ).equals( Boolean.FALSE ) )
			JavaDecompilerBufferManager.closeDecompilerBuffers( false );
	}

	public void start( BundleContext context ) throws Exception
	{
		super.start( context );
		getPreferenceStore( ).addPropertyChangeListener( this );
		SortMemberUtil.deleteDecompilerProject( );
	}

	public void stop( BundleContext context ) throws Exception
	{
		super.stop( context );
		getPreferenceStore( ).removePropertyChangeListener( this );
		plugin = null;
	}

	public Boolean isDisplayLineNumber( )
	{
		return Boolean.valueOf(
				getPreferenceStore( ).getBoolean( PREF_DISPLAY_LINE_NUMBERS ) );
	}

	public void displayLineNumber( Boolean display )
	{
		getPreferenceStore( ).setValue( PREF_DISPLAY_LINE_NUMBERS,
				display.booleanValue( ) );
	}
}