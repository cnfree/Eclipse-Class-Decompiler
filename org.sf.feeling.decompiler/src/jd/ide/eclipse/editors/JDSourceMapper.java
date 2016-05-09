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

package jd.ide.eclipse.editors;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.sf.feeling.decompiler.editor.DecompilerSourceMapper;
import org.sf.feeling.decompiler.util.UIUtil;

public abstract class JDSourceMapper extends DecompilerSourceMapper
{

	protected final static String JAR_SUFFIX = ".jar"; //$NON-NLS-1$
	protected final static String ZIP_SUFFIX = ".zip"; //$NON-NLS-1$
	protected final static String JAVA_CLASS_SUFFIX = ".class"; //$NON-NLS-1$
	protected final static String JAVA_SOURCE_SUFFIX = ".java"; //$NON-NLS-1$
	protected final static int JAVA_SOURCE_SUFFIX_LENGTH = 5;
	protected static boolean loaded = false;

	public JDSourceMapper( IPath sourcePath, String rootPath, Map options )
	{
		super( sourcePath, rootPath, options );
	}

	protected native String decompile( String baseName, String qualifiedName );

	protected void loadLibrary( ) throws IOException
	{
		if ( loaded == false )
		{
			System.load( getLibraryPath( ) );
			loaded = true;
		}
	}

	protected String getLibraryPath( ) throws IOException
	{
		URL pluginUrl = null;
		if ( Platform.OS_WIN32.equalsIgnoreCase( Platform.getOS( ) ) )
		{
			if ( Platform.ARCH_X86_64.equalsIgnoreCase( Platform.getOSArch( ) ) )
			{
				pluginUrl = this.getClass( )
						.getResource( "/native/jd-core/win32/x86_64/jd-eclipse.dll" ); //$NON-NLS-1$
			}
			else
			{
				pluginUrl = this.getClass( )
						.getResource( "/native/jd-core/win32/x86/jd-eclipse.dll" ); //$NON-NLS-1$
			}
		}
		else if ( Platform.OS_LINUX.equalsIgnoreCase( Platform.getOS( ) ) )
		{
			if ( Platform.ARCH_X86_64.equalsIgnoreCase( Platform.getOSArch( ) ) )
			{
				pluginUrl = this.getClass( )
						.getResource( "/native/jd-core/linux/x86_64/libjd-eclipse.so" ); //$NON-NLS-1$
			}
			else
			{
				pluginUrl = this.getClass( )
						.getResource( "/native/jd-core/linux/x86/libjd-eclipse.so" ); //$NON-NLS-1$
			}
		}
		else if ( Platform.OS_MACOSX.equalsIgnoreCase( Platform.getOS( ) ) )
		{
			if ( Platform.ARCH_X86_64.equalsIgnoreCase( Platform.getOSArch( ) ) )
			{
				pluginUrl = this.getClass( )
						.getResource( "/native/jd-core/macosx/x86_64/libjd-eclipse.jnilib" ); //$NON-NLS-1$
			}
			else
			{
				pluginUrl = this.getClass( )
						.getResource( "/native/jd-core/macosx/x86/libjd-eclipse.jnilib" ); //$NON-NLS-1$
			}
		}
		String path = FileLocator.toFileURL( pluginUrl ).getFile( );
		if ( UIUtil.isWin32( )
				&& path != null
				&& ( path.length( ) > 0 )
				&& ( path.charAt( 0 ) == '/' ) )
			path = path.substring( 1 );

		return path;
	}
}
