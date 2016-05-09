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

package org.sf.feeling.decompiler.jad.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;

public class JadPreferencePageMisc extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	public JadPreferencePageMisc() {
		super(1);
		setPreferenceStore(JavaDecompilerPlugin.getDefault()
				.getPreferenceStore());
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
	}

	protected void createFieldEditors() {
		addField(new BooleanFieldEditor("-stat",
				"Show the total number of processed classes/methods/fields",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor("-v",
				"Show method names while decompiling", getFieldEditorParent()));

		addField(new BooleanFieldEditor("-8",
				"Convert Unicode strings into ANSI strings",
				getFieldEditorParent()));
	}

	public void init(IWorkbench arg0) {
	}
}