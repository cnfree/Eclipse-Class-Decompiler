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

package org.sf.feeling.decompiler.update;

import java.net.URI;
import java.util.Iterator;

import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IBundleGroupProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.p2.metadata.OSGiVersion;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Display;
import org.sf.feeling.decompiler.update.i18n.Messages;
import org.sf.feeling.luna.mpc.ui.market.client.LunaDecompilerMarketplace;
import org.sf.feeling.mars.mpc.ui.market.client.MarsDecompilerMarketplace;
import org.sf.feeling.mars.mpc.ui.market.client.MarsMarketClientHandler;
import org.sf.feeling.neon.mpc.ui.market.client.NeonDecompilerMarketplace;

public class DecompilerUpdateHandler implements IDecompilerUpdateHandler
{

	public void execute( )
	{
		Job job = new Job( "Decompiler update job" ) { //$NON-NLS-1$

			protected IStatus run( IProgressMonitor monitor )
			{
				monitor.beginTask( "start task", 100 ); //$NON-NLS-1$
				try
				{
					updateDecompiler( monitor );
					monitor.worked( 100 );
					return Status.OK_STATUS;
				}
				catch ( Exception e )
				{
					monitor.worked( 100 );
					return Status.CANCEL_STATUS;
				}
			}
		};

		job.setPriority( Job.DECORATE );
		job.setSystem( true );
		job.schedule( );
	}

	private void updateDecompiler( IProgressMonitor monitor ) throws Exception
	{
		final String version = getUpdateVersion( monitor );
		if ( version != null
				&& !version.equals( DecompilerUpdatePlugin.getDefault( )
						.getPreferenceStore( )
						.getString( DecompilerUpdatePlugin.NOT_UPDATE_VERSION ) ) )
		{
			Display.getDefault( ).asyncExec( new Runnable( ) {

				public void run( )
				{
					MessageDialogWithToggle dialog = new MessageDialogWithToggle(
							Display.getDefault( ).getActiveShell( ),
							Messages.getString( "DecompilerUpdateHandler.ConfirmDialog.Title" ), //$NON-NLS-1$
							null, // accept the default window icon
							Messages.getString( "DecompilerUpdateHandler.ConfirmDialog.Message" ), //$NON-NLS-1$
							MessageDialog.CONFIRM,
							new String[]{
									Messages.getString( "DecompilerUpdateHandler.ConfirmDialog.Button.NotNow" ), //$NON-NLS-1$
									Messages.getString( "DecompilerUpdateHandler.ConfirmDialog.Button.Continue" ) //$NON-NLS-1$
					},
							1,
							Messages.getString( "DecompilerUpdateHandler.ConfirmDialog.Button.NotAskAgain" ), //$NON-NLS-1$
							false );
					int result = dialog.open( );

					if ( dialog.getToggleState( ) )
					{
						DecompilerUpdatePlugin.getDefault( )
								.getPreferenceStore( )
								.setValue( DecompilerUpdatePlugin.NOT_UPDATE_VERSION, version );
					}

					if ( result - IDialogConstants.INTERNAL_ID == 1 )
					{
						String installUrl = "http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=472922"; //$NON-NLS-1$
						if ( MarsDecompilerMarketplace.isInteresting( ) )
						{
							new MarsMarketClientHandler( ).proceedInstallation( installUrl );

						}
						else if ( LunaDecompilerMarketplace.isInteresting( ) )
						{
							new LunaDecompilerMarketplace( ).proceedInstallation( installUrl );
						}
					}
				}
			} );
		}
	}

	protected boolean existClass( String classFullName )
	{
		try
		{
			Class.forName( classFullName );
			return true;
		}
		catch ( ClassNotFoundException e )
		{
			return false;
		}
	}

	private String getUpdateVersion( IProgressMonitor monitor ) throws Exception
	{
		String updateUrl = null;
		if ( existClass( "org.eclipse.epp.internal.mpc.core.service.Node" ) )
		{
			updateUrl = MarsDecompilerMarketplace.getUpdateUrl( monitor );
		}
		else if ( existClass( "org.eclipse.epp.internal.mpc.core.model.Node" ) )
		{
			updateUrl = NeonDecompilerMarketplace.getUpdateUrl( monitor );
		}

		ProvisioningSession session = ProvisioningUI.getDefaultUI( ).getSession( );
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) session.getProvisioningAgent( )
				.getService( IMetadataRepositoryManager.SERVICE_NAME );

		URI updateUri = new URI( updateUrl );
		IMetadataRepository repository = manager.loadRepository( updateUri, monitor );
		IQuery<IInstallableUnit> query = QueryUtil.createMatchQuery( "id ~= /*.feature.group/ && " + //$NON-NLS-1$
				"properties['org.eclipse.equinox.p2.type.group'] == true " );//$NON-NLS-1$
		IQueryResult<IInstallableUnit> result = repository.query( query, monitor );

		for ( Iterator<IInstallableUnit> iterator = result.iterator( ); iterator.hasNext( ); )
		{
			IInstallableUnit iu = (IInstallableUnit) iterator.next( );
			OSGiVersion remoteVersion = (OSGiVersion) iu.getVersion( );
			OSGiVersion installVersion = (OSGiVersion) getFeatureVersion( "org.sf.feeling.decompiler" ); //$NON-NLS-1$
			if ( remoteVersion != null )
			{
				if ( installVersion == null )
					continue;

				if ( remoteVersion.getMajor( ) > installVersion.getMajor( ) )
					return getVersion( remoteVersion );
				else if ( remoteVersion.getMajor( ) == installVersion.getMajor( )
						&& remoteVersion.getMinor( ) > installVersion.getMinor( ) )
					return getVersion( remoteVersion );
			}

		}

		return null;
	}

	private String getVersion( OSGiVersion remoteVersion )
	{
		return remoteVersion.getMajor( ) + "." + remoteVersion.getMinor( ); //$NON-NLS-1$
	}

	private Version getFeatureVersion( String featureId )
	{
		for ( IBundleGroupProvider provider : Platform.getBundleGroupProviders( ) )
		{
			for ( IBundleGroup feature : provider.getBundleGroups( ) )
			{
				if ( feature.getIdentifier( ).equals( featureId ) )
					return Version.create( feature.getVersion( ) );
			}
		}
		return null;
	}
}
