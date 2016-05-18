
package org.sf.feeling.mars.mpc.ui.market.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.epp.internal.mpc.ui.wizards.InstallProfile;
import org.eclipse.epp.internal.mpc.ui.wizards.SelectionModel;
import org.eclipse.equinox.internal.p2.discovery.model.CatalogItem;

public class DecompilerSelectionModel extends SelectionModel
{

	public DecompilerSelectionModel( InstallProfile installProfile )
	{
		super( installProfile );
	}

	public Set<CatalogItem> getSelectedCatalogItems( )
	{
		Set<CatalogItem> items = new HashSet<CatalogItem>( );
		for ( CatalogItemEntry entry : getCatalogItemEntries( ) )
		{
			if ( entry.getSelectedOperation( ) != org.eclipse.epp.mpc.ui.Operation.NONE )
			{
				for ( FeatureEntry featureEntry : entry.getChildren( ) )
				{
					featureEntry.setChecked( true );
					featureEntry.getInstallableUnitItem( ).setUpdateAvailable( true );
					org.eclipse.epp.mpc.ui.Operation operation = featureEntry.computeChangeOperation( );
					if ( ( operation != null ) && ( operation != org.eclipse.epp.mpc.ui.Operation.NONE ) )
					{
						items.add( getEntryItem( entry ) );
					}
				}
				if ( entry.getSelectedOperation( ) == org.eclipse.epp.mpc.ui.Operation.CHANGE )
				{
					items.add( getEntryItem( entry ) );
				}
			}
		}
		return Collections.unmodifiableSet( items );
	}

	private CatalogItem getEntryItem( CatalogItemEntry entry )
	{
		return entry.getItem( );
	}

}
