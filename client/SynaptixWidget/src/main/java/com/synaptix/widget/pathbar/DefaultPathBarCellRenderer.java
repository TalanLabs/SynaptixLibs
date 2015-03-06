package com.synaptix.widget.pathbar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class DefaultPathBarCellRenderer extends JLabel implements PathBarCellRenderer {

	private static final long serialVersionUID = -3642978383135222891L;

	private static final Color selectedColor = Color.GRAY;

	private static final Color normalColor = new Color(240, 240, 240);

	private Font normalFont;

	private Font boldFont;

	public DefaultPathBarCellRenderer() {
		super();

		this.setHorizontalAlignment(JLabel.CENTER);
		this.setOpaque(true);
		this.setBackground(Color.white);
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	@Override
	public Component getPathBarCellRendererComponent(JPathBar pathBar, Object value, int index, boolean selected) {
		this.setText(value != null ? value.toString() : "null");
		this.setBackground(selected ? selectedColor : normalColor);
		this.setForeground(selected ? Color.white : Color.black);
		// this.setOpaque(selected);
		if (normalFont == null) {
			normalFont = this.getFont().deriveFont(Font.BOLD);
			boldFont = this.getFont().deriveFont(Font.BOLD, 13);
		}
		this.setFont(selected ? boldFont : normalFont);
		return this;
	}
}
