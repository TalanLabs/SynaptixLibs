package com.synaptix.swing.plaf.basic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.ComponentUI;

import sun.swing.DefaultLookup;

import com.synaptix.swing.plaf.SyListUI;

public class BasicSyListUI extends SyListUI {

	private final static int heightChanged = 1 << 8;

	private final static int widthChanged = 1 << 9;

	private static final int DROP_LINE_THICKNESS = 10;

	/**
	 * Height of the list. When asked to paint, if the current size of the list
	 * differs, this will update the layout state.
	 */
	private int listHeight;

	/**
	 * Width of the list. When asked to paint, if the current size of the list
	 * differs, this will update the layout state.
	 */
	private int listWidth;

	// Following ivars are used if the list is laying out horizontally

	/**
	 * Number of columns to create.
	 */
	private int columnCount;

	/**
	 * Number of rows per column. This is only used if the row height is fixed.
	 */
	private int rowsPerColumn;

	public static ComponentUI createUI(JComponent c) {
		return new BasicSyListUI();
	}

	public void installUI(JComponent c) {
		super.installUI(c);

		columnCount = 1;
	}

	private void redrawList() {
		list.revalidate();
		list.repaint();
	}

	public void paint(Graphics g, JComponent c) {
		Shape clip = g.getClip();
		paintImpl(g, c);
		g.setClip(clip);

		paintDropLine(g);
	}

	private void paintImpl(Graphics g, JComponent c) {
		switch (list.getLayoutOrientation()) {
		case JList.VERTICAL_WRAP:
			if (list.getHeight() != listHeight) {
				updateLayoutStateNeeded |= heightChanged;
				redrawList();
			}
			break;
		case JList.HORIZONTAL_WRAP:
			if (list.getWidth() != listWidth) {
				updateLayoutStateNeeded |= widthChanged;
				redrawList();
			}
			break;
		default:
			break;
		}
		maybeUpdateLayoutState();

		ListCellRenderer renderer = list.getCellRenderer();
		ListModel dataModel = list.getModel();
		ListSelectionModel selModel = list.getSelectionModel();
		int size;

		if ((renderer == null) || (size = dataModel.getSize()) == 0) {
			return;
		}

		// Determine how many columns we need to paint
		Rectangle paintBounds = g.getClipBounds();

		int startColumn, endColumn;
		if (c.getComponentOrientation().isLeftToRight()) {
			startColumn = convertLocationToColumn(paintBounds.x, paintBounds.y);
			endColumn = convertLocationToColumn(paintBounds.x
					+ paintBounds.width, paintBounds.y);
		} else {
			startColumn = convertLocationToColumn(paintBounds.x
					+ paintBounds.width, paintBounds.y);
			endColumn = convertLocationToColumn(paintBounds.x, paintBounds.y);
		}
		int maxY = paintBounds.y + paintBounds.height;
		int leadIndex = adjustIndex(list.getLeadSelectionIndex(), list);
		int rowIncrement = (list.getLayoutOrientation() == JList.HORIZONTAL_WRAP) ? columnCount
				: 1;

		for (int colCounter = startColumn; colCounter <= endColumn; colCounter++) {
			// And then how many rows in this columnn
			int row = convertLocationToRowInColumn(paintBounds.y, colCounter);
			int rowCount = getRowCount(colCounter);
			int index = getModelIndex(colCounter, row);
			Rectangle rowBounds = getCellBounds(list, index, index);

			if (rowBounds == null) {
				// Not valid, bail!
				return;
			}
			while (row < rowCount && rowBounds.y < maxY && index < size) {
				rowBounds.height = getHeight(colCounter, row);
				g.setClip(rowBounds.x, rowBounds.y, rowBounds.width,
						rowBounds.height);
				g.clipRect(paintBounds.x, paintBounds.y, paintBounds.width,
						paintBounds.height);
				paintCell(g, index, rowBounds, renderer, dataModel, selModel,
						leadIndex);
				rowBounds.y += rowBounds.height;
				index += rowIncrement;
				row++;
			}
		}
		// Empty out the renderer pane, allowing renderers to be gc'ed.
		rendererPane.removeAll();
	}

	private void paintDropLine(Graphics g) {
		JList.DropLocation loc = list.getDropLocation();
		if (loc == null || !loc.isInsert()) {
			return;
		}

		Color c = DefaultLookup
				.getColor(list, this, "List.dropLineColor", null);
		if (c != null) {
			g.setColor(c);
			Rectangle rect = getDropLineRect(loc);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
		}
	}

	private Rectangle getDropLineRect(JList.DropLocation loc) {
		int size = list.getModel().getSize();

		if (size == 0) {
			Insets insets = list.getInsets();
			if (list.getLayoutOrientation() == JList.HORIZONTAL_WRAP) {
				if (list.getComponentOrientation().isLeftToRight()) {
					return new Rectangle(insets.left, insets.top,
							DROP_LINE_THICKNESS, 20);
				} else {
					return new Rectangle(list.getWidth() - DROP_LINE_THICKNESS
							- insets.right, insets.top, DROP_LINE_THICKNESS, 20);
				}
			} else {
				return new Rectangle(insets.left, insets.top, list.getWidth()
						- insets.left - insets.right, DROP_LINE_THICKNESS);
			}
		}

		Rectangle rect = null;
		int index = loc.getIndex();
		boolean decr = false;

		if (list.getLayoutOrientation() == JList.HORIZONTAL_WRAP) {
			if (index == size) {
				decr = true;
			} else if (index != 0
					&& convertModelToRow(index) != convertModelToRow(index - 1)) {

				Rectangle prev = getCellBounds(list, index - 1);
				Rectangle me = getCellBounds(list, index);
				Point p = loc.getDropPoint();

				if (list.getComponentOrientation().isLeftToRight()) {
					decr = Point2D.distance(prev.x + prev.width, prev.y
							+ (int) (prev.height / 2.0), p.x, p.y) < Point2D
							.distance(me.x, me.y + (int) (me.height / 2.0),
									p.x, p.y);
				} else {
					decr = Point2D.distance(prev.x, prev.y
							+ (int) (prev.height / 2.0), p.x, p.y) < Point2D
							.distance(me.x + me.width, me.y
									+ (int) (prev.height / 2.0), p.x, p.y);
				}
			}

			if (decr) {
				index--;
				rect = getCellBounds(list, index);
				if (list.getComponentOrientation().isLeftToRight()) {
					rect.x += rect.width;
				} else {
					rect.x -= DROP_LINE_THICKNESS;
				}
			} else {
				rect = getCellBounds(list, index);
				if (!list.getComponentOrientation().isLeftToRight()) {
					rect.x += rect.width - DROP_LINE_THICKNESS;
				}
			}

			if (rect.x >= list.getWidth()) {
				rect.x = list.getWidth() - DROP_LINE_THICKNESS;
			} else if (rect.x < 0) {
				rect.x = 0;
			}

			rect.width = DROP_LINE_THICKNESS;
		} else if (list.getLayoutOrientation() == JList.VERTICAL_WRAP) {
			if (index == size) {
				index--;
				rect = getCellBounds(list, index);
				rect.y += rect.height;
			} else if (index != 0
					&& convertModelToColumn(index) != convertModelToColumn(index - 1)) {

				Rectangle prev = getCellBounds(list, index - 1);
				Rectangle me = getCellBounds(list, index);
				Point p = loc.getDropPoint();
				if (Point2D.distance(prev.x + (int) (prev.width / 2.0), prev.y
						+ prev.height, p.x, p.y) < Point2D.distance(me.x
						+ (int) (me.width / 2.0), me.y, p.x, p.y)) {

					index--;
					rect = getCellBounds(list, index);
					rect.y += rect.height;
				} else {
					rect = getCellBounds(list, index);
				}
			} else {
				rect = getCellBounds(list, index);
			}

			if (rect.y >= list.getHeight()) {
				rect.y = list.getHeight() - DROP_LINE_THICKNESS;
			}

			rect.height = DROP_LINE_THICKNESS;
		} else {
			if (index == size) {
				index--;
				rect = getCellBounds(list, index);
				rect.y += rect.height;
			} else {
				rect = getCellBounds(list, index);
			}

			if (rect.y >= list.getHeight()) {
				rect.y = list.getHeight() - DROP_LINE_THICKNESS;
			}

			rect.height = DROP_LINE_THICKNESS;
		}

		return rect;
	}

	/**
	 * Gets the bounds of the specified model index, returning the resulting
	 * bounds, or null if <code>index</code> is not valid.
	 */
	private Rectangle getCellBounds(JList list, int index) {
		maybeUpdateLayoutState();

		int row = convertModelToRow(index);
		int column = convertModelToColumn(index);

		if (row == -1 || column == -1) {
			return null;
		}

		Insets insets = list.getInsets();
		int x;
		int w = cellWidth;
		int y = insets.top;
		int h;
		switch (list.getLayoutOrientation()) {
		case JList.VERTICAL_WRAP:
		case JList.HORIZONTAL_WRAP:
			if (list.getComponentOrientation().isLeftToRight()) {
				x = insets.left + column * cellWidth;
			} else {
				x = list.getWidth() - insets.right - (column + 1) * cellWidth;
			}
			y += cellHeight * row;
			h = cellHeight;
			break;
		default:
			x = insets.left;
			if (cellHeights == null) {
				y += (cellHeight * row);
			} else if (row >= cellHeights.length) {
				y = 0;
			} else {
				for (int i = 0; i < row; i++) {
					y += cellHeights[i];
				}
			}
			w = list.getWidth() - (insets.left + insets.right);
			h = getRowHeight(index);
			break;
		}
		return new Rectangle(x, y, w, h);
	}

	/**
	 * Returns the row that the model index <code>index</code> will be displayed
	 * in..
	 */
	private int convertModelToRow(int index) {
		int size = list.getModel().getSize();

		if ((index < 0) || (index >= size)) {
			return -1;
		}

		if (list.getLayoutOrientation() != JList.VERTICAL && columnCount > 1
				&& rowsPerColumn > 0) {
			if (list.getLayoutOrientation() == JList.VERTICAL_WRAP) {
				return index % rowsPerColumn;
			}
			return index / columnCount;
		}
		return index;
	}

	/**
	 * Returns the column that the model index <code>index</code> will be
	 * displayed in.
	 */
	private int convertModelToColumn(int index) {
		int size = list.getModel().getSize();

		if ((index < 0) || (index >= size)) {
			return -1;
		}

		if (list.getLayoutOrientation() != JList.VERTICAL && rowsPerColumn > 0
				&& columnCount > 1) {
			if (list.getLayoutOrientation() == JList.VERTICAL_WRAP) {
				return index / rowsPerColumn;
			}
			return index % columnCount;
		}
		return 0;
	}

	private int convertLocationToColumn(int x, int y) {
		if (cellWidth > 0) {
			if (list.getLayoutOrientation() == JList.VERTICAL) {
				return 0;
			}
			Insets insets = list.getInsets();
			int col;
			if (list.getComponentOrientation().isLeftToRight()) {
				col = (x - insets.left) / cellWidth;
			} else {
				col = (list.getWidth() - x - insets.right - 1) / cellWidth;
			}
			if (col < 0) {
				return 0;
			} else if (col >= columnCount) {
				return columnCount - 1;
			}
			return col;
		}
		return 0;
	}

	/**
	 * Returns the closest row that starts at the specified y-location in the
	 * passed in column.
	 */
	private int convertLocationToRowInColumn(int y, int column) {
		int x = 0;

		if (list.getLayoutOrientation() != JList.VERTICAL) {
			if (list.getComponentOrientation().isLeftToRight()) {
				x = column * cellWidth;
			} else {
				x = list.getWidth() - (column + 1) * cellWidth
						- list.getInsets().right;
			}
		}
		return convertLocationToRow(x, y, true);
	}

	/**
	 * Returns the row at location x/y.
	 * 
	 * @param closest
	 *            If true and the location doesn't exactly match a particular
	 *            location, this will return the closest row.
	 */
	private int convertLocationToRow(int x, int y0, boolean closest) {
		int size = list.getModel().getSize();

		if (size <= 0) {
			return -1;
		}
		Insets insets = list.getInsets();
		if (cellHeights == null) {
			int row = (cellHeight == 0) ? 0 : ((y0 - insets.top) / cellHeight);
			if (closest) {
				if (row < 0) {
					row = 0;
				} else if (row >= size) {
					row = size - 1;
				}
			}
			return row;
		} else if (size > cellHeights.length) {
			return -1;
		} else {
			int y = insets.top;
			int row = 0;

			if (closest && y0 < y) {
				return 0;
			}
			int i;
			for (i = 0; i < size; i++) {
				if ((y0 >= y) && (y0 < y + cellHeights[i])) {
					return row;
				}
				y += cellHeights[i];
				row += 1;
			}
			return i - 1;
		}
	}

	/**
	 * Returns the number of rows in the given column.
	 */
	private int getRowCount(int column) {
		if (column < 0 || column >= columnCount) {
			return -1;
		}
		if (list.getLayoutOrientation() == JList.VERTICAL
				|| (column == 0 && columnCount == 1)) {
			return list.getModel().getSize();
		}
		if (column >= columnCount) {
			return -1;
		}
		if (list.getLayoutOrientation() == JList.VERTICAL_WRAP) {
			if (column < (columnCount - 1)) {
				return rowsPerColumn;
			}
			return list.getModel().getSize() - (columnCount - 1)
					* rowsPerColumn;
		}
		// JList.HORIZONTAL_WRAP
		int diff = columnCount
				- (columnCount * rowsPerColumn - list.getModel().getSize());

		if (column >= diff) {
			return Math.max(0, rowsPerColumn - 1);
		}
		return rowsPerColumn;
	}

	private static int adjustIndex(int index, JList list) {
		return index < list.getModel().getSize() ? index : -1;
	}

	/**
	 * Returns the model index for the specified display location. If
	 * <code>column</code>x<code>row</code> is beyond the length of the model,
	 * this will return the model size - 1.
	 */
	private int getModelIndex(int column, int row) {
		switch (list.getLayoutOrientation()) {
		case JList.VERTICAL_WRAP:
			return Math.min(list.getModel().getSize() - 1, rowsPerColumn
					* column + Math.min(row, rowsPerColumn - 1));
		case JList.HORIZONTAL_WRAP:
			return Math.min(list.getModel().getSize() - 1, row * columnCount
					+ column);
		default:
			return row;
		}
	}

	/**
	 * Returns the height of the cell at the passed in location.
	 */
	private int getHeight(int column, int row) {
		if (column < 0 || column > columnCount || row < 0) {
			return -1;
		}
		if (list.getLayoutOrientation() != JList.VERTICAL) {
			return cellHeight;
		}
		if (row >= list.getModel().getSize()) {
			return -1;
		}
		return (cellHeights == null) ? cellHeight
				: ((row < cellHeights.length) ? cellHeights[row] : -1);
	}

	protected void updateLayoutState() {
		super.updateLayoutState();

		if (list.getLayoutOrientation() != JList.VERTICAL) {
			listHeight = list.getHeight();
			listWidth = list.getWidth();
		}
	}

}
