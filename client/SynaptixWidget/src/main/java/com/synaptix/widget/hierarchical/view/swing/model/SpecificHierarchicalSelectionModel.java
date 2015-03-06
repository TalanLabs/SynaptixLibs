package com.synaptix.widget.hierarchical.view.swing.model;

interface SpecificHierarchicalSelectionModel {

	public abstract void selectAllModel();

	public abstract void setSelectedAt(int rowIndex, int columnIndex);

	public abstract Boolean changeSelectionStatusAt(int rowIndex, int columnIndex);

	public abstract void changeColumnSelectionStatusAt(int columnIndex);

	public abstract void changeRowSelectionStatusAt(int rowIndex);

	public abstract void changeRowSelectionStatusByRange(int lowerBoundIndex, int upperBoundIndex);

	public abstract void changeColumnSelectionStatusByRange(int lowerBoundIndex, int upperBoundIndex);

}