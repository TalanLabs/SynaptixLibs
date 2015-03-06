package com.synaptix.swing.tooltip;

import java.awt.Color;
import java.awt.Font;

public class JRedToolTip extends JDefaultToolTip {

	private static final long serialVersionUID = -3518288502198135703L;

	public JRedToolTip(String text) {
		super(text);

		this.setForeground(new Color(255, 255, 255));
		this.setBorderColor(new Color(0xbd110e));
		this.setBottomColor(new Color(0xff2723));
		this.setTopColor(new Color(0xfe524e));
		this.setFont(new Font("Dialog", Font.BOLD, 11));
	}

}
