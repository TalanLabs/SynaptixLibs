package com.synaptix.swing.tooltip;

import java.awt.Color;
import java.awt.Font;

public class JVioletToolTip extends JDefaultToolTip {

	private static final long serialVersionUID = -3518288502198135703L;

	public JVioletToolTip(String text) {
		super(text);

		this.setForeground(Color.white);
		this.setBorderColor(new Color(0x0e11bd));
		this.setBottomColor(new Color(0x2327ff));
		this.setTopColor(new Color(0x4e52fe));
		this.setFont(new Font("Dialog", Font.BOLD, 11));
	}

}
