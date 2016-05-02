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

package org.sf.feeling.decompiler.editor;

import java.util.Arrays;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.core.BufferManager;
import org.eclipse.jdt.internal.core.ClassFile;
import org.eclipse.jdt.internal.core.SourceMapper;

public class ClassFileSourceMap
{

	private static IType getOuterMostEnclosingType( ClassFile cf )
	{
		IType type = cf.getType( );
		IType enclosingType = type.getDeclaringType( );
		while ( enclosingType != null )
		{
			type = enclosingType;
			enclosingType = type.getDeclaringType( );
		}
		return type;
	}

	private static void mapSource( JavaDecompilerBufferManager bufferManager,
			ClassFile cf, SourceMapper mapper, IBinaryType info,
			IClassFile bufferOwner, char[] markedSrc )
	{
		char[] contents = mapper.findSource( cf.getType( ), info );
		if ( Arrays.equals( markedSrc, contents ) )
			return;
		contents = markedSrc;
		if ( contents != null )
		{
			// create buffer
			IBuffer buffer = BufferManager.createBuffer( bufferOwner );
			if ( buffer == null )
				return;
			JavaDecompilerBufferManager bufManager = bufferManager;
			bufManager.addBuffer( buffer );

			// set the buffer source
			if ( buffer.getCharacters( ) == null )
			{
				buffer.setContents( contents );
			}

			// listen to buffer changes
			// buffer.addBufferChangedListener( cf );

			// do the source mapping
			mapper.mapSource( getOuterMostEnclosingType( cf ), contents, info );

			return;
		}
		else
		{
			// create buffer
			IBuffer buffer = BufferManager.createNullBuffer( bufferOwner );
			if ( buffer == null )
				return;
			JavaDecompilerBufferManager bufManager = bufferManager;
			bufManager.addBuffer( buffer );

			// listen to buffer changes
			// buffer.addBufferChangedListener( cf );
			return;
		}
	}

	public static void updateSource( JavaDecompilerBufferManager bufferManager,
			ClassFile cf, char[] markedSrc ) throws Exception
	{
		IType type = cf.getType( );
		if ( !type.isBinary( ) )
		{
			return;
		}
		Object info = cf.getElementInfo( );
		IType enclosingType = type.getDeclaringType( );
		while ( enclosingType != null )
		{
			type = enclosingType;
			enclosingType = type.getDeclaringType( );
		}
		IType outerMostEnclosingType = type;
		SourceMapper mapper = cf.getSourceMapper( );
		IBinaryType typeInfo = info instanceof IBinaryType ? (IBinaryType) info
				: null;
		if ( mapper != null )
		{
			mapSource( bufferManager,
					cf,
					mapper,
					typeInfo,
					outerMostEnclosingType.getClassFile( ),
					markedSrc );
		}

	}
}
