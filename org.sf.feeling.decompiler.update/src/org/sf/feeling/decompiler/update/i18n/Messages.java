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

package org.sf.feeling.decompiler.update.i18n;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages
{

	private static final String BUNDLE_NAME = "org.sf.feeling.decompiler.update.i18n.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( BUNDLE_NAME );

	private Messages( )
	{
	}

	public static String getString( String key )
	{

		try
		{
			String result = RESOURCE_BUNDLE.getString( key );
			try
			{
				result = new String( result.getBytes( "ISO-8859-1" ), "utf-8" ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			catch ( UnsupportedEncodingException e )
			{
				return '!' + key + '!';
			}
			return result;
		}
		catch ( MissingResourceException e )
		{
			return '!' + key + '!';
		}
	}

	/**
	 * Gets formatted translation for current local
	 * 
	 * @param key
	 *            the key
	 * @return translated value string
	 */
	public static String getFormattedString( String key, Object[] arguments )
	{
		return MessageFormat.format( getString( key ), arguments );
	}
}
