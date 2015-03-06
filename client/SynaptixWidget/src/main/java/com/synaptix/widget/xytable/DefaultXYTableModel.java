package com.synaptix.widget.xytable;

public class DefaultXYTableModel extends AbstractXYTableModel {

	private int nbColumn;

	private int nbRow;

	public DefaultXYTableModel() {
		this(5, 5);
	}

	public DefaultXYTableModel(int nbColumn, int nbRow) {
		super();

		this.nbColumn = nbColumn;
		this.nbRow = nbRow;
	}

	@Override
	public int getColumnCount() {
		return nbColumn;
	}

	@Override
	public String getColumnName(int column) {
		return "Column_" + column;
	}

	@Override
	public Object getColumnValue(int column) {
		return column;
	}

	@Override
	public int getRowCount() {
		return nbRow;
	}

	@Override
	public String getRowName(int row) {
		return "Row" + row;
	}

	@Override
	public Object getRowValue(int row) {
		return row;
	}

	@Override
	public Object getValue(int column, int row) {
		return row * nbColumn + column;
	}
}
