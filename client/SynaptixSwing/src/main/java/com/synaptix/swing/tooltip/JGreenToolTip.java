package com.synaptix.swing.tooltip;

import java.awt.Color;
import java.awt.Font;

public class JGreenToolTip extends JDefaultToolTip {

	private static final long serialVersionUID = -3518288502198135703L;

	public JGreenToolTip(String text) {
		super(text);

		this.setForeground(new Color(255, 255, 255));
		this.setBorderColor(new Color(0x206f0a));
		this.setBottomColor(new Color(0x2a9b0e));
		this.setTopColor(new Color(0x2da80f));
		this.setFont(new Font("Dialog", Font.BOLD, 11));
	}

}
