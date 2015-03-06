package com.synaptix.widget.hierarchical.writer;

public interface IExportableTable {

	/**
	 * How much horizontal space does the table need?
	 * 
	 * @return
	 */
	public int getExportWidth();

	/**
	 * How much vertical space does the table need?
	 * 
	 * @return
	 */
	public int getExportHeight();

	/**
	 * Get the cell information on given coordinates
	 * 
	 * @param rowIndex
	 *            relative to the current table
	 * @param columnIndex
	 *            relative to the current table
	 * @return
	 */
	public CellInformation getCellInformation(int rowIndex, int columnIndex);

	/**
	 * Get the size of a column
	 * 
	 * @param columnIndex
	 * @return
	 */
	public int getColumnWidth(int columnIndex);

}
