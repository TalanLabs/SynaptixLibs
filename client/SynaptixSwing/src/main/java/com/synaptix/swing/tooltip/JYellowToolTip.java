package com.synaptix.swing.tooltip;

import java.awt.Color;
import java.awt.Font;

public class JYellowToolTip extends JDefaultToolTip {

	private static final long serialVersionUID = -3518288502198135703L;

	public JYellowToolTip(String text) {
		super(text);

		this.setForeground(new Color(0, 0, 0));
		this.setBorderColor(new Color(0x9f7f3f));
		this.setBottomColor(new Color(0xffdf3f));
		this.setTopColor(new Color(0xffff5f));
		this.setFont(new Font("Dialog", Font.BOLD, 11));
	}

}
