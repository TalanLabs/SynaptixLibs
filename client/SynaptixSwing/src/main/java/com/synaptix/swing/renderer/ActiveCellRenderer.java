package com.synaptix.swing.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.synaptix.swing.SwingMessages;

public class ActiveCellRenderer extends JLabel implements ListCellRenderer,
		TableCellRenderer {

	private static final long serialVersionUID = 5506422227017626894L;

	public ActiveCellRenderer() {
		super("", JLabel.CENTER); //$NON-NLS-1$
		this.setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		if (value != null && value instanceof Boolean) {
			boolean active = (Boolean) value;
			this.setText(active ? SwingMessages.getString("ActiveCellRenderer.1") : SwingMessages.getString("ActiveCellRenderer.2")); //$NON-NLS-1$ //$NON-NLS-2$
			if (!isSelected) {
				setBackground(active ? Color.GREEN : Color.RED);
			}
		} else {
			this.setText(null);
		}
		return this;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		if (value != null && value instanceof Boolean) {
			boolean active = (Boolean) value;
			this.setText(active ? SwingMessages.getString("ActiveCellRenderer.1") : SwingMessages.getString("ActiveCellRenderer.2")); //$NON-NLS-1$ //$NON-NLS-2$

			if (!isSelected) {
				setBackground(active ? Color.GREEN : Color.RED);
			}
		} else {
			this.setText(null);
		}
		return this;
	}
}