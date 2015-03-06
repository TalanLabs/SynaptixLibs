package com.synaptix.widget.grid;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class DefaultGridCellRenderer extends JPanel implements GridCellRenderer {

	private static final long serialVersionUID = 2568772188369862065L;

	private static final Border normalBorder = BorderFactory.createLineBorder(new Color(150, 100, 0));

	private JLabel label;

	public DefaultGridCellRenderer() {
		super(new BorderLayout());

		label = new JLabel();
		label.setOpaque(true);
		label.setBackground(Color.ORANGE);
		label.setHorizontalAlignment(JLabel.CENTER);

		this.add(label, BorderLayout.CENTER);
		this.setOpaque(false);
		this.setBorder(normalBorder);
	}

	@Override
	public Component getGridCellRendererComponent(JGridCell gridPanel, Object value) {
		label.setText(value.toString());

		return this;
	}

	@Override
	public boolean isVisible() {
		return false;
	}
}