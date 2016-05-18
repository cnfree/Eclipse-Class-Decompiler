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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.epp.internal.mpc.core.MarketplaceClientCore;
import org.eclipse.epp.internal.mpc.ui.MarketplaceClientUi;
import org.eclipse.epp.mpc.ui.CatalogDescriptor;
import org.eclipse.epp.mpc.ui.MarketplaceUrlHandler;
import org.eclipse.epp.mpc.ui.Operation;
import org.eclipse.epp.mpc.ui.MarketplaceUrlHandler.SolutionInstallationInfo;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.statushandlers.StatusManager;
import org.sf.feeling.mars.mpc.ui.commands.MarketplaceWizardCommand;

public class MarsMarketClientHandler
{

	protected static final String UTF_8 = "UTF-8"; //$NON-NLS-1$

	public void proceedInstallation( String url )
	{
		MarketplaceUrlHandler.SolutionInstallationInfo info = MarketplaceUrlHandler.createSolutionInstallInfo( url );
		if ( info != null )
		{
			triggerInstall( info );
		}
	}

	public static void triggerInstall( SolutionInstallationInfo info )
	{
		if ( info.getRequestUrl( ) != null )
		{
			MarketplaceClientUi.getLog( )
					.log( new Status( IStatus.INFO,
							MarketplaceClientUi.BUNDLE_ID,
							NLS.bind( Messages.MarketplaceUrlHandler_performInstallRequest, info.getRequestUrl( ) ) ) );
		}
		String installId = info.getInstallId( );
		String mpcState = info.getState( );
		CatalogDescriptor catalogDescriptor = info.getCatalogDescriptor( );
		MarketplaceWizardCommand command = new MarketplaceWizardCommand( );
		command.setSelectedCatalogDescriptor( catalogDescriptor );
		try
		{
			if ( mpcState != null )
			{
				command.setWizardState( URLDecoder.decode( mpcState, UTF_8 ) );
			}
			Map<String, Operation> nodeToOperation = new HashMap<String, Operation>( );
			nodeToOperation.put( URLDecoder.decode( installId, UTF_8 ), Operation.INSTALL );
			command.setOperations( nodeToOperation );
		}
		catch ( UnsupportedEncodingException e1 )
		{
			throw new IllegalStateException( e1 );
		}
		try
		{
			command.execute( new ExecutionEvent( ) );
		}
		catch ( ExecutionException e )
		{
			IStatus status = MarketplaceClientCore.computeStatus( e,
					Messages.MarketplaceUrlHandler_cannotOpenMarketplaceWizard );
			StatusManager.getManager( ).handle( status, StatusManager.SHOW | StatusManager.BLOCK | StatusManager.LOG );
		}
	}
}
