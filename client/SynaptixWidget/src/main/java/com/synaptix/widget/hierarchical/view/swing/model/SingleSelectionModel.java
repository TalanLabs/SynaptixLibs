package com.synaptix.widget.hierarchical.view.swing.model;

/*package*/class SingleSelectionModel implements SpecificHierarchicalSelectionModel {

	private final HierarchicalSelectionModel<?, ?, ?> parent;

	public SingleSelectionModel(final HierarchicalSelectionModel<?, ?, ?> parent) {
		this.parent = parent;
	}

	@Override
	public void selectAllModel() {
		this.parent.cleanSelectionModel();
	}

	@Override
	public void setSelectedAt(final int rowIndex, final int columnIndex) {
		this.parent.cleanSelectionModel();
		this.parent._setSelectedAt(rowIndex, columnIndex);
	}

	@Override
	public Boolean changeSelectionStatusAt(final int rowIndex, final int columnIndex) {
		this.parent.cleanSelectionModel();
		this.parent._setSelectedAt(rowIndex, columnIndex);
		return this.parent.isSelectedAt(rowIndex, columnIndex);
	}

	@Override
	public void changeColumnSelectionStatusAt(final int columnIndex) {
	}

	@Override
	public void changeRowSelectionStatusAt(final int rowIndex) {
	}

	@Override
	public void changeRowSelectionStatusByRange(final int lowerBoundIndex, final int upperBoundIndex) {
	}

	@Override
	public void changeColumnSelectionStatusByRange(final int lowerBoundIndex, final int upperBoundIndex) {
	}

}
