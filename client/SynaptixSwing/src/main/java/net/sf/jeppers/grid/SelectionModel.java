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
 * <code>SelectionModel</code> stores selection information for a grid of cells.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public interface SelectionModel {

	public enum SelectionMode {
		Single, Multiple
	};

	/**
	 * Set la selection mode
	 * 
	 * @param selectionMode
	 */
	public void setSelectionMode(SelectionMode selectionMode);

	/**
	 * Retourne le mode de selection, soit Single ou Multiple
	 * 
	 * @return
	 */
	public SelectionMode getSelectionMode();

	/**
	 * Add listener to model
	 */
	public void addSelectionModelListener(SelectionModelListener listener);

	/**
	 * Remove listener from model
	 */
	public void removeSelectionModelListener(SelectionModelListener listener);

	/**
	 * Returns true if the specified cell is selected
	 */
	public boolean isSelected(int row, int column);

	/**
	 * Set the selection anchor
	 */
	public void setAnchor(int row, int column);

	/**
	 * Set the selection lead
	 */
	public void setLead(int row, int column);

	/**
	 * Clear selection
	 */
	public void clearSelection();

	/**
	 * Returns the top coordinate of the anchor
	 */
	public int getAnchorRow();

	/**
	 * Returns the leftmost coordinate of the anchor
	 */
	public int getAnchorColumn();

	/**
	 * Returns the bottom coordinate of the lead
	 */
	public int getLeadRow();

	/**
	 * Returns the rightmost coordinate of the lead
	 */
	public int getLeadColumn();

	/**
	 * Retrieves the index of the first column where at least one cell is
	 * selected
	 */
	public int getFirstSelectedColumn();

	/**
	 * Retrieves the index of the last column where at least one cell is
	 * selected
	 */
	public int getLastSelectedColumn();

	/**
	 * Retrieves the index of the first row where at least one cell is selected
	 */
	public int getFirstSelectedRow();

	/**
	 * Retrieves the index of the last row where at least one cell is selected
	 */
	public int getLastSelectedRow();

	/**
	 * Returns true if the value is undergoing a series of changes.
	 */
	public boolean getValueIsAdjusting();

	/**
	 * This property is true if upcoming changes to the value of the model
	 * should be considered a single event.
	 */
	public void setValueIsAdjusting(boolean isAdjusting);

	/**
	 * Set the selection to the specified range
	 */
	public void setSelectionRange(int topRow, int leftColumn, int bottomRow,
			int rightColumn);
}
