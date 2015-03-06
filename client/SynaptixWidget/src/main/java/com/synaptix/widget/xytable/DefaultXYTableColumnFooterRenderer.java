package com.synaptix.widget.xytable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;

public class DefaultXYTableColumnFooterRenderer extends JLabel implements XYTableColumnFooterRenderer {

	private static final long serialVersionUID = -8778628984142379086L;

	public DefaultXYTableColumnFooterRenderer() {
		super();

		this.setBackground(Color.GRAY);
		this.setForeground(Color.WHITE);
		this.setHorizontalAlignment(JLabel.CENTER);
		this.setOpaque(true);
		this.setFont(this.getFont().deriveFont(Font.BOLD));
	}

	@Override
	public Component getXYTableColumnFooterRendererComponent(JXYTable xyTable, Object value, int column, boolean selected) {
		this.setText(value != null ? value.toString() : "");
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
