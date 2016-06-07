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

package jd.ide.eclipse;

import java.lang.reflect.Field;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class JavaDecompilerPlugin extends AbstractUIPlugin
{

	public static final String PLUGIN_ID = "jd.ide.eclipse"; //$NON-NLS-1$

	private static JavaDecompilerPlugin plugin;

	public static JavaDecompilerPlugin getDefault( )
	{
		if ( plugin == null )
			plugin = new JavaDecompilerPlugin( );

		IPreferenceStore store = plugin.getPreferenceStore( );
		store.setDefault( org.sf.feeling.decompiler.JavaDecompilerPlugin.PREF_DISPLAY_LINE_NUMBERS, false );
		store.setDefault( org.sf.feeling.decompiler.JavaDecompilerPlugin.PREF_DISPLAY_METADATA, false );

		return plugin;
	}

	public IPreferenceStore getPreferenceStore( )
	{
		IPreferenceStore store = org.sf.feeling.decompiler.JavaDecompilerPlugin.getDefault( ).getPreferenceStore( );

		try
		{
			Field field = Plugin.class.getDeclaredField( "preferences" ); //$NON-NLS-1$
			if ( field != null )
			{
				field.setAccessible( true );
				field.set( this, org.sf.feeling.decompiler.JavaDecompilerPlugin.getDefault( ).getPluginPreferences( ) );
			}
		}

		catch ( Exception e )
		{
			e.printStackTrace( );
		}

		return store;
	}
}