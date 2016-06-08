
package org.sf.feeling.decompiler.util;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.core.net.proxy.IProxyData;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;

public class HttpUtil
{

	public static void configureProxy( HttpClient client, String url )
	{
		IProxyData proxyData = ProxyHelper.getProxyData( url );
		if ( ( proxyData != null )
				&& ( !( IProxyData.SOCKS_PROXY_TYPE.equals( proxyData.getType( ) ) ) ) )
		{
			HttpHost proxy = new HttpHost( proxyData.getHost( ),
					proxyData.getPort( ),
					proxyData.getType( ).toLowerCase( ) );
			client.getParams( ).setParameter( "http.route.default-proxy", //$NON-NLS-1$
					proxy );

			if ( proxyData.isRequiresAuthentication( ) )
				( (AbstractHttpClient) client ).getCredentialsProvider( )
						.setCredentials(
								new AuthScope( proxyData.getHost( ),
										proxyData.getPort( ) ),
								new UsernamePasswordCredentials(
										proxyData.getUserId( ),
										proxyData.getPassword( ) ) );
		}
	}

	public static HttpClient createHttpClient( String baseUri )
	{
		HttpClient client = new DefaultHttpClient( );
		client.getParams( ).setParameter( "http.useragent", //$NON-NLS-1$
				JavaDecompilerPlugin.PLUGIN_ID );

		if ( baseUri != null )
		{
			configureProxy( client, baseUri );
		}

		return client;
	}
}