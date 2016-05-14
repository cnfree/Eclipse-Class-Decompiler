
package org.sf.feeling.decompiler.procyon;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;

public class ProcyonDecompilerPlugin extends AbstractUIPlugin implements
		IPropertyChangeListener
{

	public static final String PLUGIN_ID = "org.sf.feeling.decompiler.procyon"; //$NON-NLS-1$

	public static final String decompilerType = "Procyon";

	private static ProcyonDecompilerPlugin plugin;

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
		}
		return preferenceStore;
	}

	public static ProcyonDecompilerPlugin getDefault( )
	{
		return plugin;
	}

	public ProcyonDecompilerPlugin( )
	{
		plugin = this;
	}

	public static ImageDescriptor getImageDescriptor( String path )
	{
		URL base = ProcyonDecompilerPlugin.getDefault( )
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
