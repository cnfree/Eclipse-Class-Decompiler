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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.sf.feeling.decompiler.JavaDecompilerPlugin;

public class ReflectionUtils
{

	public static Method getDeclaredMethod( Object object, String methodName,
			Class[] parameterTypes )
	{
		Method method = null;

		for ( Class clazz = object
				.getClass( ); clazz != Object.class; clazz = clazz
						.getSuperclass( ) )
		{
			try
			{
				method = clazz.getDeclaredMethod( methodName, parameterTypes );
				return method;
			}
			catch ( Exception e )
			{

			}
		}

		return null;
	}

	public static Object invokeMethod( Object object, String methodName,
			Class[] parameterTypes, Object[] parameters )
	{

		Method method = getDeclaredMethod( object, methodName, parameterTypes );
		try
		{
			if ( null != method )
			{
				method.setAccessible( true );
				return method.invoke( object, parameters );
			}
		}
		catch ( Exception e )
		{
			JavaDecompilerPlugin.logError( e, "" ); //$NON-NLS-1$
		}

		return null;
	}

	public static Field getDeclaredField( Object object, String fieldName )
	{
		Field field = null;

		Class clazz = object.getClass( );

		for ( ; clazz != Object.class; clazz = clazz.getSuperclass( ) )
		{
			try
			{
				field = clazz.getDeclaredField( fieldName );
				return field;
			}
			catch ( Exception e )
			{

			}
		}

		return null;
	}

	public static void setFieldValue( Object object, String fieldName,
			Object value )
	{

		Field field = getDeclaredField( object, fieldName );

		try
		{
			if ( field != null )
			{
				field.setAccessible( true );
				field.set( object, value );
			}
		}
		catch ( Exception e )
		{
			JavaDecompilerPlugin.logError( e, "" ); //$NON-NLS-1$
		}

	}

	public static Object getFieldValue( Object object, String fieldName )
	{

		Field field = getDeclaredField( object, fieldName );

		try
		{
			if ( field != null )
			{
				field.setAccessible( true );
				return field.get( object );
			}

		}
		catch ( Exception e )
		{
			JavaDecompilerPlugin.logError( e, "" ); //$NON-NLS-1$
		}

		return null;
	}
}