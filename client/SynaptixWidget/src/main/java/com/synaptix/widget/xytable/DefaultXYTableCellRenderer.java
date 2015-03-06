package com.synaptix.widget.xytable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;

public class DefaultXYTableCellRenderer extends JLabel implements XYTableCellRenderer {

	private static final long serialVersionUID = -8778628984142379086L;

	public DefaultXYTableCellRenderer() {
		super();

		this.setBackground(Color.WHITE);
		this.setForeground(Color.BLACK);
		this.setHorizontalAlignment(JLabel.CENTER);
		this.setOpaque(true);
	}

	@Override
	public Component getXYTableCellRendererComponent(JXYTable xyTable, Object value, int column, int row, boolean selected) {
		this.setText(value != null ? value.toString() : "");
		if (selected) {
			this.setBackground(Color.BLACK);
			this.setForeground(Color.WHITE);
		} else {
			this.setBackground(Color.WHITE);
			this.setForeground(Color.BLACK);
		}
		return this;
	}

}
