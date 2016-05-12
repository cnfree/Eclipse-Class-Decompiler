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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.editor.DecompilerType;
import org.sf.feeling.decompiler.editor.IDecompiler;
import org.sf.feeling.decompiler.fernflower.FernFlowerDecompiler;

public class ClassUtil
{

	public static String removeComment( IDecompiler decompiler, String code )
	{
		if ( DecompilerType.JAD.equals( decompiler.getDecompilerType( ) ) )
		{
			return removeJadComments( code );
		}
		return code;
	}

	private static String removeJadComments( String code )
	{
		String[] spilts = code.replaceAll( "\r\n", "\n" ).split( "\n" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		StringBuffer buffer = new StringBuffer( );
		for ( int i = 0; i < spilts.length; i++ )
		{
			if ( i > 0 && i < 5 )
				continue;
			String string = spilts[i];
			Pattern pattern = Pattern.compile( "\\s*/\\*\\s*\\S*\\*/", //$NON-NLS-1$
					Pattern.CASE_INSENSITIVE );
			Matcher matcher = pattern.matcher( string );
			if ( matcher.find( ) )
			{
				if ( matcher.start( ) == 0 )
				{
					buffer.append( string ).append( "\r\n" ); //$NON-NLS-1$
					continue;
				}
			}

			boolean refer = false;

			pattern = Pattern.compile( "\\s*// Referenced", //$NON-NLS-1$
					Pattern.CASE_INSENSITIVE );
			matcher = pattern.matcher( string );
			if ( matcher.find( ) )
			{
				refer = true;

				while ( true )
				{
					i++;
					if ( spilts[i].trim( ).startsWith( "//" ) ) //$NON-NLS-1$
					{
						continue;
					}
					else if ( i >= spilts.length )
					{
						break;
					}
					else
					{
						i--;
						break;
					}
				}
			}

			if ( !refer )
				buffer.append( string + "\r\n" ); //$NON-NLS-1$
		}
		return buffer.toString( );
	}

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
		if ( greatLevel6( is ) )
		{
			if ( JavaDecompilerPlugin.getDefault( ).isDisplayLineNumber( )
					|| UIUtil.isDebugPerspective( ) )
			{
				if ( !supportGreatLevel6AndDebug( decompiler.getDecompilerType( ) ) )
				{
					return new FernFlowerDecompiler( );
				}
			}
			else if ( !supportGreatLevel6( decompiler.getDecompilerType( ) ) )
			{
				return new FernFlowerDecompiler( );
			}
		}
		else
		{
			if ( JavaDecompilerPlugin.getDefault( ).isDisplayLineNumber( )
					|| UIUtil.isDebugPerspective( ) )
			{
				if ( DecompilerType.CFR.equals( decompiler.getDecompilerType( ) ) )
				{
					return new FernFlowerDecompiler( );
				}
			}
		}
		return decompiler;
	}

	private static boolean supportGreatLevel6( String decompilerType )
	{
		if ( DecompilerType.CFR.endsWith( decompilerType ) )
			return true;
		if ( DecompilerType.PROCYON.endsWith( decompilerType ) )
			return true;
		if ( DecompilerType.FernFlower.endsWith( decompilerType ) )
			return true;
		return false;
	}

	private static boolean supportGreatLevel6AndDebug( String decompilerType )
	{
		if ( DecompilerType.PROCYON.endsWith( decompilerType ) )
			return true;
		if ( DecompilerType.FernFlower.endsWith( decompilerType ) )
			return true;
		return false;
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

	public static boolean greatLevel6( InputStream is )
	{
		DataInputStream data = null;
		try
		{
			data = new DataInputStream( is );
			if ( 0xCAFEBABE != data.readInt( ) )
			{
				return false;
			}
			data.readUnsignedShort( );
			int major = data.readUnsignedShort( );
			return major >= 51;
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return false;
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
}
