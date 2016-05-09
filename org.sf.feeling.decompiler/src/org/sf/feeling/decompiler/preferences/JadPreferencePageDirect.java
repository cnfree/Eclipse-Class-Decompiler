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

package org.sf.feeling.decompiler.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.fieldeditors.NoWSFieldEditor;

public class JadPreferencePageDirect extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	public JadPreferencePageDirect() {
		super(1);
		setPreferenceStore(JavaDecompilerPlugin.getDefault()
				.getPreferenceStore());
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
	}

	protected void createFieldEditors() {
		addField(new BooleanFieldEditor("-dead",
				"Try to decompile dead parts of code", getFieldEditorParent()));

		addField(new BooleanFieldEditor("-dis", "Disassembler only",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor("-noconv",
				"Don't convert Java identifiers into valid ones",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor("-nocast",
				"Don't generate auxiliary casts", getFieldEditorParent()));

		addField(new BooleanFieldEditor("-noclass",
				"Don't convert .class operators", getFieldEditorParent()));

		addField(new BooleanFieldEditor("-nocode",
				"Don't generate the source code for methods",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor("-noctor",
				"Suppress the empty constructors", getFieldEditorParent()));

		addField(new BooleanFieldEditor("-nodos",
				"Turn off check for class files written in DOS mode",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor("-nofd",
				"Don't disambiguate fields with the same names",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor("-noinner",
				"Turn off the support of inner classes", getFieldEditorParent()));

		addField(new BooleanFieldEditor("-nolvt",
				"Ignore Local Variable Table entries", getFieldEditorParent()));

		addField(new BooleanFieldEditor("-safe",
				"Generate additional casts to disambiguate methods/fields",
				getFieldEditorParent()));

		NoWSFieldEditor packpref = new NoWSFieldEditor("-pa",
				"Prefix for all packages in generated source files",
				getFieldEditorParent());
		addField(packpref);

		NoWSFieldEditor numclpref = new NoWSFieldEditor("-pc",
				"Prefix for classes with numerical names",
				getFieldEditorParent());
		addField(numclpref);

		NoWSFieldEditor excpref = new NoWSFieldEditor("-pe",
				"Prefix for unused exception names", getFieldEditorParent());
		addField(excpref);

		NoWSFieldEditor numfipref = new NoWSFieldEditor("-pf",
				"Prefix for fields with numerical names",
				getFieldEditorParent());
		addField(numfipref);

		NoWSFieldEditor numlopref = new NoWSFieldEditor("-pl",
				"Prefix for locals with numerical names",
				getFieldEditorParent());
		addField(numlopref);

		NoWSFieldEditor nummepref = new NoWSFieldEditor("-pm",
				"Prefix for methods with numerical names",
				getFieldEditorParent());
		addField(nummepref);

		NoWSFieldEditor numpapref = new NoWSFieldEditor("-pp",
				"Prefix for method parms with numerical names",
				getFieldEditorParent());
		addField(numpapref);
	}

	public void init(IWorkbench arg0) {
	}
}