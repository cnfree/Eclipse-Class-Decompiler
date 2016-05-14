
package org.sf.feeling.decompiler.jd;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;

public class JDCoreDecompilerPlugin extends AbstractUIPlugin implements
		IPropertyChangeListener
{

	public static final String PLUGIN_ID = "org.sf.feeling.decompiler.jd"; //$NON-NLS-1$

	public static final String CMD = "org.sf.feeling.decompiler.jd.cmd"; //$NON-NLS-1$

	public static final String decompilerType = "JD-Core";

	private static JDCoreDecompilerPlugin plugin;

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

	public static JDCoreDecompilerPlugin getDefault( )
	{
		return plugin;
	}

	public JDCoreDecompilerPlugin( )
	{
		plugin = this;
	}

	public static ImageDescriptor getImageDescriptor( String path )
	{
		URL base = JDCoreDecompilerPlugin.getDefault( ).getBundle( ).getEntry( "/" ); //$NON-NLS-1$
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
