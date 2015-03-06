/* 
 * Copyright (c) 2002, Cameron Zemek
 * 
 * This file is part of JGrid.
 * 
 * JGrid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * JGrid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sf.jeppers.grid;

/**
 * Describes a rectangular array of cells. Unlike
 * <code>javax.swing.table.TableModel</code> this data model is suitable for a
 * symmetrical grid. That is, there is no column (or row) focus.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public interface GridModel {
	/**
	 * Adds a listener to the list that's notified each time a change to the
	 * data model occurs.
	 * 
	 * @param listener
	 *            the GridModelListener to add
	 */
	public void addGridModelListener(GridModelListener listener);

	/**
	 * Removes a listener from the list that's notified each time a change to
	 * the data model occurs.
	 * 
	 * @param listener
	 *            the GridModelListener to remove
	 */
	public void removeGridModelListener(GridModelListener listener);

	/**
	 * Returns the value for the cell at <code>row</code> and
	 * <code>column</code>
	 * 
	 * @param row
	 *            row index of cell
	 * @param column
	 *            column index of cell
	 * @return the value of the cell
	 */
	public Object getValueAt(int row, int column);

	/**
	 * Retourne la class pour la cellule <code>row</code> et <code>column</code>
	 * 
	 * @param row
	 *            row index of cell
	 * @param column
	 *            column index of cell
	 * @return the class of the cell
	 */
	public Class<?> getClassAt(int row, int column);

	/**
	 * Returns true if the cell at <code>row</code> and <code>column</code> is
	 * editable. Otherwise, <code>setValueAt</code> on the cell will not change
	 * the value of that cell.
	 * 
	 * @param row
	 *            row index of cell
	 * @param column
	 *            column index of cell
	 * @return true if the cell is editable
	 */
	public boolean isCellEditable(int row, int column);

	/**
	 * Sets the value for the cell at <code>row</code> and <code>column</code>
	 * to <code>value</code>
	 * 
	 * @param value
	 *            new cell value
	 * @param row
	 *            row index of cell
	 * @param column
	 *            column index of cell
	 */
	public void setValueAt(Object value, int row, int column);

	/**
	 * Returns the number of rows in the model.
	 * 
	 * @return the number of rows in the model
	 */
	public int getRowCount();

	/**
	 * Return the number of columns in the model.
	 * 
	 * @return the number of columns in the model
	 */
	public int getColumnCount();
}
