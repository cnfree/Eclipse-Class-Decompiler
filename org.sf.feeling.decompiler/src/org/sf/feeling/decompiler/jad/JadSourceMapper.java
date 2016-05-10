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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Path;
import org.sf.feeling.decompiler.editor.BaseDecompilerSourceMapper;

public class JadSourceMapper extends BaseDecompilerSourceMapper
{

	public JadSourceMapper( )
	{
		super( new Path( "." ), "", new HashMap( ) ); //$NON-NLS-1$ //$NON-NLS-2$
		decompiler = new JadDecompiler( );
	}

	protected String removeComment( String code )
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

	protected void logExceptions( Collection exceptions, StringBuffer buffer )
	{
		if ( !exceptions.isEmpty( ) )
		{
			buffer.append( "\n\tCaught exceptions:" ); //$NON-NLS-1$
			if ( exceptions == null || exceptions.size( ) == 0 )
				return; // nothing to do
			buffer.append( "\n" ); //$NON-NLS-1$
			StringWriter stackTraces = new StringWriter( );
			PrintWriter stackTracesP = new PrintWriter( stackTraces );

			Iterator i = exceptions.iterator( );
			while ( i.hasNext( ) )
			{
				( (Exception) i.next( ) ).printStackTrace( stackTracesP );
				stackTracesP.println( "" ); //$NON-NLS-1$
			}

			stackTracesP.flush( );
			stackTracesP.close( );
			buffer.append( stackTraces.toString( ) );
		}
	}

	protected void printDecompileReport( StringBuffer source,
			String fileLocation, Collection exceptions )
	{
		String location = "\tDecompiled from: " //$NON-NLS-1$
				+ fileLocation;
		source.append( "\n\n/*" ); //$NON-NLS-1$
		source.append( "\n\tDECOMPILATION REPORT\n\n" ); //$NON-NLS-1$
		source.append( location ).append( "\n" ); //$NON-NLS-1$
		source.append( "\tTotal time: " ) //$NON-NLS-1$
				.append( decompiler.getDecompilationTime( ) )
				.append( " ms\n" ); //$NON-NLS-1$
		source.append( "\t"
				+ decompiler.getLog( ).replaceAll( "\t", "" ).replaceAll( "\n",
						"\n\t" ) );
		logExceptions( exceptions, source );
		source.append( "\n*/" ); //$NON-NLS-1$
	}

}