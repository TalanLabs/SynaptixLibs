package com.synaptix.widget.xytable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;

public class DefaultXYTableRowHeaderRenderer extends JLabel implements XYTableRowHeaderRenderer {

	private static final long serialVersionUID = -8778628984142379086L;

	public DefaultXYTableRowHeaderRenderer() {
		super();

		this.setBackground(Color.GRAY);
		this.setForeground(Color.WHITE);
		this.setHorizontalAlignment(JLabel.CENTER);
		this.setOpaque(true);
		this.setFont(this.getFont().deriveFont(Font.BOLD));
	}

	@Override
	public Component getXYTableRowHeaderRendererComponent(JXYTable xyTable, String name, int row, boolean selected) {
		this.setText(name != null ? name : "");
		if (selected) {
			this.setBackground(Color.WHITE);
			this.setForeground(Color.GRAY);
		} else {
			this.setBackground(Color.GRAY);
			this.setForeground(Color.WHITE);
		}
		return this;
	}

}
