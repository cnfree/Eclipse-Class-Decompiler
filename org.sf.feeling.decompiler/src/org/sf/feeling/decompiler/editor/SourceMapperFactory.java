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
import org.sf.feeling.decompiler.fernflower.FernFlowerSourceMapper;

public class SourceMapperFactory
{
	private static DecompilerSourceMapper fernFlowerSourceMapper;

	public static DecompilerSourceMapper getSourceMapper( String decompiler )
	{

		if ( DecompilerType.FernFlower.equals( decompiler ) )
		{
			if ( fernFlowerSourceMapper == null )
			{
				fernFlowerSourceMapper = new FernFlowerSourceMapper( );
			}
			return fernFlowerSourceMapper;
		}
		else
		{
			IDecompilerDescriptor descriptor = JavaDecompilerPlugin.getDefault( )
					.getDecompilerDescriptor( decompiler );
			if ( descriptor != null )
				return descriptor.getDecompilerSourceMapper( );
		}
		return null;
	}
}
