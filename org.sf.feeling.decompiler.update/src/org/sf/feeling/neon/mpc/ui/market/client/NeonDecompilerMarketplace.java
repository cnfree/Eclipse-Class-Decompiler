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
