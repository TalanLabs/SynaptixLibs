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
 * <code>StyleModel</code> handles editors, renderers, and cell styles for a
 * grid of cells.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public interface StyleModel {
	
	public void addStyleModelListener(StyleModelListener listener);

	public void removeStyleModelListener(StyleModelListener listener);

	/**
	 * Returns an appropriate editor for the cell specified by this row and
	 * column.
	 */
	public GridCellEditor getEditor(Class<?> aClass, int row, int column,
			JGrid grid);

	/**
	 * Returns an appropriate renderer for the cell specified by this row and
	 * column.
	 */
	public GridCellRenderer getRenderer(Class<?> aClass, int row, int column,
			JGrid grid);

	/**
	 * Returns the style for the cell at row, column.
	 */
	public CellStyle getCellStyle(int row, int column);

	/**
	 * Notification from the <code>UIManager</code> that the L&F has changed.
	 */
	public void updateUI();
}
