
package org.sf.feeling.decompiler.jad;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.jad.decompiler.JadDecompiler;
import org.sf.feeling.decompiler.jad.decompiler.JadLoader;

public class JadDecompilerPlugin extends AbstractUIPlugin implements
		IPropertyChangeListener
{

	public static final String PLUGIN_ID = "org.sf.feeling.decompiler.jad"; //$NON-NLS-1$

	public static final String CMD = "org.sf.feeling.decompiler.jad.cmd"; //$NON-NLS-1$

	public static final String decompilerType = "Jad";

	private static JadDecompilerPlugin plugin;

	private IPreferenceStore preferenceStore;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start( BundleContext context ) throws Exception
	{
		super.start( context );
		getPreferenceStore( ).addPropertyChangeListener( this );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop( BundleContext context ) throws Exception
	{
		super.stop( context );
		getPreferenceStore( ).removePropertyChangeListener( this );
		plugin = null;
	}

	public void propertyChange( PropertyChangeEvent event )
	{

	}

	public IPreferenceStore getPreferenceStore( )
	{
		if ( preferenceStore == null )
		{
			preferenceStore = JavaDecompilerPlugin.getDefault( )
					.getPreferenceStore( );

			IPreferenceStore decompilerStore = preferenceStore;
			String jad = JadLoader.loadJad( );
			if ( jad != null )
				decompilerStore.setDefault( CMD, jad );

			decompilerStore.setDefault( JadDecompiler.OPTION_INDENT_SPACE, 4 );
			decompilerStore.setDefault( JadDecompiler.OPTION_IRADIX, 10 );
			decompilerStore.setDefault( JadDecompiler.OPTION_LRADIX, 10 );
			decompilerStore.setDefault( JadDecompiler.OPTION_SPLITSTR_MAX, 0 );
			decompilerStore.setDefault( JadDecompiler.OPTION_PI, 0 );
			decompilerStore.setDefault( JadDecompiler.OPTION_PV, 0 );
			decompilerStore.setDefault( JadDecompiler.OPTION_FIELDSFIRST, true );
			decompilerStore.setDefault( JadDecompiler.OPTION_NOCTOR, true );
			decompilerStore.setDefault( JadDecompiler.OPTION_ANSI, false );

		}
		return preferenceStore;
	}

	public static JadDecompilerPlugin getDefault( )
	{
		return plugin;
	}

	public JadDecompilerPlugin( )
	{
		plugin = this;
	}

	public static ImageDescriptor getImageDescriptor( String path )
	{
		URL base = JadDecompilerPlugin.getDefault( )
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

}
