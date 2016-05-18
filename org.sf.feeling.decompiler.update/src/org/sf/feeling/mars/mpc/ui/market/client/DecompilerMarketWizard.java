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

import org.eclipse.epp.internal.mpc.ui.catalog.MarketplaceCatalog;
import org.eclipse.epp.internal.mpc.ui.wizards.MarketplaceCatalogConfiguration;
import org.eclipse.epp.internal.mpc.ui.wizards.MarketplaceWizard;
import org.sf.feeling.decompiler.util.ReflectionUtils;

public class DecompilerMarketWizard extends MarketplaceWizard
{

	public DecompilerMarketWizard( MarketplaceCatalog catalog, MarketplaceCatalogConfiguration configuration )
	{
		super( catalog, configuration );
		updateSelectionModel( );
	}

	private void updateSelectionModel( )
	{
		DecompilerSelectionModel model = new DecompilerSelectionModel( this );
		ReflectionUtils.setFieldValue( this, "selectionModel", model ); //$NON-NLS-1$
	}

}
