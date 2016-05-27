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

import java.util.Enumeration;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.BufferManager;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.util.UIUtil;

/**
 * This class is a hack that replaces JDT <code>BufferManager</code> in order to
 * make <code>addBuffer()</code> and <code>removeBuffer()</code> accessible.
 */
public class JavaDecompilerBufferManager extends BufferManager
{

	public static void closeDecompilerBuffers( boolean all )
	{
		BufferManager manager = BufferManager.getDefaultBufferManager( );
		if ( manager instanceof JavaDecompilerBufferManager )
		{
			Enumeration enumeration = manager.getOpenBuffers( );
			while ( enumeration.hasMoreElements( ) )
			{
				IBuffer buffer = (IBuffer) enumeration.nextElement( );
				IOpenable owner = buffer.getOwner( );
				if ( owner instanceof IClassFile
						&& buffer.getContents( ) != null
						&& buffer.getContents( ).startsWith(
								JavaDecompilerClassFileEditor.MARK ) )
				{
					resetBuffer( all, manager, buffer, owner );
				}
			}
		}
	}

	private static void resetBuffer( boolean all, BufferManager manager,
			IBuffer buffer, IOpenable owner )
	{
		JavaDecompilerBufferManager jManager = (JavaDecompilerBufferManager) manager;
		jManager.removeBuffer( buffer );
		if ( !all ) // restore buffers for files without source
		{
			IClassFile cf = (IClassFile) owner;
			String realSource = null;
			try
			{
				realSource = cf.getSource( );
			}
			catch ( JavaModelException e )
			{
				IStatus err = new Status( IStatus.ERROR,
						JavaDecompilerPlugin.PLUGIN_ID,
						0,
						"failed to get source while flushing buffers", //$NON-NLS-1$
						e );
				JavaDecompilerPlugin.getDefault( ).getLog( ).log( err );
			}
			if ( realSource == null )
				jManager.addBuffer( buffer );
		}
	}

	public JavaDecompilerBufferManager( BufferManager manager )
	{
		super( );
		synchronized ( BufferManager.class )
		{
			Enumeration enumeration = manager.getOpenBuffers( );
			while ( enumeration.hasMoreElements( ) )
			{
				IBuffer buffer = (IBuffer) enumeration.nextElement( );
				addBuffer( buffer );
			}
			BufferManager.DEFAULT_BUFFER_MANAGER = this;
		}
	}

	public void addBuffer( IBuffer buffer )
	{
		if ( buffer.getContents( ) == null )
			return;
		super.addBuffer( buffer );
	}

	public void removeBuffer( IBuffer buffer )
	{
		super.removeBuffer( buffer );
	}

	public IBuffer getBuffer( final IOpenable owner )
	{
		IPreferenceStore prefs = JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( );
		boolean always = prefs
				.getBoolean( JavaDecompilerPlugin.IGNORE_EXISTING );

		if ( !always )
		{
			return super.getBuffer( owner );
		}

		IBuffer buffer = super.getBuffer( owner );
		final IBuffer[] buffers = new IBuffer[]{
				buffer
		};

		if ( requestFromJavadocHover( ) )
		{

			if ( buffers[0] == null
					|| buffers[0].getContents( ) == null
					|| buffers[0].getContents( )
							.startsWith( JavaDecompilerClassFileEditor.MARK ) )
			{
				buffers[0] = null;

				Display.getDefault( ).asyncExec( new Runnable( ) {

					public void run( )
					{
						JavaDecompilerClassFileEditor editor = UIUtil
								.getActiveEditor( );
						if ( editor != null
								&& editor
										.getEditorInput( ) instanceof IClassFileEditorInput )
						{
							IClassFile input = ( (IClassFileEditorInput) editor
									.getEditorInput( ) ).getClassFile( );
							if ( owner.equals( input ) )
							{
								buffers[0] = editor.getClassBuffer( );
								JavaDecompilerBufferManager.this
										.addBuffer( buffers[0] );
							}
						}
					}
				} );
			}
		}

		return buffers[0];
	}

	private boolean requestFromJavadocHover( )
	{
		StackTraceElement[] stacks = Thread.currentThread( ).getStackTrace( );
		for ( int i = 0; i < stacks.length; i++ )
		{
			if ( stacks[i].getClassName( ).indexOf( "BinaryType" ) != -1
					&& stacks[i].getMethodName( ).equals( "getJavadocRange" ) )
				return false;

			if ( stacks[i].getClassName( ).indexOf( "JavadocHover" ) != -1
					&& stacks[i].getMethodName( ).equals( "getHoverInfo" ) )
				return true;
		}
		return false;
	}
}
