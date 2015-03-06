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

import java.awt.Point;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Default implementation of <code>SpanModel</code>. The implementation uses
 * <code>HashSet</code> to store the cell spans. Therefore
 * <code>getSpanOver(int, int)</code> has O(N) performance where N is the number
 * of spans. <code>isCellSpan(int, int)</code> has O(1) performance and is used
 * by <code>JGrid</code> to avoid using <code>getSpanOver(int, int)</code> when
 * possible.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class DefaultSpanModel extends AbstractSpanModel implements
		ResizableGrid, Serializable {

	private static final long serialVersionUID = 2079949800194814198L;

	// used to specially paint spanned cells by BasicGridUI
	private Set<CellSpan> spanSet = new HashSet<CellSpan>();

	// set of cells that are part of (non-atomic) spans
	private Set<Point> spanCellSet = new HashSet<Point>();

	private Point cell = new Point();

	public DefaultSpanModel() {
	}

	public CellSpan getSpanOver(int row, int column) {
		Iterator<CellSpan> i = getSpanIterator();
		while (i.hasNext()) {
			CellSpan span = (CellSpan) i.next();
			if (row >= span.getFirstRow() && row <= span.getLastRow()
					&& column >= span.getFirstColumn()
					&& column <= span.getLastColumn()) {
				return span;
			}
		}
		// Return atomic span
		return new CellSpan(row, column, 1, 1);
	}

	public boolean isCellSpan(int row, int column) {
		cell.x = column;
		cell.y = row;
		return spanCellSet.contains(cell);
	}

	public Iterator<CellSpan> getSpanIterator() {
		return spanSet.iterator();
	}

	public void addSpan(CellSpan span) {
		for (int row = span.getFirstRow(); row <= span.getLastRow(); row++) {
			for (int column = span.getFirstColumn(); column <= span
					.getLastColumn(); column++) {
				spanCellSet.add(new Point(column, row));
			}
		}
		spanSet.add(span);
		fireCellSpanAdded(span);
	}

	public void removeSpan(CellSpan span) {
		for (int row = span.getFirstRow(); row <= span.getLastRow(); row++) {
			for (int column = span.getFirstColumn(); column <= span
					.getLastColumn(); column++) {
				spanCellSet.remove(new Point(column, row));
			}
		}
		spanSet.remove(span);
		fireCellSpanRemoved(span);
	}

	private void rebuildSpanCellSet() {
		spanCellSet.clear();
		Iterator<CellSpan> j = spanSet.iterator();
		while (j.hasNext()) {
			CellSpan span = (CellSpan) j.next();
			for (int rowIndex = span.getFirstRow(); rowIndex <= span
					.getLastRow(); rowIndex++) {
				for (int colIndex = span.getFirstColumn(); colIndex <= span
						.getLastColumn(); colIndex++) {
					spanCellSet.add(new Point(colIndex, rowIndex));
				}
			}
		}
	}

	public void insertRows(int row, int rowCount) {
		Set<CellSpan> oldSpanSet = spanSet;
		spanSet = new HashSet<CellSpan>();
		Iterator<CellSpan> i = oldSpanSet.iterator();
		while (i.hasNext()) {
			CellSpan span = (CellSpan) i.next();
			if (span.getLastRow() < row) {
				// leave span unchanged
				spanSet.add(span);
			} else if (span.getFirstRow() >= row) {
				// move span down
				CellSpan newSpan = new CellSpan(span.getRow() + rowCount, span
						.getColumn(), span.getRowCount(), span.getColumnCount());
				spanSet.add(newSpan);
			} else {
				// increase span
				CellSpan newSpan = new CellSpan(span.getRow(),
						span.getColumn(), span.getRowCount() + rowCount, span
								.getColumnCount());
				spanSet.add(newSpan);
			}
		}

		rebuildSpanCellSet();
	}

	public void removeRows(int row, int rowCount) {
		Set<CellSpan> oldSpanSet = spanSet;
		spanSet = new HashSet<CellSpan>();
		Iterator<CellSpan> i = oldSpanSet.iterator();
		while (i.hasNext()) {
			CellSpan span = (CellSpan) i.next();
			if (span.getLastRow() < row) {
				// leave span unchanged
				spanSet.add(span);
			} else if (span.getFirstRow() >= row) {
				// move span up
				CellSpan newSpan = new CellSpan(span.getRow() - rowCount, span
						.getColumn(), span.getRowCount(), span.getColumnCount());
				spanSet.add(newSpan);
			} else {
				// decrease span
				CellSpan newSpan = new CellSpan(span.getRow(),
						span.getColumn(), span.getRowCount() - rowCount, span
								.getColumnCount());
				if (newSpan.getRowCount() > 1) {
					spanSet.add(newSpan);
				}
			}
		}
		rebuildSpanCellSet();
	}

	public void insertColumns(int column, int columnCount) {
		Set<CellSpan> oldSpanSet = spanSet;
		spanSet = new HashSet<CellSpan>();
		Iterator<CellSpan> i = oldSpanSet.iterator();
		while (i.hasNext()) {
			CellSpan span = (CellSpan) i.next();
			if (span.getLastRow() < column) {
				// leave span unchanged
				spanSet.add(span);
			} else if (span.getFirstRow() >= column) {
				// move span right
				CellSpan newSpan = new CellSpan(span.getRow(), span.getColumn()
						+ columnCount, span.getRowCount(), span
						.getColumnCount());
				spanSet.add(newSpan);
			} else {
				// increase span
				CellSpan newSpan = new CellSpan(span.getRow(),
						span.getColumn(), span.getRowCount(), span
								.getColumnCount()
								+ columnCount);
				spanSet.add(newSpan);
			}
		}
		rebuildSpanCellSet();
	}

	public void removeColumns(int column, int columnCount) {
		Set<CellSpan> oldSpanSet = spanSet;
		spanSet = new HashSet<CellSpan>();
		Iterator<CellSpan> i = oldSpanSet.iterator();
		while (i.hasNext()) {
			CellSpan span = (CellSpan) i.next();
			if (span.getLastRow() < column) {
				// leave span unchanged
				spanSet.add(span);
			} else if (span.getFirstRow() >= column) {
				// move span left
				CellSpan newSpan = new CellSpan(span.getRow(), span.getColumn()
						- columnCount, span.getRowCount(), span
						.getColumnCount());
				spanSet.add(newSpan);
			} else {
				// decrease span
				CellSpan newSpan = new CellSpan(span.getRow(),
						span.getColumn(), span.getRowCount(), span
								.getColumnCount()
								- columnCount);
				if (newSpan.getColumnCount() > 1) {
					spanSet.add(newSpan);
				}
			}
		}
		rebuildSpanCellSet();
	}
}
