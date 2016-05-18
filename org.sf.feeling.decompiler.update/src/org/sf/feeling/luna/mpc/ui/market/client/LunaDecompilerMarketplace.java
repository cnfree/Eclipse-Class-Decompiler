
package org.sf.feeling.luna.mpc.ui.market.client;

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

import org.eclipse.epp.internal.mpc.ui.wizards.MarketplaceDropAdapter;
import org.sf.feeling.decompiler.update.tester.OperationTester;

public class LunaDecompilerMarketplace extends MarketplaceDropAdapter
{

	public void proceedInstallation( String url )
	{
		super.proceedInstallation( url );
	}

	public static boolean isInteresting( )
	{
		try
		{
			Class.forName( "org.eclipse.epp.mpc.ui.Operation" ); //$NON-NLS-1$
			if ( OperationTester.supportMars( ) )
				return false;
			else
				return true;
		}
		catch ( ClassNotFoundException e )
		{
			return true;
		}
	}
}
