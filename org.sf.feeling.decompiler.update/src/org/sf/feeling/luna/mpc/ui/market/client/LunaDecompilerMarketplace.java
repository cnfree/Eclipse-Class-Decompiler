
package org.sf.feeling.luna.mpc.ui.market.client;

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
			Class.forName( "org.eclipse.epp.mpc.ui.Operation" );
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
