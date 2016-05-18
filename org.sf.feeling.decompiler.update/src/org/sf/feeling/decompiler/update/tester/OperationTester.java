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

package org.sf.feeling.decompiler.update.tester;

import org.eclipse.epp.mpc.ui.Operation;

public class OperationTester
{

	public static boolean supportMars( )
	{
		try
		{
			if ( Operation.valueOf( "CHANGE" ) != null ) //$NON-NLS-1$
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
