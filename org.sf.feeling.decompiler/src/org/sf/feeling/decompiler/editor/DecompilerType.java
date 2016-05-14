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

package org.sf.feeling.decompiler.editor;

import org.sf.feeling.decompiler.JavaDecompilerPlugin;

public class DecompilerType
{
	
	public static final String FernFlower = "FernFlower";//$NON-NLS-1$

	public static String[] decompilerTypes = null;

	public static String[] getDecompilerTypes( )
	{
		if ( decompilerTypes == null )
		{
			decompilerTypes = JavaDecompilerPlugin.getDefault( )
					.getDecompilerDescriptorTypes( );
		}
		return decompilerTypes;
	}
}
