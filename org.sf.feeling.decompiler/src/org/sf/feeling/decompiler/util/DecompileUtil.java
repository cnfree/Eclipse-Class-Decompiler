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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.sf.feeling.decompiler.editor.DecompilerSourceMapper;
import org.sf.feeling.decompiler.editor.DecompilerType;
import org.sf.feeling.decompiler.editor.JavaDecompilerClassFileEditor;
import org.sf.feeling.decompiler.editor.SourceMapperFactory;

public class DecompileUtil
{

	public static String decompile( IClassFile cf, String type, boolean always,
			boolean reuseBuf, boolean force ) throws CoreException
	{
		String decompilerType = type;
		String origSrc = cf.getSource( );
		// have to check our mark since all line comments are stripped
		// in debug align mode
		if ( origSrc == null
				|| always
						&& !origSrc.startsWith(
								JavaDecompilerClassFileEditor.MARK )
				|| ( origSrc.startsWith( JavaDecompilerClassFileEditor.MARK )
						&& ( !reuseBuf || force ) ) )
		{
			DecompilerSourceMapper sourceMapper = SourceMapperFactory
					.getSourceMapper( decompilerType );
			char[] src = sourceMapper.findSource( cf.getType( ) );
			if ( src == null )
			{
				if ( DecompilerType.JAD.equals( decompilerType ) )
				{
					src = SourceMapperFactory
							.getSourceMapper( DecompilerType.JDCORE )
							.findSource( cf.getType( ) );
				}
				else if ( DecompilerType.JDCORE.equals( decompilerType )
						&& UIUtil.isWin32( ) )
				{
					src = SourceMapperFactory
							.getSourceMapper( DecompilerType.JAD )
							.findSource( cf.getType( ) );
				}
			}
			if ( src == null )
			{
				return origSrc;
			}
			else
				return new String( src );
		}

		return origSrc;
	}

	public static String decompiler( FileStoreEditorInput input,
			String decompilerType )
	{
		DecompilerSourceMapper sourceMapper = SourceMapperFactory
				.getSourceMapper( decompilerType );
		File file = new File( ( (FileStoreEditorInput) input ).getURI( ) );
		return sourceMapper.decompile( file );

	}

	public static String getPackageName( String source )
	{
		Pattern p = Pattern.compile( "(?i)package\\s+\\S+" ); //$NON-NLS-1$

		Matcher m = p.matcher( source );
		if ( m.find( ) )
		{
			return m.group( )
					.replace( "package", "" ) //$NON-NLS-1$ //$NON-NLS-2$
					.replace( ";", "" ) //$NON-NLS-1$ //$NON-NLS-2$
					.trim( );
		}
		return null;
	}
}
