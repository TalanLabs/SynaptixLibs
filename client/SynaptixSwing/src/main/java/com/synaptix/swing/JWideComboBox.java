package com.synaptix.swing;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

public class JWideComboBox extends JComboBox {

	private static final long serialVersionUID = -5117251545350212713L;

	private boolean layingOut = false;
	
	public JWideComboBox() {
	}

	public JWideComboBox(final Object items[]) {
		super(items);
	}

	public JWideComboBox(Vector<?> items) {
		super(items);
	}

	public JWideComboBox(ComboBoxModel aModel) {
		super(aModel);
	}

	public void doLayout() {
		try {
			layingOut = true;
			super.doLayout();
		} finally {
			layingOut = false;
		}
	}

	public Dimension getSize() {
		Dimension dim = super.getSize();
		if (!layingOut)
			dim.width = Math.max(dim.width, getPreferredSize().width);
		return dim;
	}
}
