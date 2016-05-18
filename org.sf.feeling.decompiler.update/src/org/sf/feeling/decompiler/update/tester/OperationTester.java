
package org.sf.feeling.decompiler.update.tester;

import org.eclipse.epp.mpc.ui.Operation;

public class OperationTester
{

	public static boolean supportMars( )
	{
		try
		{
			if ( Operation.valueOf( "CHANGE" ) != null )
				return true;
			else
				return false;
		}
		catch ( Exception e )
		{
			return false;
		}
	}
}
