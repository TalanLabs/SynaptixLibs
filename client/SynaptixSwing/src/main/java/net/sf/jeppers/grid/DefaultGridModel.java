/* 
 * Copyright (c) 2005, Cameron Zemek
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
import java.util.Arrays;

/**
 * This is an implementation of <code>GridModel</code> that is backed by arrays.
 * It is suitable for dense data. Use <code>SparseGridModel</code> if you have
 * sparse amounts of data.
 * 
 * @author <a href="mailto:grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class DefaultGridModel extends AbstractGridModel implements
		ResizableGrid, Serializable {

	private static final long serialVersionUID = -3963819001137194784L;

	private Object[][] data;

	private Class<?>[][] clazz;

	private boolean[][] lock;

	private int rowSize;

	private int colSize;

	private boolean defaultLock;

	public DefaultGridModel(int rows, int columns) {
		this(rows, columns, false);
	}

	public DefaultGridModel(int rows, int columns, boolean defaultLock) {
		data = new Object[rows][columns];
		lock = new boolean[rows][columns];
		clazz = new Class<?>[rows][columns];
		rowSize = rows;
		colSize = columns;

		this.defaultLock = defaultLock;

		for (int i = 0; i < rows; i++) {
			Arrays.fill(lock[i], defaultLock);
		}
	}

	public int getRowCount() {
		return rowSize;
	}

	public int getColumnCount() {
		return colSize;
	}

	public Class<?> getClassAt(int row, int column) {
		return clazz[row][column];
	}

	public Object getValueAt(int row, int column) {
		return data[row][column];
	}

	public boolean isCellEditable(int row, int column) {
		return !lock[row][column];
	}

	public void setValueAt(Object value, int row, int column) {
		data[row][column] = value;
		fireGridCellUpdated(row, column);
	}

	public void setCellLock(boolean cellLock, int row, int column) {
		lock[row][column] = cellLock;
	}

	public void setClassAt(Class<?> cl, int row, int column) {
		clazz[row][column] = cl;
	}

	public void insertRows(int row, int rowCount) {
		Object newData[][] = new Object[rowSize + rowCount][colSize];
		System.arraycopy(data, 0, newData, 0, row);
		System.arraycopy(data, row, newData, row + rowCount, rowSize - row);

		boolean newLock[][] = new boolean[rowSize + rowCount][colSize];
		System.arraycopy(lock, 0, newLock, 0, row);
		System.arraycopy(lock, row, newLock, row + rowCount, rowSize - row);

		for (int i = row; i < row + rowCount; i++) {
			Arrays.fill(newLock[i], defaultLock);
		}

		Class<?> newClazz[][] = new Class<?>[rowSize + rowCount][colSize];
		System.arraycopy(clazz, 0, newClazz, 0, row);
		System.arraycopy(clazz, row, newClazz, row + rowCount, rowSize - row);

		// Update grid model
		rowSize += rowCount;
		data = newData;
		lock = newLock;
		clazz = newClazz;

		fireGridRowsInserted(row, (row + rowCount - 1));
	}

	public void removeRows(int row, int rowCount) {
		Object newData[][] = new Object[rowSize - rowCount][colSize];
		System.arraycopy(data, 0, newData, 0, row);
		System.arraycopy(data, row + rowCount, newData, row, rowSize - rowCount
				- row);

		boolean newLock[][] = new boolean[rowSize - rowCount][colSize];
		System.arraycopy(lock, 0, newLock, 0, row);
		System.arraycopy(lock, row + rowCount, newLock, row, rowSize - rowCount
				- row);

		Class<?> newClazz[][] = new Class<?>[rowSize - rowCount][colSize];
		System.arraycopy(clazz, 0, newClazz, 0, row);
		System.arraycopy(clazz, row + rowCount, newClazz, row, rowSize
				- rowCount - row);

		// Update grid model
		rowSize -= rowCount;
		data = newData;
		lock = newLock;
		clazz = newClazz;

		fireGridRowsDeleted(row, (row + rowCount - 1));
	}

	public void insertColumns(int column, int columnCount) {
		Object newData[][] = new Object[rowSize][colSize + columnCount];
		boolean newLock[][] = new boolean[rowSize][colSize + columnCount];
		Class<?> newClazz[][] = new Class<?>[rowSize][colSize + columnCount];

		// copy contents of each row into newData
		for (int row = 0; row < rowSize; row++) {
			System.arraycopy(data[row], 0, newData[row], 0, column);
			System.arraycopy(data[row], column, newData[row], column
					+ columnCount, colSize - column);

			System.arraycopy(lock[row], 0, newLock[row], 0, column);
			System.arraycopy(lock[row], column, newLock[row], column
					+ columnCount, colSize - column);

			for (int j = column; j < column + columnCount; j++) {
				newLock[row][j] = defaultLock;
			}

			System.arraycopy(clazz[row], 0, newClazz[row], 0, column);
			System.arraycopy(clazz[row], column, newClazz[row], column
					+ columnCount, colSize - column);
		}

		// Update grid model
		colSize += columnCount;
		data = newData;
		lock = newLock;
		clazz = newClazz;

		fireGridColumnsInserted(column, (column + columnCount - 1));
	}

	public void removeColumns(int column, int columnCount) {
		Object newData[][] = new Object[rowSize][colSize - columnCount];
		boolean newLock[][] = new boolean[rowSize][colSize - columnCount];
		Class<?> newClazz[][] = new Class<?>[rowSize][colSize - columnCount];

		// copy contents of each row into newData
		for (int row = 0; row < rowSize; row++) {
			System.arraycopy(data[row], 0, newData[row], 0, column);
			System.arraycopy(data[row], column + columnCount, newData[row],
					column, colSize - columnCount - column);

			System.arraycopy(lock[row], 0, newLock[row], 0, column);
			System.arraycopy(lock[row], column + columnCount, newLock[row],
					column, colSize - columnCount - column);

			System.arraycopy(clazz[row], 0, newClazz[row], 0, column);
			System.arraycopy(clazz[row], column + columnCount, newClazz[row],
					column, colSize - columnCount - column);
		}

		// Update grid model
		colSize -= columnCount;
		data = newData;
		lock = newLock;
		clazz = newClazz;

		fireGridColumnsDeleted(column, (column + columnCount - 1));
	}
}
