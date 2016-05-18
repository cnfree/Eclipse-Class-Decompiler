
package org.sf.feeling.neon.mpc.ui.market.client;

import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.epp.internal.mpc.core.model.Node;
import org.eclipse.epp.internal.mpc.core.service.DefaultMarketplaceService;

public class NeonDecompilerMarketplace
{

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
