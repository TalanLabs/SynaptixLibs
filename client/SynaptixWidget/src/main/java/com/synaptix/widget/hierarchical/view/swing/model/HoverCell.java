package com.synaptix.widget.hierarchical.view.swing.model;

import com.synaptix.widget.hierarchical.view.swing.component.HierarchicalPanelKind;

public final class HoverCell {

	private final HierarchicalPanelKind panelKind;

	private final int rowIndex;

	private final int columnIndex;

	private final Object value;

	public HoverCell(HierarchicalPanelKind panelKind, int rowIndex, int columnIndex, Object value) {
		super();

		this.panelKind = panelKind;
		this.rowIndex = rowIndex;
		this.columnIndex = columnIndex;
		this.value = value;
	}

	public final HierarchicalPanelKind getPanelKind() {
		return panelKind;
	}

	public final int getRowIndex() {
		return rowIndex;
	}

	public final int getColumnIndex() {
		return columnIndex;
	}

	public final Object getValue() {
		return value;
	}
}
