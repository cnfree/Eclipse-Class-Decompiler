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
import org.sf.feeling.decompiler.cfr.CfrSourceMapper;
import org.sf.feeling.decompiler.jad.JadSourceMapper;
import org.sf.feeling.decompiler.jdcore.JDCoreSourceMapper;
import org.sf.feeling.decompiler.procyon.ProcyonSourceMapper;

public class SourceMapperFactory
{

	private static DecompilerSourceMapper jadSourceMapper;
	private static DecompilerSourceMapper jdCoreSourceMapper;
	private static DecompilerSourceMapper cfrSourceMapper;
	private static DecompilerSourceMapper procyonSourceMapper;

	public static DecompilerSourceMapper getSourceMapper( String decompiler )
	{
		if ( DecompilerType.JAD.equals( decompiler ) )
		{
			if ( jadSourceMapper == null )
			{
				jadSourceMapper = new JadSourceMapper( );
			}
			return jadSourceMapper;
		}
		else if ( DecompilerType.JDCORE.equals( decompiler ) )
		{
			if ( jdCoreSourceMapper == null )
			{
				jdCoreSourceMapper = new JDCoreSourceMapper( );
			}
			return jdCoreSourceMapper;
		}
		else if ( DecompilerType.CFR.equals( decompiler ) )
		{
			if ( JavaDecompilerPlugin.getDefault( ).enableCfrDecompiler( ) )
			{
				if ( cfrSourceMapper == null )
				{
					cfrSourceMapper = new CfrSourceMapper( );
				}
				return cfrSourceMapper;
			}
		}
		else if ( DecompilerType.PROCYON.equals( decompiler ) )
		{
			if ( JavaDecompilerPlugin.getDefault( ).enableProcyonDecompiler( ) )
			{
				if ( procyonSourceMapper == null )
				{
					procyonSourceMapper = new ProcyonSourceMapper( );
				}
				return procyonSourceMapper;
			}
		}
		return null;
	}
}
