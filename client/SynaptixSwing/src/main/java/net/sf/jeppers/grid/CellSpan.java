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

import java.io.Serializable;

/**
 * This class embodies the idea that cells can be merged. That is a cell can span
 * over cells to the right and to the bottom. The cell which is displayed by the span is 
 * referred to as the anchor cell.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class CellSpan implements Serializable {
	private int anchorRow;
	private int anchorColumn;
	private int rowCount;
	private int columnCount;
	
	/**
	 * Create a <code>CellSpan</code> that represents a span at
	 * <code>anchorRow</code> and <code>anchorColumn</code>
	 * that spans over the following <code>rowCount</code> rows and 
	 * <code>columnCount</code> columns.
	 */
	public CellSpan(int anchorRow, int anchorColumn, int rowCount, int columnCount){
		this.anchorRow = anchorRow;
		this.anchorColumn = anchorColumn;
		this.rowCount = rowCount;
		this.columnCount = columnCount;
	}
	
	/**
	 * Return the anchor row.
	 * 
	 * @return 	row index of anchor cell.
	 */
	public int getRow(){
		return anchorRow;
	}
	
	/**
	 * Return the anchor column.
	 * 
	 * @return	column index of anchor cell.
	 */
	public int getColumn(){
		return anchorColumn;
	}
	
	/**
	 * Return the number of rows that the span covers.
	 * 
	 * @return 	number of rows included in the span.
	 */
	public int getRowCount(){
		return rowCount;
	}
	
	/**
	 * Return the number of columns that the span covers.
	 * 
	 * @return 	number of columns included in the span.
	 */
	public int getColumnCount(){
		return columnCount;
	}
	
	/**
	 * Return the first row that the span covers.
	 * 
	 * @return	the anchor row
	 */
	public int getFirstRow(){
		return anchorRow;
	}
	
	/**
	 * Return the last row that the span covers.
	 * 
	 * @return	the last row of the span.
	 */
	public int getLastRow(){
		return (anchorRow + rowCount - 1);
	}
	
	/**
	 * Return the first column that the span covers.
	 * 
	 * @return	the anchor column
	 */
	public int getFirstColumn(){
		return anchorColumn;
	}
	
	/**
	 * Return the last column that the span covers.
	 * 
	 * @return	the last column of the span.
	 */
	public int getLastColumn(){
		return (anchorColumn + columnCount - 1);
	}
	
	/**
	 * Returns true if the cell at <code>row</code> and <code>column</code>
	 * is part of the span. That is, the specified cell is the anchor cell or is hidden by
	 * the span.
	 */
	public boolean containsCell(int row, int column){
		return (row >= getFirstRow() && row <= getLastRow() &&
					column >= getFirstColumn() && column <= getLastColumn());
	}
    
    /**
     * Returns true if the span is atomic (ie. getRowCount() == 1 && getColumnCount() == 1)
     */
    public boolean isAtomic() {
        return rowCount == 1 && columnCount == 1;
    }
}
