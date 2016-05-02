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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class CommandLine
{

	private static NumericOption createNumericOption( String token, int numIdx )
	{
		String optName = token.substring( 0, numIdx );
		int numValue = Integer.parseInt( token.substring( numIdx ) );
		return new NumericOption( optName, numValue );
	}

	private static int findTrailingNumber( String token )
	{
		int numIdx = -1;
		// check if this is a numeric option
		for ( int i = ( token.length( ) - 1 ); i >= 0; i-- )
		{
			char ch = token.charAt( i );
			if ( ch >= '0' && ch <= '9' )
			{
				numIdx = i;
			}
			else
			{ // break on first non-digit from end
				break;
			}
		}
		return numIdx;
	}

	public static void main( String[] args ) throws Exception
	{
		CommandLine cl = CommandLine.parse( "C:\\Program Files\\jad -zxc ttt -x -lff100 -bb", //$NON-NLS-1$
				"-" ); //$NON-NLS-1$
		System.out.println( "command: " + cl.getCommand( ) ); //$NON-NLS-1$
		System.out.println( "option count: " + cl.getOptionCount( ) ); //$NON-NLS-1$
		List options = cl.getOptions( );
		for ( int i = 0; i < options.size( ); i++ )
		{
			CommandOption opt = (CommandOption) options.get( i );
			System.out.println( "option_" //$NON-NLS-1$
					+ i
					+ "(" //$NON-NLS-1$
					+ opt.getClass( ).getName( )
					+ "): " //$NON-NLS-1$
					+ opt.toString( ) );
		}
		System.out.println( "-lff=" + cl.getOption( "-lff" ).getValue( ) ); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println( cl.toString( ) );
	}

	public static CommandLine parse( String line, String optionPrefix )
			throws ParseException
	{
		if ( line == null )
		{
			throw new IllegalArgumentException( "null line" ); //$NON-NLS-1$
		}
		if ( optionPrefix == null )
		{
			throw new IllegalArgumentException( "null option prefix" ); //$NON-NLS-1$
		}
		int optStart = line.indexOf( optionPrefix );
		if ( optStart == 0 )
		{
			throw new ParseException( "missing command", 0 ); //$NON-NLS-1$
		}
		if ( optStart < 0 )
		{ // no options found
			return new CommandLine( line.trim( ), null );
		}
		String cmd = line.substring( 0, optStart ).trim( );
		ArrayList opts = new ArrayList( );
		StringTokenizer st = new StringTokenizer( line.substring( optStart ),
				"\t " ); //$NON-NLS-1$
		String optName = null;
		while ( st.hasMoreTokens( ) )
		{
			String token = st.nextToken( );
			boolean option = token.startsWith( optionPrefix );
			if ( option )
			{ // found option start
				if ( optName != null )
				{ // create a no-value option
					opts.add( new CommandOption( optName, null ) );
					optName = null;
				}
				// check if this is a numeric option
				NumericOption numOpt = tryNumericOption( token );
				if ( numOpt != null )
				{
					opts.add( numOpt );
				}
				else
				{
					optName = token;
				}
			}
			else
			{ // found option value
				if ( optName == null )
				{
					throw new ParseException( "value without a name", 0 ); //$NON-NLS-1$
				}
				opts.add( new CommandOption( optName, token ) );
				optName = null;
			}
		}
		if ( optName != null )
		{ // do not forget the last one!
			// check if this is a numeric option
			NumericOption numOpt = tryNumericOption( optName );
			if ( numOpt != null )
			{
				opts.add( numOpt );
			}
			else
			{ // create a non-numeric option
				opts.add( new CommandOption( optName, null ) );
			}
		}
		return new CommandLine( cmd, opts );
	}

	private static NumericOption tryNumericOption( String token )
			throws ParseException
	{
		int numIdx = findTrailingNumber( token );
		if ( numIdx == 0 || numIdx == 1 )
		{
			throw new ParseException( "option cannot start with a number", //$NON-NLS-1$
					numIdx );
		}
		return ( numIdx > 0 ) ? createNumericOption( token, numIdx ) : null;
	}

	private String command;

	private List options = new ArrayList( );

	public CommandLine( String command, List options )
	{
		if ( command == null || command.trim( ).length( ) == 0 )
		{
			throw new IllegalArgumentException( "bogus command" ); //$NON-NLS-1$
		}
		this.command = command;
		if ( options != null )
		{
			this.options.addAll( options );
		}
	}

	public void addOption( CommandOption option )
	{
		options.add( option );
	}

	public void clearOptions( )
	{
		options.clear( );
	}

	/**
	 * @return
	 */
	public String getCommand( )
	{
		return command;
	}

	public CommandOption getOption( String name )
	{
		if ( name == null )
		{
			throw new IllegalArgumentException( "null name" ); //$NON-NLS-1$
		}
		for ( Iterator iter = options.iterator( ); iter.hasNext( ); )
		{
			CommandOption option = (CommandOption) iter.next( );
			if ( name.equals( option.getName( ) ) )
			{
				return option;
			}
		}
		return null;
	}

	public int getOptionCount( )
	{
		return options.size( );
	}

	/**
	 * @return
	 */
	public List getOptions( )
	{
		return options;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString( )
	{
		StringBuffer buf = new StringBuffer( command ).append( ' ' );
		for ( Iterator iter = options.iterator( ); iter.hasNext( ); )
		{
			CommandOption option = (CommandOption) iter.next( );
			buf.append( option.toString( ) ).append( ' ' );
		}
		buf.setLength( buf.length( ) - 1 ); // remove last white space
		return buf.toString( );
	}

}
