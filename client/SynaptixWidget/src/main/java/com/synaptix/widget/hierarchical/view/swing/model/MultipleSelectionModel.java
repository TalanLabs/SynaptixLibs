package com.synaptix.widget.hierarchical.view.swing.model;


public class MultipleSelectionModel implements SpecificHierarchicalSelectionModel {

	private final HierarchicalSelectionModel<?, ?, ?> parent;

	public MultipleSelectionModel(final HierarchicalSelectionModel<?, ?, ?> parent) {
		this.parent = parent;
	}

	@Override
	public void selectAllModel() {
		for (int rowIndex = 0; rowIndex < this.parent.getNbRows(); rowIndex++) {
			this.parent._selectRow(rowIndex);
		}
	}

	@Override
	public void setSelectedAt(final int rowIndex, final int columnIndex) {
		this.parent._setSelectedAt(rowIndex, columnIndex);
	}

	@Override
	public Boolean changeSelectionStatusAt(final int rowIndex, final int columnIndex) {
		this.parent._changeSelectionStatusAt(rowIndex, columnIndex);
		return this.parent.isSelectedAt(rowIndex, columnIndex);
	}

	@Override
	public void changeColumnSelectionStatusAt(final int columnIndex) {
		this.parent._changeColumnSelectionStatusAt(columnIndex);
	}

	@Override
	public void changeRowSelectionStatusAt(final int rowIndex) {
		this.parent._changeRowSelectionStatusAt(rowIndex);
	}

	@Override
	public void changeRowSelectionStatusByRange(final int lowerBoundIndex, final int upperBoundIndex) {
		this.parent._changeRowSelectionStatusByRange(lowerBoundIndex, upperBoundIndex);
	}

	@Override
	public void changeColumnSelectionStatusByRange(final int lowerBoundIndex, final int upperBoundIndex) {
		this.parent._changeColumnSelectionStatusByRange(lowerBoundIndex, upperBoundIndex);
	}

}
