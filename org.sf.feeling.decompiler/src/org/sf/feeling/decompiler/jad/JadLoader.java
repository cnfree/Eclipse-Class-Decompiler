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

public final class JadLoader
{

	public static String loadJad( )
	{
		InputStream is = JadLoader.class.getResourceAsStream( "/native/jad.exe" ); //$NON-NLS-1$
		if ( is == null )
		{
			throw new Error( "Can't obtain jad.exe" ); //$NON-NLS-1$
		}

		FileOutputStream fos = null;
		try
		{
			String property = "java.io.tmpdir"; //$NON-NLS-1$
			String tempDir = System.getProperty( property );
			File lib = new File( tempDir, "jad" //$NON-NLS-1$
					+ System.currentTimeMillis( )
					+ ".exe" ); //$NON-NLS-1$
			lib.createNewFile( );
			lib.deleteOnExit( );
			fos = new FileOutputStream( lib );
			int count;
			byte[] buf = new byte[1024];
			while ( ( count = is.read( buf, 0, buf.length ) ) > 0 )
			{
				fos.write( buf, 0, count );
			}

			return "\"" + lib.getAbsolutePath( ) + "\""; //$NON-NLS-1$ //$NON-NLS-2$
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
