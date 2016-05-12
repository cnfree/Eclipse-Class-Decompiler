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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicodeUtil
{

	static final Pattern unicodePattern = Pattern.compile( "\\\\u([0-9a-zA-Z]{4})" );

	public static String decode( String s )
	{
		Matcher m = unicodePattern.matcher( s );
		StringBuffer sb = new StringBuffer( s.length( ) );
		while ( m.find( ) )
		{
			m.appendReplacement( sb,
					Character.toString( (char) Integer.parseInt( m.group( 1 ),
							16 ) ) );
		}
		m.appendTail( sb );
		return sb.toString( );
	}
}
