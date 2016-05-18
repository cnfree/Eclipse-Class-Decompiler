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

package org.sf.feeling.mars.mpc.ui.market.client;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "org.eclipse.epp.mpc.ui.messages"; //$NON-NLS-1$
	public static String CatalogDescriptor_badUri;
	public static String CatalogDescriptor_downloadError;
	public static String CatalogDescriptor_requestCatalog;
	public static String MarketplaceUrlHandler_cannotOpenMarketplaceWizard;
	public static String MarketplaceUrlHandler_performInstallRequest;
	public static String Operation_change;
	public static String Operation_install;
	public static String Operation_uninstall;
	public static String Operation_update;

	static
	{
		NLS.initializeMessages( BUNDLE_NAME, Messages.class );
	}

	private Messages( )
	{
	}
}