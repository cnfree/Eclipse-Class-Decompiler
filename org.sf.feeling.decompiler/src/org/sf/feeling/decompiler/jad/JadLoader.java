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

package org.sf.feeling.decompiler.jad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.Platform;

public final class JadLoader
{

	public static String loadJad( )
	{
		String jadFileName = null;
		String jadFilePath = null;

		if ( Platform.OS_WIN32.equalsIgnoreCase( Platform.getOS( ) ) )
		{
			jadFileName = "jad" + System.currentTimeMillis( ) + ".exe";
			jadFilePath = "/native/jad/win32/jad.exe";
		}
		else if ( Platform.OS_LINUX.equalsIgnoreCase( Platform.getOS( ) ) )
		{
			jadFileName = "jad" + System.currentTimeMillis( );
			jadFilePath = "/native/jad/linux/jad";
		}
		else if ( Platform.OS_MACOSX.equalsIgnoreCase( Platform.getOS( ) ) )
		{
			jadFileName = "jad" + System.currentTimeMillis( );
			jadFilePath = "/native/jad/macosx/jad";
		}
		else
		{
			throw new Error( "Can't obtain jad executable file." ); //$NON-NLS-1$
		}

		InputStream is = JadLoader.class.getResourceAsStream( jadFilePath ); //$NON-NLS-1$
		if ( is == null )
		{
			throw new Error( "Can't obtain jad executable file." ); //$NON-NLS-1$
		}

		FileOutputStream fos = null;
		try
		{
			String property = "java.io.tmpdir"; //$NON-NLS-1$
			String tempDir = System.getProperty( property );
			File jad = new File( tempDir, jadFileName );
			jad.createNewFile( );
			jad.deleteOnExit( );
			fos = new FileOutputStream( jad );
			int count;
			byte[] buf = new byte[1024];
			while ( ( count = is.read( buf, 0, buf.length ) ) > 0 )
			{
				fos.write( buf, 0, count );
			}
			fos.close( );
			fos = null;

			try
			{
				if ( Platform.OS_LINUX.equalsIgnoreCase( Platform.getOS( ) ) )
				{
					Runtime.getRuntime( ).exec( "chmod a+x "
							+ jad.getAbsolutePath( ) ).waitFor( );
				}
				else if ( Platform.OS_MACOSX.equalsIgnoreCase( Platform.getOS( ) ) )
				{
					Runtime.getRuntime( ).exec( "chmod a+x "
							+ jad.getAbsolutePath( ) ).waitFor( );
				}
			}
			catch ( InterruptedException e )
			{
				e.printStackTrace();
			}

			// jad.setExecutable( true );
			return jad.getAbsolutePath( );
		}
		catch ( IOException e )
		{
			throw new Error( "Failed to create temporary file for jad.exe: " //$NON-NLS-1$
					+ e );
		}
		finally
		{
			try
			{
				is.close( );
			}
			catch ( IOException e )
			{
			}
			if ( fos != null )
			{
				try
				{
					fos.close( );
				}
				catch ( IOException e )
				{
				}
			}
		}
	}
}
