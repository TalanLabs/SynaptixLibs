package com.synaptix.widget.renderer.view.swing;

import java.awt.Component;

import javax.swing.JTable;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

public class TypeTableToStringSubstanceTableCellRenderer extends SubstanceDefaultTableCellRenderer {

	private static final long serialVersionUID = -2574227069335201208L;

	protected final ITableToString tableToString;

	public TypeTableToStringSubstanceTableCellRenderer(ITableToString valueTable) {
		super();
		this.tableToString = valueTable;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return super.getTableCellRendererComponent(table, tableToString.getString(table, value, row, column), isSelected, hasFocus, row, column);
	}
}
