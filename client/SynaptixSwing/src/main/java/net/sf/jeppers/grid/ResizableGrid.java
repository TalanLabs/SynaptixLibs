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
 * This interface defines the methods to implement for models (<code>StyleModel</code>, 
 * <code>RulerModel</code>, <code>SpanModel</code>, <code>SelectionModel</code>) 
 * that need to sync dimensions with <code>GridModel</code>.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public interface ResizableGrid {
	/**
	 * Insert <code>rowCount</code> rows at <code>row</code>.
	 * 
	 * @param row			the row to insert at
	 * @param rowCount	the number of rows to insert
	 */
	public void insertRows(int row, int rowCount);
	
	/**
	 * Remove <code>rowCount</code> rows at <code>row</code>.
	 * 
	 * @param row			the row to remove from
	 * @param rowCount	the number of rows to remove
	 */
	public void removeRows(int row, int rowCount);
	
	/**
	 * Insert <code>columnCount</code> columns at <code>column</code>.
	 * 
	 * @param column				the column to insert at
	 * @param columnCount	the number of columns to insert
	 */
	public void insertColumns(int column, int columnCount);
	
	/**
	 * Remove <code>columnCount</code> columns at <code>column</code>.
	 * 
	 * @param column				the column to remove from
	 * @param columnCount	the number of columns to remove
	 */
	public void removeColumns(int column, int columnCount);
}
