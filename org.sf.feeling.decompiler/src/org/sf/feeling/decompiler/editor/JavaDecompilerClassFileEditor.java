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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.BufferManager;
import org.eclipse.jdt.internal.core.ClassFile;
import org.eclipse.jdt.internal.ui.javaeditor.ClassFileEditor;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.InternalClassFileEditorInput;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.util.ClassUtil;
import org.sf.feeling.decompiler.util.DecompileUtil;
import org.sf.feeling.decompiler.util.FileUtil;
import org.sf.feeling.decompiler.util.ReflectionUtils;
import org.sf.feeling.decompiler.util.UIUtil;

public class JavaDecompilerClassFileEditor extends ClassFileEditor
{

	public static final String ID = "org.sf.feeling.decompiler.ClassFileEditor"; //$NON-NLS-1$
	public static final String MARK = "/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/"; //$NON-NLS-1$

	public JavaDecompilerClassFileEditor( )
	{
		super( );
	}

	private boolean doOpenBuffer( IEditorInput input, boolean force )
			throws JavaModelException
	{
		IPreferenceStore prefs = JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( );
		String decompilerType = prefs
				.getString( JavaDecompilerPlugin.DECOMPILER_TYPE );
		return doOpenBuffer( input, decompilerType, force );
	}

	private boolean doOpenBuffer( IEditorInput input, String type,
			boolean force ) throws JavaModelException
	{
		IPreferenceStore prefs = JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( );
		boolean reuseBuf = prefs
				.getBoolean( JavaDecompilerPlugin.REUSE_BUFFER );
		boolean always = prefs
				.getBoolean( JavaDecompilerPlugin.IGNORE_EXISTING );
		return doOpenBuffer( input, type, force, reuseBuf, always );
	}

	private boolean doOpenBuffer( IEditorInput input, String type,
			boolean force, boolean reuseBuf, boolean always )
			throws JavaModelException
	{
		if ( UIUtil.isDebugPerspective( ) )
			reuseBuf = false;

		if ( input instanceof IClassFileEditorInput )
		{

			boolean opened = false;
			IClassFile cf = ( (IClassFileEditorInput) input ).getClassFile( );

			String decompilerType = type;
			String origSrc = cf.getSource( );
			if ( origSrc == null
					|| always && !origSrc.startsWith( MARK )
					|| ( origSrc.startsWith( MARK )
							&& ( !reuseBuf || force ) ) )
			{
				DecompilerSourceMapper sourceMapper = SourceMapperFactory
						.getSourceMapper( decompilerType );
				char[] src = sourceMapper == null ? null
						: sourceMapper.findSource( cf.getType( ) );
				if ( src == null )
				{
					if ( DecompilerType.JAD.equals( decompilerType ) )
					{
						src = SourceMapperFactory
								.getSourceMapper( DecompilerType.JAD )
								.findSource( cf.getType( ) );
					}
					else if ( DecompilerType.JDCORE.equals( decompilerType ) )
					{
						src = SourceMapperFactory
								.getSourceMapper( DecompilerType.JDCORE )
								.findSource( cf.getType( ) );
					}
					else if ( DecompilerType.FernFlower.equals( decompilerType ) )
					{
						src = SourceMapperFactory
								.getSourceMapper( DecompilerType.FernFlower )
								.findSource( cf.getType( ) );
					}
					else if ( DecompilerType.CFR.equals( decompilerType ) )
					{
						src = SourceMapperFactory
								.getSourceMapper( DecompilerType.CFR )
								.findSource( cf.getType( ) );
					}
					else if ( DecompilerType.PROCYON.equals( decompilerType ) )
					{
						src = SourceMapperFactory
								.getSourceMapper( DecompilerType.PROCYON )
								.findSource( cf.getType( ) );
					}
				}
				if ( src == null )
				{
					return false;
				}
				char[] markedSrc = src;
				IBuffer buffer = BufferManager.createBuffer( cf );
				buffer.setContents( markedSrc );
				getBufferManager( ).addBuffer( buffer );
				sourceMapper.mapSource( cf.getType( ), markedSrc, true );

				ClassFileSourceMap.updateSource( getBufferManager( ),
						(ClassFile) cf,
						markedSrc );

				opened = true;
			}
			return opened;

		}
		return false;
	}

	/**
	 * Sets edditor input only if buffer was actually opened.
	 * 
	 * @param force
	 *            if <code>true</code> initialize no matter what
	 */
	public void doSetInput( boolean force )
	{
		IEditorInput input = getEditorInput( );
		try
		{
			if ( doOpenBuffer( input, force ) )
			{
				super.doSetInput( input );
			}
		}
		catch ( Exception e )
		{
			JavaDecompilerPlugin.logError( e, "" ); //$NON-NLS-1$
		}
	}

	public void doSetInput( String type, boolean force )
	{
		IEditorInput input = getEditorInput( );
		try
		{
			if ( doOpenBuffer( input, type, force ) )
			{
				super.doSetInput( input );
			}
		}
		catch ( Exception e )
		{
			JavaDecompilerPlugin.logError( e, "" ); //$NON-NLS-1$
		}
	}

	protected void doSetInput( IEditorInput input ) throws CoreException
	{
		if ( input instanceof IFileEditorInput )
		{
			String filePath = UIUtil.getPathLocation(
					( (IFileEditorInput) input ).getStorage( ).getFullPath( ) );
			if ( filePath == null || !new File( filePath ).exists( ) )
			{
				super.doSetInput( input );
			}
			else
			{
				doSetInput( new DecompilerClassEditorInput(
						EFS.getLocalFileSystem( )
								.getStore( new Path( filePath ) ) ) );
			}
		}
		else if ( input instanceof FileStoreEditorInput )
		{

			FileStoreEditorInput storeInput = (FileStoreEditorInput) input;
			IPreferenceStore prefs = JavaDecompilerPlugin.getDefault( )
					.getPreferenceStore( );
			String decompilerType = prefs
					.getString( JavaDecompilerPlugin.DECOMPILER_TYPE );
			String source = DecompileUtil.decompiler( storeInput,
					decompilerType );

			if ( source != null )
			{
				String packageName = DecompileUtil.getPackageName( source );
				String classFullName = packageName == null
						? storeInput.getName( )
						: packageName
								+ "." //$NON-NLS-1$
								+ storeInput.getName( ).replaceAll(
										"(?i)\\.class", //$NON-NLS-1$
										"" ); //$NON-NLS-1$

				File file = new File( System.getProperty( "java.io.tmpdir" ), //$NON-NLS-1$
						storeInput.getName( ).replaceAll( "(?i)\\.class", //$NON-NLS-1$
								".java" ) ); //$NON-NLS-1$
				FileUtil.writeToFile( file, source );
				file.deleteOnExit( );

				DecompilerClassEditorInput editorInput = new DecompilerClassEditorInput(
						EFS.getLocalFileSystem( ).getStore(
								new Path( file.getAbsolutePath( ) ) ) );
				editorInput.setToolTipText( classFullName );

				IEditorPart editor = PlatformUI.getWorkbench( )
						.getActiveWorkbenchWindow( )
						.getActivePage( )
						.openEditor( editorInput,
								"org.eclipse.jdt.ui.CompilationUnitEditor" ); //$NON-NLS-1$
				try
				{
					ReflectionUtils.invokeMethod( editor,
							"setPartName", //$NON-NLS-1$
							new Class[]{
									String.class
							}, new String[]{
									storeInput.getName( )
							} );

					ReflectionUtils.invokeMethod( editor,
							"setTitleImage", //$NON-NLS-1$
							new Class[]{
									Image.class
							}, new Object[]{
									JavaDecompilerPlugin
											.getImageDescriptor(
													"icons/decompiler.png" ) //$NON-NLS-1$
											.createImage( )
							} );

					ReflectionUtils.setFieldValue( editor,
							"fIsEditingDerivedFileAllowed", //$NON-NLS-1$
							Boolean.valueOf( false ) );
				}
				catch ( Exception e )
				{
					JavaDecompilerPlugin.logError( e, "" ); //$NON-NLS-1$
				}
			}
			Display.getDefault( ).asyncExec( new Runnable( ) {

				public void run( )
				{
					JavaDecompilerClassFileEditor.this.getEditorSite( )
							.getPage( )
							.closeEditor( JavaDecompilerClassFileEditor.this,
									false );
				}
			} );

			throw new CoreException(
					new Status( 8,
							JavaDecompilerPlugin.PLUGIN_ID,
							1,
							"", //$NON-NLS-1$
							null ) );
		}
		else
		{
			if ( input instanceof InternalClassFileEditorInput )
			{
				InternalClassFileEditorInput classInput = (InternalClassFileEditorInput) input;

				IPath relativePath = classInput.getClassFile( )
						.getParent( )
						.getPath( );
				String location = UIUtil.getPathLocation( relativePath );
				if ( !( FileUtil.isZipFile( location )
						|| FileUtil.isZipFile( relativePath.toOSString( ) ) ) )
				{
					String filePath = UIUtil.getPathLocation(
							classInput.getClassFile( ).getPath( ) );
					if ( filePath != null )
					{
						DecompilerClassEditorInput editorInput = new DecompilerClassEditorInput(
								EFS.getLocalFileSystem( )
										.getStore( new Path( filePath ) ) );
						doSetInput( editorInput );
					}
					else
					{
						doSetInput( new DecompilerClassEditorInput(
								EFS.getLocalFileSystem( ).getStore( classInput
										.getClassFile( ).getPath( ) ) ) );
					}
					return;
				}
			}
			try
			{
				doOpenBuffer( input, false );
			}
			catch ( JavaModelException e )
			{
				IClassFileEditorInput classFileEditorInput = (IClassFileEditorInput) input;
				IClassFile file = classFileEditorInput.getClassFile( );

				if ( file.getSourceRange( ) == null
						&& file.getBytes( ) != null )
				{
					if ( ClassUtil.isClassFile( file.getBytes( ) ) )
					{
						File classFile = new File(
								JavaDecompilerPlugin.getDefault( )
										.getPreferenceStore( )
										.getString(
												JavaDecompilerPlugin.TEMP_DIR ),
								file.getElementName( ) );
						try
						{
							FileOutputStream fos = new FileOutputStream(
									classFile );
							fos.write( file.getBytes( ) );
							fos.close( );

							doSetInput( new DecompilerClassEditorInput(
									EFS.getLocalFileSystem( )
											.getStore( new Path( classFile
													.getAbsolutePath( ) ) ) ) );
							classFile.delete( );
							return;
						}
						catch ( IOException e1 )
						{
							JavaDecompilerPlugin.logError( e, "" ); //$NON-NLS-1$
						}
						finally
						{
							if ( classFile != null && classFile.exists( ) )
								classFile.delete( );
						}
					}
				}
			}
			super.doSetInput( input );
		}
	}

	protected JavaDecompilerBufferManager getBufferManager( )
	{
		JavaDecompilerBufferManager manager;
		BufferManager defManager = BufferManager.getDefaultBufferManager( );
		if ( defManager instanceof JavaDecompilerBufferManager )
			manager = (JavaDecompilerBufferManager) defManager;
		else
			manager = new JavaDecompilerBufferManager( defManager );
		return manager;
	}
}