package com.synaptix.swing;

import java.awt.Rectangle;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class JSelectableLabel extends JTextField {

	private static final long serialVersionUID = 4873047563241662634L;

	public JSelectableLabel() {
		this(null);
	}

	public JSelectableLabel(String text) {
		super(text);

		this.setEditable(false);
		this.setBorder(null);
		this.setForeground(UIManager.getColor("Label.foreground"));
		this.setFont(UIManager.getFont("Label.font"));
		this.setOpaque(false);
	}

	@Override
	public void scrollRectToVisible(Rectangle r) {
		// scroll problem
	}

	@Override
	public void setBorder(Border border) {
		super.setBorder(null);
	}
}
