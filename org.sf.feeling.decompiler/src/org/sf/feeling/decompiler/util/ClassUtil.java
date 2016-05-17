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

package org.sf.feeling.decompiler.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.editor.IDecompiler;
import org.sf.feeling.decompiler.editor.IDecompilerDescriptor;
import org.sf.feeling.decompiler.fernflower.FernFlowerDecompiler;

public class ClassUtil
{

	public static IDecompiler checkAvailableDecompiler( IDecompiler decompiler,
			File file )
	{
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream( file );
			return checkAvailableDecompiler( decompiler, fis );
		}
		catch ( FileNotFoundException e )
		{
			e.printStackTrace( );
		}
		finally
		{
			if ( fis != null )
			{
				try
				{
					fis.close( );
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
			}
		}
		return decompiler;
	}

	public static IDecompiler checkAvailableDecompiler( IDecompiler decompiler,
			InputStream is )
	{
		int classLevel = getLevel( is );
		if ( decompiler.supportLevel( classLevel ) )
		{
			if ( isDebug( ) )
			{
				if ( !decompiler.supportDebugLevel( classLevel ) )
				{
					return getDefaultDecompiler( classLevel, isDebug( ) );
				}
			}
		}
		else
		{
			return getDefaultDecompiler( classLevel, isDebug( ) );
		}
		return decompiler;
	}

	private static boolean isDebug( )
	{
		return JavaDecompilerPlugin.getDefault( ).isDisplayLineNumber( )
				|| UIUtil.isDebugPerspective( );
	}

	public static boolean greatLevel6( File file )
	{
		DataInputStream data = null;
		try
		{
			data = new DataInputStream( new FileInputStream( file ) );
			if ( 0xCAFEBABE != data.readInt( ) )
			{
				return false;
			}
			data.readUnsignedShort( );
			int major = data.readUnsignedShort( );
			data.close( );
			data = null;
			return major >= 51;
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		finally
		{
			if ( data != null )
			{
				try
				{
					data.close( );
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
			}
		}
		return false;
	}

	public static int getLevel( InputStream is )
	{
		DataInputStream data = null;
		try
		{
			data = new DataInputStream( is );
			if ( 0xCAFEBABE != data.readInt( ) )
			{
				return -1;
			}
			data.readUnsignedShort( );
			int major = data.readUnsignedShort( );
			return major - 44;
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return -1;
	}

	public static boolean isClassFile( byte[] bytes )
	{
		ByteArrayInputStream bis = new ByteArrayInputStream( bytes );
		DataInputStream data = null;
		try
		{
			data = new DataInputStream( bis );
			if ( 0xCAFEBABE != data.readInt( ) )
			{
				return false;
			}
			data.readUnsignedShort( );
			data.readUnsignedShort( );
			return true;
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		finally
		{
			if ( data != null )
			{
				try
				{
					data.close( );
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
			}
		}
		return false;
	}

	public static IDecompiler getDefaultDecompiler( int level, boolean debug )
	{
		Collection<IDecompilerDescriptor> descriptors = JavaDecompilerPlugin.getDefault( )
				.getDecompilerDescriptorMap( )
				.values( );
		if ( descriptors != null )
		{
			for ( Iterator iterator = descriptors.iterator( ); iterator.hasNext( ); )
			{
				IDecompilerDescriptor iDecompilerDescriptor = (IDecompilerDescriptor) iterator.next( );
				if ( iDecompilerDescriptor.isDefault( ) )
				{
					IDecompiler decompiler = iDecompilerDescriptor.getDecompiler( );
					if ( debug )
					{
						if ( decompiler.supportDebug( )
								&& decompiler.supportDebugLevel( level ) )
						{
							return decompiler;
						}
					}
					else
					{
						if ( decompiler.supportLevel( level ) )
						{
							return decompiler;
						}
					}
				}
			}
		}
		return new FernFlowerDecompiler( );
	}
}
