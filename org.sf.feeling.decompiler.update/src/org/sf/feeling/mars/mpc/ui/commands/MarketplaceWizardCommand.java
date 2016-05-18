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

package org.sf.feeling.mars.mpc.ui.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.epp.internal.mpc.core.MarketplaceClientCore;
import org.eclipse.epp.internal.mpc.ui.CatalogRegistry;
import org.eclipse.epp.internal.mpc.ui.MarketplaceClientUi;
import org.eclipse.epp.internal.mpc.ui.MarketplaceClientUiPlugin;
import org.eclipse.epp.internal.mpc.ui.catalog.MarketplaceCatalog;
import org.eclipse.epp.internal.mpc.ui.catalog.ResourceProvider;
import org.eclipse.epp.internal.mpc.ui.wizards.AbstractTagFilter;
import org.eclipse.epp.internal.mpc.ui.wizards.ComboTagFilter;
import org.eclipse.epp.internal.mpc.ui.wizards.MarketplaceCatalogConfiguration;
import org.eclipse.epp.internal.mpc.ui.wizards.MarketplaceFilter;
import org.eclipse.epp.internal.mpc.ui.wizards.MarketplaceWizard;
import org.eclipse.epp.internal.mpc.ui.wizards.MarketplaceWizard.WizardState;
import org.eclipse.epp.internal.mpc.ui.wizards.MarketplaceWizardDialog;
import org.eclipse.epp.mpc.core.model.ICatalog;
import org.eclipse.epp.mpc.core.model.ICategory;
import org.eclipse.epp.mpc.core.model.IMarket;
import org.eclipse.epp.mpc.core.service.ICatalogService;
import org.eclipse.epp.mpc.core.service.ServiceHelper;
import org.eclipse.epp.mpc.ui.CatalogDescriptor;
import org.eclipse.epp.mpc.ui.IMarketplaceClientConfiguration;
import org.eclipse.epp.mpc.ui.Operation;
import org.eclipse.equinox.internal.p2.discovery.DiscoveryCore;
import org.eclipse.equinox.internal.p2.discovery.model.Tag;
import org.eclipse.equinox.internal.p2.ui.discovery.util.WorkbenchUtil;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogFilter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.sf.feeling.mars.mpc.ui.market.client.DecompilerMarketWizard;

/**
 * @author David Green
 * @author Carsten Reckord
 */
public class MarketplaceWizardCommand extends AbstractHandler implements IHandler
{

	private List<CatalogDescriptor> catalogDescriptors;

	private CatalogDescriptor selectedCatalogDescriptor;

	private String wizardState;

	private Map<String, Operation> operations;

	private WizardState wizardDialogState;

	public Object execute( ExecutionEvent event ) throws ExecutionException
	{
		final MarketplaceCatalog catalog = new MarketplaceCatalog( );

		catalog.setEnvironment( DiscoveryCore.createEnvironment( ) );
		catalog.setVerifyUpdateSiteAvailability( false );

		MarketplaceCatalogConfiguration configuration = new MarketplaceCatalogConfiguration( );
		configuration.setVerifyUpdateSiteAvailability( false );

		if ( catalogDescriptors == null || catalogDescriptors.isEmpty( ) )
		{
			final IStatus remoteCatalogStatus = installRemoteCatalogs( );
			configuration.getCatalogDescriptors( ).addAll( CatalogRegistry.getInstance( ).getCatalogDescriptors( ) );
			if ( configuration.getCatalogDescriptors( ).isEmpty( ) )
			{
				// doesn't make much sense to continue without catalogs.
				// nothing will work and no way to recover later
				IStatus cause;
				if ( !remoteCatalogStatus.isOK( ) )
				{
					cause = remoteCatalogStatus;
				}
				else
				{
					cause = new Status( IStatus.ERROR,
							MarketplaceClientUi.BUNDLE_ID,
							Messages.MarketplaceWizardCommand_noRemoteCatalogs );
				}
				IStatus exitStatus = new Status( IStatus.ERROR,
						MarketplaceClientUi.BUNDLE_ID,
						cause.getCode( ),
						Messages.MarketplaceWizardCommand_cannotOpenMarketplace,
						new CoreException( cause ) );
				try
				{
					MarketplaceClientUi.handle( exitStatus,
							StatusManager.SHOW
									| StatusManager.BLOCK
									| ( exitStatus.getSeverity( ) == IStatus.CANCEL ? 0 : StatusManager.LOG ) );
				}
				catch ( Exception ex )
				{
					// HOTFIX for bug 477269 - Display might get disposed during
					// call to handle due to workspace shutdown or similar.
					// In that case, just log...
					MarketplaceClientUi.getLog( ).log( exitStatus );
				}
				return null;
			}
			else if ( !remoteCatalogStatus.isOK( ) )
			{
				MarketplaceClientUi.handle( remoteCatalogStatus, StatusManager.LOG );
			}
		}
		else
		{
			configuration.getCatalogDescriptors( ).addAll( catalogDescriptors );
		}
		if ( selectedCatalogDescriptor != null )
		{
			if ( selectedCatalogDescriptor.getLabel( ).equals( "org.eclipse.epp.mpc.descriptorHint" ) ) //$NON-NLS-1$
			{
				CatalogDescriptor resolvedDescriptor = CatalogRegistry.getInstance( )
						.findCatalogDescriptor( selectedCatalogDescriptor.getUrl( ).toExternalForm( ) );
				if ( resolvedDescriptor == null )
				{
					IStatus status = new Status( IStatus.ERROR,
							MarketplaceClientUi.BUNDLE_ID,
							Messages.MarketplaceWizardCommand_CouldNotFindMarketplaceForSolution,
							new ExecutionException( selectedCatalogDescriptor.getUrl( ).toExternalForm( ) ) );
					MarketplaceClientUi.handle( status, StatusManager.SHOW | StatusManager.BLOCK | StatusManager.LOG );
					return null;
				}
				else
				{
					configuration.setCatalogDescriptor( resolvedDescriptor );
				}
			}
			else
			{
				configuration.setCatalogDescriptor( selectedCatalogDescriptor );
			}
		}

		configuration.getFilters( ).clear( );

		final ComboTagFilter marketFilter = new ComboTagFilter( ) {

			@Override
			public void catalogUpdated( boolean wasCancelled )
			{
				List<Tag> choices = new ArrayList<Tag>( );
				List<IMarket> markets = catalog.getMarkets( );
				for ( IMarket market : markets )
				{
					Tag marketTag = new Tag( IMarket.class, market.getId( ), market.getName( ) );
					marketTag.setData( market );
					choices.add( marketTag );
				}
				setChoices( choices );
			}
		};
		marketFilter.setSelectAllOnNoSelection( true );
		marketFilter.setNoSelectionLabel( Messages.MarketplaceWizardCommand_allMarkets );
		marketFilter.setTagClassification( ICategory.class );
		marketFilter.setChoices( new ArrayList<Tag>( ) );

		final ComboTagFilter marketCategoryTagFilter = new ComboTagFilter( ) {

			@Override
			public void catalogUpdated( boolean wasCancelled )
			{
				updateCategoryChoices( this, marketFilter );
			}
		};
		marketCategoryTagFilter.setSelectAllOnNoSelection( true );
		marketCategoryTagFilter.setNoSelectionLabel( Messages.MarketplaceWizardCommand_allCategories );
		marketCategoryTagFilter.setTagClassification( ICategory.class );
		marketCategoryTagFilter.setChoices( new ArrayList<Tag>( ) );

		final IPropertyChangeListener marketListener = new IPropertyChangeListener( ) {

			public void propertyChange( PropertyChangeEvent event )
			{
				final String property = event.getProperty( );
				if ( AbstractTagFilter.PROP_SELECTED.equals( property ) )
				{
					updateCategoryChoices( marketCategoryTagFilter, marketFilter );
				}
			}
		};
		marketFilter.addPropertyChangeListener( marketListener );

		configuration.getFilters( ).add( marketFilter );
		configuration.getFilters( ).add( marketCategoryTagFilter );
		configuration.setInitialState( wizardState );
		if ( operations != null && !operations.isEmpty( ) )
		{
			configuration.setInitialOperations( operations );
		}

		for ( CatalogFilter filter : configuration.getFilters( ) )
		{
			( (MarketplaceFilter) filter ).setCatalog( catalog );
		}

		MarketplaceWizard wizard = new DecompilerMarketWizard( catalog, configuration );
		wizard.setInitialState( wizardDialogState );
		wizard.setWindowTitle( Messages.MarketplaceWizardCommand_eclipseMarketplace );

		WizardDialog dialog = new MarketplaceWizardDialog( WorkbenchUtil.getShell( ), wizard );
		dialog.open( );

		return null;
	}

	private void updateCategoryChoices( final ComboTagFilter marketCategoryTagFilter,
			final ComboTagFilter marketFilter )
	{
		Set<Tag> newChoices = new HashSet<Tag>( );
		List<Tag> choices = new ArrayList<Tag>( );

		Set<IMarket> selectedMarkets = new HashSet<IMarket>( );
		for ( Tag marketTag : marketFilter.getSelected( ) )
		{
			selectedMarkets.add( (IMarket) marketTag.getData( ) );
		}

		final MarketplaceCatalog catalog = (MarketplaceCatalog) marketCategoryTagFilter.getCatalog( );
		List<IMarket> markets = catalog.getMarkets( );
		for ( IMarket market : markets )
		{
			if ( selectedMarkets.isEmpty( ) || selectedMarkets.contains( market ) )
			{
				for ( ICategory marketCategory : market.getCategory( ) )
				{
					Tag categoryTag = new Tag( ICategory.class, marketCategory.getId( ), marketCategory.getName( ) );
					categoryTag.setData( marketCategory );
					if ( newChoices.add( categoryTag ) )
					{
						choices.add( categoryTag );
					}
				}
			}
		}
		Collections.sort( choices, new Comparator<Tag>( ) {

			public int compare( Tag o1, Tag o2 )
			{
				return o1.getLabel( ).compareTo( o2.getLabel( ) );
			}
		} );
		marketCategoryTagFilter.setChoices( choices );
	}

	public void setCatalogDescriptors( List<CatalogDescriptor> catalogDescriptors )
	{
		this.catalogDescriptors = catalogDescriptors;
	}

	public void setSelectedCatalogDescriptor( CatalogDescriptor selectedCatalogDescriptor )
	{
		this.selectedCatalogDescriptor = selectedCatalogDescriptor;
	}

	public void setWizardState( String wizardState )
	{
		this.wizardState = wizardState;
	}

	public void setWizardDialogState( WizardState wizardState )
	{
		this.wizardDialogState = wizardState;
	}

	/**
	 * @deprecated use {@link #setOperations(Map)} instead
	 */
	@Deprecated
	public void setOperationByNodeId( Map<String, org.eclipse.epp.internal.mpc.ui.wizards.Operation> operationByNodeId )
	{
		this.operations = org.eclipse.epp.internal.mpc.ui.wizards.Operation.mapAllBack( operationByNodeId );
	}

	public void setOperations( Map<String, Operation> operationByNodeId )
	{
		this.operations = operationByNodeId;
	}

	public void setConfiguration( IMarketplaceClientConfiguration configuration )
	{
		setCatalogDescriptors( configuration.getCatalogDescriptors( ) );
		setOperations( configuration.getInitialOperations( ) );
		setWizardState( (String) configuration.getInitialState( ) );
		setSelectedCatalogDescriptor( configuration.getCatalogDescriptor( ) );
	}

	public IStatus installRemoteCatalogs( )
	{
		try
		{
			final AtomicReference<List<? extends ICatalog>> result = new AtomicReference<List<? extends ICatalog>>( );

			PlatformUI.getWorkbench( ).getProgressService( ).busyCursorWhile( new IRunnableWithProgress( ) {

				public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
				{
					try
					{
						ICatalogService catalogService = ServiceHelper.getMarketplaceServiceLocator( )
								.getCatalogService( );
						final List<? extends ICatalog> catalogs = catalogService.listCatalogs( monitor );
						result.set( catalogs );
					}
					catch ( CoreException e )
					{
						if ( e.getStatus( ).getSeverity( ) == IStatus.CANCEL )
						{
							throw new InterruptedException( );
						}
						throw new InvocationTargetException( e );
					}
				}
			} );

			List<? extends ICatalog> catalogs = result.get( );
			for ( ICatalog catalog : catalogs )
			{
				ResourceProvider resourceProvider = MarketplaceClientUiPlugin.getInstance( ).getResourceProvider( );
				String catalogName = catalog.getName( );
				String requestSource = NLS.bind( Messages.MarketplaceWizardCommand_requestCatalog,
						catalogName,
						catalog.getId( ) );
				String catalogImageUrl = catalog.getImageUrl( );
				if ( catalogImageUrl != null )
				{
					try
					{
						resourceProvider.retrieveResource( requestSource, catalogImageUrl );
					}
					catch ( Exception e )
					{
						MarketplaceClientUi.log( IStatus.WARNING,
								Messages.MarketplaceWizardCommand_FailedRetrievingCatalogImage,
								catalogName,
								catalogImageUrl,
								e );
					}
				}
				if ( catalog.getBranding( ) != null && catalog.getBranding( ).getWizardIcon( ) != null )
				{
					String wizardIconUrl = catalog.getBranding( ).getWizardIcon( );
					try
					{
						resourceProvider.retrieveResource( requestSource, wizardIconUrl );
					}
					catch ( Exception e )
					{
						MarketplaceClientUi.log( IStatus.WARNING,
								Messages.MarketplaceWizardCommand_FailedRetrievingCatalogWizardIcon,
								catalogName,
								wizardIconUrl,
								e );
					}
				}
				CatalogDescriptor descriptor = new CatalogDescriptor( catalog );
				registerOrOverrideCatalog( descriptor );
			}
		}
		catch ( InterruptedException ie )
		{
			return Status.CANCEL_STATUS;
		}
		catch ( Exception e )
		{
			IStatus status = MarketplaceClientCore.computeStatus( e,
					Messages.MarketplaceWizardCommand_CannotInstallRemoteLocations );
			return status;
		}
		return Status.OK_STATUS;
	}

	private void registerOrOverrideCatalog( CatalogDescriptor descriptor )
	{
		CatalogRegistry catalogRegistry = CatalogRegistry.getInstance( );
		List<CatalogDescriptor> descriptors = catalogRegistry.getCatalogDescriptors( );
		for ( CatalogDescriptor catalogDescriptor : descriptors )
		{
			if ( catalogDescriptor.getUrl( ).toExternalForm( ).equals( descriptor.getUrl( ).toExternalForm( ) ) )
			{
				catalogRegistry.unregister( catalogDescriptor );
			}
		}
		catalogRegistry.register( descriptor );
	}
}
