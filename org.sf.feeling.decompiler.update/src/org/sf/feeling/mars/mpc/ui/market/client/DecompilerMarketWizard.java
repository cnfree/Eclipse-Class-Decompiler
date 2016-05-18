
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
		ReflectionUtils.setFieldValue( this, "selectionModel", model );
	}

}
