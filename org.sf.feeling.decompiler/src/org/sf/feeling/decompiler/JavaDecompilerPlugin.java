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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.sf.feeling.decompiler.editor.DecompilerType;
import org.sf.feeling.decompiler.editor.IDecompilerDescriptor;
import org.sf.feeling.decompiler.editor.JavaDecompilerBufferManager;
import org.sf.feeling.decompiler.extension.DecompilerAdapterManager;
import org.sf.feeling.decompiler.update.IDecompilerUpdateHandler;
import org.sf.feeling.decompiler.util.SortMemberUtil;

public class JavaDecompilerPlugin extends AbstractUIPlugin implements
		IPropertyChangeListener
{

	public static final String EDITOR_ID = "org.sf.feeling.decompiler.ClassFileEditor"; //$NON-NLS-1$
	public static final String PLUGIN_ID = "org.sf.feeling.decompiler"; //$NON-NLS-1$
	public static final String TEMP_DIR = "org.sf.feeling.decompiler.tempd"; //$NON-NLS-1$

	public static final String REUSE_BUFFER = "org.sf.feeling.decompiler.reusebuff"; //$NON-NLS-1$
	public static final String IGNORE_EXISTING = "org.sf.feeling.decompiler.alwaysuse"; //$NON-NLS-1$
	public static final String USE_ECLIPSE_FORMATTER = "org.sf.feeling.decompiler.use_eclipse_formatter"; //$NON-NLS-1$
	public static final String USE_ECLIPSE_SORTER = "org.sf.feeling.decompiler.use_eclipse_sorter"; //$NON-NLS-1$
	public static final String DECOMPILER_TYPE = "org.sf.feeling.decompiler.type"; //$NON-NLS-1$
	public static final String PREF_DISPLAY_LINE_NUMBERS = "jd.ide.eclipse.prefs.DisplayLineNumbers"; //$NON-NLS-1$
	public static final String DECOMPILE_COUNT = "decompile.count"; //$NON-NLS-1$
	public static final String PREF_DISPLAY_METADATA = "jd.ide.eclipse.prefs.DisplayMetadata"; //$NON-NLS-1$
	public static final String ALIGN = "jd.ide.eclipse.prefs.RealignLineNumbers"; //$NON-NLS-1$
	public static final String DEFAULT_EDITOR = "org.sf.feeling.decompiler.default_editor"; //$NON-NLS-1$ ;
	public static final String CHECK_UPDATE = "org.sf.feeling.decompiler.check_update"; //$NON-NLS-1$ ;

	private boolean isDebugMode = false;

	private static JavaDecompilerPlugin plugin;

	private IPreferenceStore preferenceStore;

	private TreeMap<String, IDecompilerDescriptor> decompilerDescriptorMap = new TreeMap<String, IDecompilerDescriptor>( );

	private AtomicInteger decompileCount = new AtomicInteger( 0 );

	public AtomicInteger getDecompileCount( )
	{
		return decompileCount;
	}

	public Map<String, IDecompilerDescriptor> getDecompilerDescriptorMap( )
	{
		return decompilerDescriptorMap;
	}

	public String[] getDecompilerDescriptorTypes( )
	{
		return decompilerDescriptorMap.keySet( ).toArray( new String[0] );
	}

	public IDecompilerDescriptor getDecompilerDescriptor( String decompilerType )
	{
		return decompilerDescriptorMap.get( decompilerType );
	}

	public static JavaDecompilerPlugin getDefault( )
	{
		return plugin;
	}

	public static void logError( Throwable t, String message )
	{
		JavaDecompilerPlugin.getDefault( )
				.getLog( )
				.log( new Status( Status.ERROR, PLUGIN_ID, 0, message, t ) );
	}

	public static void log( int severity, Throwable t, String message )
	{
		JavaDecompilerPlugin.getDefault( )
				.getLog( )
				.log( new Status( severity, PLUGIN_ID, 0, message, t ) );
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
		Object[] decompilerAdapters = DecompilerAdapterManager.getAdapters( this,
				IDecompilerDescriptor.class );

		if ( decompilerAdapters != null )
		{
			for ( int i = 0; i < decompilerAdapters.length; i++ )
			{
				Object adapter = decompilerAdapters[i];
				if ( adapter instanceof IDecompilerDescriptor )
				{
					IDecompilerDescriptor descriptor = (IDecompilerDescriptor) adapter;
					if ( descriptor.isEnabled( ) )
					{
						decompilerDescriptorMap.put( descriptor.getDecompilerType( ),
								descriptor );
					}
				}
			}
		}

		store.setDefault( TEMP_DIR, System.getProperty( "java.io.tmpdir" ) //$NON-NLS-1$
				+ File.separator
				+ ".org.sf.feeling.decompiler" ); //$NON-NLS-1$
		store.setDefault( REUSE_BUFFER, true );
		store.setDefault( IGNORE_EXISTING, false );
		store.setDefault( USE_ECLIPSE_FORMATTER, true );
		store.setDefault( USE_ECLIPSE_SORTER, false );
		store.setDefault( PREF_DISPLAY_METADATA, false );
		store.setDefault( DECOMPILER_TYPE, getDefalutDecompilerType( ) );
		store.setDefault( DEFAULT_EDITOR, true );
		store.setDefault( CHECK_UPDATE, true );
		store.setDefault( DECOMPILE_COUNT, 0 );
	}

	public void propertyChange( PropertyChangeEvent event )
	{
		if ( event.getProperty( ).equals( IGNORE_EXISTING ) )
			JavaDecompilerBufferManager.closeDecompilerBuffers( false );
	}

	public void start( BundleContext context ) throws Exception
	{
		super.start( context );
		getPreferenceStore( ).addPropertyChangeListener( this );

		decompileCount.set( getPreferenceStore( ).getInt( DECOMPILE_COUNT ) );

		SortMemberUtil.deleteDecompilerProject( );
		Display.getDefault( ).asyncExec( new SetupRunnable( ) );
	}

	public IPreferenceStore getPreferenceStore( )
	{
		if ( preferenceStore == null )
		{
			preferenceStore = super.getPreferenceStore( );

			String decompilerType = preferenceStore.getString( DECOMPILER_TYPE );
			if ( !DecompilerType.FernFlower.equals( decompilerType ) )
			{
				IDecompilerDescriptor descriptor = getDecompilerDescriptor( decompilerType );
				if ( descriptor == null )
				{
					preferenceStore.setValue( DECOMPILER_TYPE,
							getDefalutDecompilerType( ) );
				}
			}

		}
		return preferenceStore;
	}

	public void stop( BundleContext context ) throws Exception
	{
		getPreferenceStore( ).setValue( DECOMPILE_COUNT, decompileCount.get( ) );
		super.stop( context );
		getPreferenceStore( ).removePropertyChangeListener( this );
		plugin = null;
	}

	public Boolean isDisplayLineNumber( )
	{
		return Boolean.valueOf( getPreferenceStore( ).getBoolean( PREF_DISPLAY_LINE_NUMBERS ) );
	}

	public void displayLineNumber( Boolean display )
	{
		getPreferenceStore( ).setValue( PREF_DISPLAY_LINE_NUMBERS,
				display.booleanValue( ) );
	}

	public boolean enableCheckUpdateSetting( )
	{
		Object updateAdapter = DecompilerAdapterManager.getAdapter( this,
				IDecompilerUpdateHandler.class );
		if ( updateAdapter instanceof IDecompilerUpdateHandler )
		{
			IDecompilerUpdateHandler updateHandler = (IDecompilerUpdateHandler) updateAdapter;
			return !updateHandler.isForce( null );
		}
		return false;
	}

	public String getDefalutDecompilerType( )
	{
		Collection<IDecompilerDescriptor> descriptors = JavaDecompilerPlugin.getDefault( )
				.getDecompilerDescriptorMap( )
				.values( );
		if ( descriptors != null )
		{
			for ( Iterator iterator = descriptors.iterator( ); iterator.hasNext( ); )
			{
				IDecompilerDescriptor iDecompilerDescriptor = (IDecompilerDescriptor) iterator.next( );
				if ( iDecompilerDescriptor.isDefault( ) )
				{
					return iDecompilerDescriptor.getDecompilerType( );
				}
			}
		}
		return DecompilerType.FernFlower;
	}

	public boolean isDebugMode( )
	{
		return isDebugMode;
	}

	public void setDebugMode( boolean isDebugMode )
	{
		this.isDebugMode = isDebugMode;
	}

	public void resetDecompileCount( )
	{
		decompileCount.set( 0 );
		getPreferenceStore( ).setValue( DECOMPILE_COUNT, decompileCount.get( ) );
	}
}