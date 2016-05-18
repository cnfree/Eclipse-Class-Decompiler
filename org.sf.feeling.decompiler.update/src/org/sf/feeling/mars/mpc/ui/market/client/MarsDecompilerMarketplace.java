
package org.sf.feeling.mars.mpc.ui.market.client;

import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.epp.internal.mpc.core.service.DefaultMarketplaceService;
import org.eclipse.epp.internal.mpc.core.service.Node;
import org.sf.feeling.decompiler.update.tester.OperationTester;

public class MarsDecompilerMarketplace
{

	public static boolean isInteresting( )
	{
		try
		{
			Class.forName( "org.eclipse.epp.mpc.ui.Operation" );
			if ( OperationTester.supportMars( ) )
				return true;
			else
				return false;
		}
		catch ( ClassNotFoundException e )
		{
			return false;
		}
	}

	public static String getUpdateUrl( IProgressMonitor monitor ) throws Exception
	{
		DefaultMarketplaceService service = new DefaultMarketplaceService(
				new URL( "http://marketplace.eclipse.org" ) ); //$NON-NLS-1$
		Node node = new Node( );
		node.setId( "472922" ); //$NON-NLS-1$
		node = service.getNode( node, monitor );
		return node.getUpdateurl( );
	}
}
