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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JarClassExtractor
{

	/**
	 * extracts class files from jar/zip archive to specified path. See
	 * <code>IDecompiler</code> documentation for the format of pareameters.
	 */
	public static void extract( String archivePath, String packege,
			String className, boolean inner, String to ) throws IOException
	{
		ZipFile archive = new ZipFile( archivePath );
		List entries = findRelevant( archive, packege, className, inner );
		InputStream in = null;
		OutputStream out = null;
		byte[] buffer = new byte[2048];
		ZipEntry entry;
		String outFile;
		int lastSep, amountRead;

		for ( int i = 0; i < entries.size( ); i++ )
		{
			entry = (ZipEntry) entries.get( i );
			outFile = entry.getName( );
			if ( ( lastSep = outFile.lastIndexOf( '/' ) ) != -1 )
				outFile = outFile.substring( lastSep );

			try
			{
				in = archive.getInputStream( entry );
				if ( in == null )
					throw new IOException( "Zip file entry <" //$NON-NLS-1$
							+ entry.getName( )
							+ "> not found" ); //$NON-NLS-1$
				out = new FileOutputStream( to + File.separator + outFile );

				while ( ( amountRead = in.read( buffer ) ) != -1 )
					out.write( buffer, 0, amountRead );
			}
			finally
			{
				if ( in != null )
					in.close( );
				if ( out != null )
					out.close( );
			}
		}

	}

	private static List findRelevant( ZipFile archive, String packege,
			String className, boolean inner )
	{
		String entryName = ( packege.length( ) == 0 ) ? className : packege
				+ "/" //$NON-NLS-1$
				+ className;
		String innerPrefix = entryName.substring( 0, entryName.length( ) - 6 )
				+ "$"; //$NON-NLS-1$
		// strip .class + $
		Enumeration entries = archive.entries( );
		ZipEntry entry;
		String name;
		ArrayList relevant = new ArrayList( );

		while ( entries.hasMoreElements( ) )
		{
			entry = (ZipEntry) entries.nextElement( );
			name = entry.getName( );
			if ( name.equals( entryName )
					|| ( name.startsWith( innerPrefix ) && inner ) )
				relevant.add( entry );
		}
		return relevant;
	}
}