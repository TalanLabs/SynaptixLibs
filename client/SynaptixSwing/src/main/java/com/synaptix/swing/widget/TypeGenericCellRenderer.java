package com.synaptix.swing.widget;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.synaptix.swing.utils.GenericObjectToString;

public class TypeGenericCellRenderer<T> extends JLabel implements
		ListCellRenderer, TableCellRenderer {

	private static final long serialVersionUID = 7388584552867300961L;

	private GenericObjectToString<T> os;

	public TypeGenericCellRenderer(GenericObjectToString<T> os) {
		super("", JLabel.LEFT);

		this.os = os;

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
		setValue(value);

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
		setValue(value);

		return this;
	}

	@SuppressWarnings("unchecked")
	private void setValue(Object value) {
		T t = (T) value;
		this.setText(os.getString(t));
	}
}
