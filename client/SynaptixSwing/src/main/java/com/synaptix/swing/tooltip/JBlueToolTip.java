package com.synaptix.swing.tooltip;

import java.awt.Color;
import java.awt.Font;

public class JBlueToolTip extends JDefaultToolTip {

	private static final long serialVersionUID = -3518288502198135703L;

	public JBlueToolTip(String text) {
		super(text);

		this.setForeground(Color.white);
		this.setBorderColor(new Color(0x005cc4));
		this.setBottomColor(new Color(0x0068cc));
		this.setTopColor(new Color(0x0086e1));
		this.setFont(new Font("Dialog", Font.BOLD, 11));
	}

}
