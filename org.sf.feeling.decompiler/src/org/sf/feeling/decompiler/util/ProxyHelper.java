/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/

package org.sf.feeling.decompiler.util;

import java.lang.reflect.Field;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IStatus;
import org.osgi.util.tracker.ServiceTracker;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;

public class ProxyHelper
{

	private static ProxyAuthenticator authenticator;
	private static ServiceTracker proxyServiceTracker;

	private static class ProxyAuthenticator extends Authenticator
	{

		private final Authenticator delegate;

		public ProxyAuthenticator( Authenticator delegate )
		{
			if ( delegate instanceof ProxyAuthenticator )
			{
				delegate = ( (ProxyAuthenticator) delegate ).getDelegate( );
			}
			this.delegate = delegate;
		}

		public Authenticator getDelegate( )
		{
			return this.delegate;
		}

		protected PasswordAuthentication getPasswordAuthentication( )
		{
			if ( getRequestorType( ) == Authenticator.RequestorType.PROXY )
			{
				IProxyService proxyService = ProxyHelper.getProxyService( );
				if ( ( proxyService != null )
						&& ( proxyService.isProxiesEnabled( ) ) )
				{
					URL requestingURL = getRequestingURL( );
					IProxyData[] proxies;
					try
					{
						proxies = proxyService.select( requestingURL.toURI( ) );
					}
					catch ( URISyntaxException localURISyntaxException )
					{
						proxies = proxyService.getProxyData( );
					}
					for ( IProxyData proxyData : proxies )
					{
						if ( ( proxyData.isRequiresAuthentication( ) )
								&& ( proxyData
										.getPort( ) == getRequestingPort( ) )
								&& ( proxyData.getHost( )
										.equals( getRequestingHost( ) ) ) )
						{
							return new PasswordAuthentication(
									proxyData.getUserId( ),
									proxyData.getPassword( ).toCharArray( ) );
						}
					}
				}
			}
			if ( this.delegate != null )
			{
				try
				{
					Authenticator.setDefault( this.delegate );
					Authenticator.requestPasswordAuthentication(
							getRequestingHost( ),
							getRequestingSite( ),
							getRequestingPort( ),
							getRequestingProtocol( ),
							getRequestingPrompt( ),
							getRequestingScheme( ),
							getRequestingURL( ),
							getRequestorType( ) );
				}
				finally
				{
					Authenticator.setDefault( this );
				}
			}
			return null;
		}
	}

	public static synchronized void acquireProxyService( )
	{
		if ( proxyServiceTracker == null )
		{
			proxyServiceTracker = new ServiceTracker(
					JavaDecompilerPlugin.getDefault( )
							.getBundle( )
							.getBundleContext( ),
					IProxyService.class.getName( ),
					null );
			proxyServiceTracker.open( );
		}
		Authenticator defaultAuthenticator = getDefaultAuthenticator( );
		if ( ( authenticator == null )
				|| ( authenticator != defaultAuthenticator ) )
		{
			authenticator = new ProxyAuthenticator( defaultAuthenticator );
			Authenticator.setDefault( authenticator );
		}
	}

	private static Authenticator getDefaultAuthenticator( )
	{
		try
		{
			Field authenticatorField = Authenticator.class
					.getDeclaredField( "theAuthenticator" ); //$NON-NLS-1$
			boolean accessible = authenticatorField.isAccessible( );
			try
			{
				if ( !( accessible ) )
				{
					authenticatorField.setAccessible( true );
				}
				return ( (Authenticator) authenticatorField.get( null ) );
			}
			finally
			{
				if ( !( accessible ) )
					authenticatorField.setAccessible( false );
			}
		}
		catch ( Exception e )
		{
			JavaDecompilerPlugin.log( IStatus.WARNING,
					e,
					"Unable to read default network authenticator - existing authenticator will be replaced" ); //$NON-NLS-1$
		}
		return null;
	}

	public static IProxyData getProxyData( String url )
	{
		IProxyService proxyService = getProxyService( );
		if ( proxyService != null )
		{
			URI uri;
			try
			{
				uri = new URI( url );
			}
			catch ( URISyntaxException e )
			{
				throw new IllegalArgumentException( e.getMessage( ), e );
			}
			IProxyData[] proxyData = proxyService.select( uri );
			if ( ( proxyData != null )
					&& ( proxyData.length > 0 )
					&& ( proxyData[0] != null ) )
			{
				IProxyData pd = proxyData[0];
				return pd;
			}
		}
		return null;
	}

	protected static IProxyService getProxyService( )
	{
		return ( ( proxyServiceTracker == null ) ? null
				: (IProxyService) proxyServiceTracker.getService( ) );
	}

	public static synchronized void releaseProxyService( )
	{
		Authenticator defaultAuthenticator = getDefaultAuthenticator( );
		if ( authenticator != null )
		{
			if ( defaultAuthenticator == authenticator )
			{
				Authenticator.setDefault( authenticator.getDelegate( ) );
			}
			authenticator = null;
		}
		if ( proxyServiceTracker != null )
			proxyServiceTracker.close( );
	}
}