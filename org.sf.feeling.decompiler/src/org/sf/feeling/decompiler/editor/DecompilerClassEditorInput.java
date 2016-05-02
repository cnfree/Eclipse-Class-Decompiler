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

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.ide.FileStoreEditorInput;

public class DecompilerClassEditorInput extends FileStoreEditorInput
{

	private String toolTipText = null;

	public DecompilerClassEditorInput( IFileStore fileStore )
	{
		super( fileStore );
	}

	public String getToolTipText( )
	{
		if ( toolTipText != null )
			return toolTipText;
		else
			return super.getToolTipText( );
	}

	public void setToolTipText( String toolTipText )
	{
		this.toolTipText = toolTipText;
	}

}
