
package org.sf.feeling.decompiler.update.tester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.Platform;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;

public class DecompilerUpdateTester extends PropertyTester
{

	public boolean test( Object receiver, String property, Object[] args,
			Object expectedValue )
	{
		if ( receiver instanceof JavaDecompilerPlugin
				&& "mpcInstall".equals( property ) ) //$NON-NLS-1$
		{
			if ( Boolean.TRUE.equals( expectedValue ) )
			{
				if ( Platform.getBundle( "org.eclipse.epp.mpc.ui" ) != null ) //$NON-NLS-1$
				{
					return true;
				}
			}
		}
		return false;
	}

}
