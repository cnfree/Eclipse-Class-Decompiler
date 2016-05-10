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

package org.sf.feeling.decompiler.jad;

import java.util.List;

/**
 * Generic Java Decompiler Interface. Decompilers work in file system terms, so
 * methods take file path-like parameters as opposed to Java style package and
 * class names. For instance, if a class com.acme.Foo that is be found under
 * directory /bar needs to be decompiled then the following should be passed to
 * a <code>IDecompiler</code> instance: root = /bar packege = com/acme className
 * = Foo.class
 */
public interface IDecompiler
{

	/**
	 * Decompiles a class file located in file system (not in archive)
	 * 
	 * @param root
	 *            path to the directory which is root for this class package
	 *            hierarchy
	 * @param packege
	 *            file path like package name
	 * @param className
	 *            file name of the class file
	 */
	public void decompile( String root, String packege, String className );

	/**
	 * Decompiles a class file located in archive.
	 * 
	 * @param archivePath
	 *            path to archive that contains the class to be decompiled
	 * @param packege
	 *            file path like package name
	 * @param className
	 *            file name of the class file
	 */
	public void decompileFromArchive( String archivePath, String packege,
			String className );

	/**
	 * @return time taken by decompilation
	 */
	public long getDecompilationTime( );

	/**
	 * If any exceptions occured during decompilation thaey should be included
	 * into the <code>List</code> returned by this method.
	 * 
	 * @return non-<code>null</code> value which is a list containing
	 *         <code>java.lang.Exception</code>'s
	 */
	public List getExceptions( );

	/**
	 * @return decompilation log specific to physical decompiler
	 */
	public String getLog( );

	/**
	 * @return Class file source which is the result of decompilation.
	 */
	public String getSource( );
}