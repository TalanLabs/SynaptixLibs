package com.synaptix.swing.tooltip;

import java.awt.Color;
import java.awt.Font;

public class JOrangeToolTip extends JDefaultToolTip {

	private static final long serialVersionUID = -3518288502198135703L;

	public JOrangeToolTip(String text) {
		super(text);

		this.setForeground(new Color(255, 255, 255));
		this.setBorderColor(new Color(0x8f4f0d));
		this.setBottomColor(new Color(0xc96d11));
		this.setTopColor(new Color(0xdb7612));
		this.setFont(new Font("Dialog", Font.BOLD, 11));
	}

}
