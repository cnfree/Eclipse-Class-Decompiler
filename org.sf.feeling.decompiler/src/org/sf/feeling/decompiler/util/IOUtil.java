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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class IOUtil
{

	public static byte[] readInputStream( InputStream inStream )
			throws Exception
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream( );
		byte[] buffer = new byte[1024];
		int len = 0;
		while ( ( len = inStream.read( buffer ) ) != -1 )
		{
			outStream.write( buffer, 0, len );
		}
		byte[] data = outStream.toByteArray( );// 网页的二进制数据
		outStream.close( );
		inStream.close( );
		return data;
	}
}
