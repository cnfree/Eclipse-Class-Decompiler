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
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.fieldeditors.StringChoiceFieldEditor;

public class JadPreferencePageFormat extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	public JadPreferencePageFormat() {
		super(1);
		setPreferenceStore(JavaDecompilerPlugin.getDefault()
				.getPreferenceStore());
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
	}

	protected void createFieldEditors() {
		addField(new BooleanFieldEditor("-a",
				"Generate JVM instructions as comments", getFieldEditorParent()));

		addField(new BooleanFieldEditor("-af",
				"Output fully qualified names when annotating",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor("-b", "Generate redundant braces",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor("-clear", "Clear all prefixes",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor("-f", "Generate fully qualified names",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor("-ff", "Output fields before methods",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor("-i",
				"Print default initializers for fields", getFieldEditorParent()));

		addField(new BooleanFieldEditor("-nonlb",
				"Don't insert a newline before opening brace",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor("-space",
				"Output space between keyword (if, while, etc) and expression",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor("-nl",
				"Split strings on newline characters", getFieldEditorParent()));

		IntegerFieldEditor splitstr = new IntegerFieldEditor("-l",
				"Split strings into pieces of max chars (0=dis)",
				getFieldEditorParent());

		addField(splitstr);

		StringChoiceFieldEditor iradix = new StringChoiceFieldEditor("-radix",
				"Display integers using the specified radix",
				getFieldEditorParent());
		iradix.addItem("8", "8");
		iradix.addItem("10", "10");
		iradix.addItem("16", "16");
		addField(iradix);

		StringChoiceFieldEditor lradix = new StringChoiceFieldEditor("-lradix",
				"Display long integers using the specified radix",
				getFieldEditorParent());
		lradix.addItem("8", "8");
		lradix.addItem("10", "10");
		lradix.addItem("16", "16");
		addField(lradix);

		addField(new IntegerFieldEditor("-pi",
				"Pack imports into one line using .* (0=dis)",
				getFieldEditorParent()));

		addField(new IntegerFieldEditor("-pv",
				"Pack fields with the same types into one line (0=dis)",
				getFieldEditorParent()));

		StringChoiceFieldEditor indent = new StringChoiceFieldEditor("-t",
				"Number of spaces for indentation: ", getFieldEditorParent());
		indent.addItem("tab", "use tab");
		indent.addItem("0", "0");
		indent.addItem("1", "1");
		indent.addItem("2", "2");
		indent.addItem("3", "3");
		indent.addItem("4", "4");
		indent.addItem("5", "5");
		indent.addItem("6", "6");
		indent.addItem("7", "7");
		indent.addItem("8", "8");
		indent.addItem("9", "9");
		indent.addItem("10", "10");
		indent.addItem("11", "11");
		indent.addItem("12", "12");
		indent.addItem("13", "13");
		indent.addItem("14", "14");
		indent.addItem("15", "15");
		addField(indent);
	}

	public void init(IWorkbench arg0) {
	}
}