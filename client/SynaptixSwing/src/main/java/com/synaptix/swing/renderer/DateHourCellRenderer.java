package com.synaptix.swing.renderer;

import java.awt.Component;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.synaptix.swing.utils.DateTimeUtils;

public class DateHourCellRenderer extends JLabel implements ListCellRenderer,
		TableCellRenderer {

	private static final long serialVersionUID = 7388584552867300961L;

	public DateHourCellRenderer() {
		super("", JLabel.LEFT); //$NON-NLS-1$
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
		if (value != null && value instanceof Date) {
			Date date = (Date) value;
			this.setText(DateTimeUtils.formatLongDate(date));
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
		if (value != null && value instanceof Date) {
			Date date = (Date) value;
			this.setText(DateTimeUtils.formatLongDate(date));
		} else {
			this.setText(null);
		}
		return this;
	}
}