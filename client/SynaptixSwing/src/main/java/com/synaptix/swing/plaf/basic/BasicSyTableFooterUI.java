package com.synaptix.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Enumeration;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.synaptix.swing.JSyTable;
import com.synaptix.swing.plaf.SyTableFooterUI;
import com.synaptix.swing.table.JSyTableFooter;
import com.synaptix.swing.table.SyTableColumn;

public class BasicSyTableFooterUI extends SyTableFooterUI {

	protected JSyTableFooter footer;

	protected CellRendererPane rendererPane;

	public static ComponentUI createUI(JComponent h) {
		return new BasicSyTableFooterUI();
	}

	public void installUI(JComponent c) {
		footer = (JSyTableFooter) c;

		rendererPane = new CellRendererPane();
		footer.add(rendererPane);

		installDefaults();
		installListeners();
		installKeyboardActions();
	}

	protected void installDefaults() {
		LookAndFeel.installColorsAndFont(footer, "TableHeader.background", //$NON-NLS-1$
				"TableHeader.foreground", "TableHeader.font"); //$NON-NLS-1$ //$NON-NLS-2$
		LookAndFeel.installProperty(footer, "opaque", Boolean.TRUE); //$NON-NLS-1$
	}

	protected void installListeners() {
	}

	protected void installKeyboardActions() {
	}

	public void uninstallUI(JComponent c) {
		uninstallDefaults();
		uninstallListeners();
		uninstallKeyboardActions();

		footer.remove(rendererPane);
		rendererPane = null;
		footer = null;
	}

	protected void uninstallDefaults() {
	}

	protected void uninstallListeners() {
	}

	protected void uninstallKeyboardActions() {
	}

	public void paint(Graphics g, JComponent c) {
		if (footer.getTable().getColumnCount() <= 0) {
			return;
		}
		boolean ltr = footer.getComponentOrientation().isLeftToRight();

		Rectangle clip = g.getClipBounds();
		Point left = clip.getLocation();
		Point right = new Point(clip.x + clip.width - 1, clip.y);
		TableColumnModel cm = footer.getTable().getColumnModel();
		int cMin = footer.getTable().columnAtPoint(ltr ? left : right);
		int cMax = footer.getTable().columnAtPoint(ltr ? right : left);
		// This should never happen.
		if (cMin == -1) {
			cMin = 0;
		}
		// If the table does not have enough columns to fill the view we'll get
		// -1.
		// Replace this with the index of the last column.
		if (cMax == -1) {
			cMax = cm.getColumnCount() - 1;
		}

		paintGrid(g, cMin, cMax);
		paintCells(g, cMin, cMax);
		/*
		 * JTableHeader header = footer.getTable().getTableHeader(); TableColumn
		 * draggedColumn = (header == null) ? null : header .getDraggedColumn();
		 * 
		 * int columnWidth; Rectangle cellRect = footer.getHeaderRect(ltr ? cMin :
		 * cMax); TableColumn aColumn; if (ltr) { for (int column = cMin; column <=
		 * cMax; column++) { aColumn = cm.getColumn(column); columnWidth =
		 * aColumn.getWidth(); cellRect.width = columnWidth; if (aColumn !=
		 * draggedColumn) { paintCell(g, cellRect, column); } cellRect.x +=
		 * columnWidth; } } else { for (int column = cMax; column >= cMin;
		 * column--) { aColumn = cm.getColumn(column); columnWidth =
		 * aColumn.getWidth(); cellRect.width = columnWidth; if (aColumn !=
		 * draggedColumn) { paintCell(g, cellRect, column); } cellRect.x +=
		 * columnWidth; } } if (draggedColumn != null) { }
		 */

		// Remove all components in the rendererPane.
		rendererPane.removeAll();
	}

	private void paintCells(Graphics g, int cMin, int cMax) {
		JTableHeader header = footer.getTable().getTableHeader();
		TableColumn draggedColumn = (header == null) ? null : header
				.getDraggedColumn();

		TableColumnModel cm = footer.getTable().getColumnModel();
		int columnMargin = cm.getColumnMargin();

		Rectangle cellRect;
		TableColumn aColumn;
		int columnWidth;
		if (footer.getTable().getComponentOrientation().isLeftToRight()) {
			cellRect = footer.getTable().getCellRect(0, cMin, false);
			int i = 0;
			for (int column = cMin; column <= cMax; column++) {
				aColumn = cm.getColumn(column);
				columnWidth = aColumn.getWidth();
				cellRect.width = columnWidth - columnMargin;
				cellRect.y = 0;
				cellRect.height = footer.getTable().getRowHeight();

				SyTableColumn ytc = (SyTableColumn) aColumn;
				if (aColumn != draggedColumn && ytc.isExistSumValue()) {
					paintCell(g, cellRect, column);
				}
				cellRect.x += columnWidth;
				i++;
			}
		} else {
			cellRect = footer.getTable().getCellRect(0, cMin, false);
			aColumn = cm.getColumn(cMin);
			int i = 0;
			if (aColumn != draggedColumn) {
				columnWidth = aColumn.getWidth();
				cellRect.width = columnWidth - columnMargin;
				cellRect.y = 0;
				cellRect.height = footer.getTable().getRowHeight();

				SyTableColumn ytc = (SyTableColumn) aColumn;
				if (ytc.isExistSumValue()) {
					paintCell(g, cellRect, cMin);
				}
				i++;
			}
			for (int column = cMin + 1; column <= cMax; column++) {
				aColumn = cm.getColumn(column);
				columnWidth = aColumn.getWidth();
				cellRect.width = columnWidth - columnMargin;
				cellRect.x -= columnWidth;

				cellRect.y = 0;
				cellRect.height = footer.getTable().getRowHeight();

				SyTableColumn ytc = (SyTableColumn) aColumn;
				if (aColumn != draggedColumn && ytc.isExistSumValue()) {
					paintCell(g, cellRect, column);
				}
			}
		}

		// Paint the dragged column if we are dragging.
		if (draggedColumn != null) {
			paintDraggedArea(g, draggedColumn, header.getDraggedDistance());
		}

		// Remove any renderers that may be left in the rendererPane.
		rendererPane.removeAll();
	}

	private void paintDraggedArea(Graphics g, TableColumn draggedColumn,
			int distance) {
		int draggedColumnIndex = viewIndexForColumn(draggedColumn);
		Rectangle draggedCellRect = footer.getHeaderRect(draggedColumnIndex);

		// Draw a gray well in place of the moving column.
		g.setColor(footer.getParent().getBackground());
		g.fillRect(draggedCellRect.x, draggedCellRect.y, draggedCellRect.width,
				draggedCellRect.height);

		draggedCellRect.x += distance;

		// Fill the background.
		g.setColor(footer.getBackground());
		g.fillRect(draggedCellRect.x, draggedCellRect.y, draggedCellRect.width,
				draggedCellRect.height);

		g.setColor(footer.getTable().getGridColor());

		if (footer.getTable().getShowVerticalLines()) {
			int x1 = draggedCellRect.x;
			int y1 = draggedCellRect.y;
			int x2 = x1 + draggedCellRect.width - 1;
			int y2 = y1 + draggedCellRect.height - 1;
			// Left
			g.drawLine(x1 - 1, y1, x1 - 1, y2);
			// Right
			g.drawLine(x2, y1, x2, y2);
		}

		draggedCellRect.width -= 1;
		draggedCellRect.y += 1;
		draggedCellRect.height -= 1;

		SyTableColumn ytc = (SyTableColumn) draggedColumn;
		if (ytc.isExistSumValue()) {
			paintCell(g, draggedCellRect, draggedColumnIndex);
		}
	}

	private void paintGrid(Graphics g, int cMin, int cMax) {
		g.setColor(footer.getTable().getGridColor());

		Rectangle minCell = footer.getTable().getCellRect(0, cMin, true);
		Rectangle maxCell = footer.getTable().getCellRect(0, cMax, true);
		Rectangle damagedArea = minCell.union(maxCell);

		if (footer.getTable().getShowVerticalLines()) {
			TableColumnModel cm = footer.getTable().getColumnModel();
			int tableHeight = damagedArea.y + damagedArea.height;
			int x;
			if (footer.getTable().getComponentOrientation().isLeftToRight()) {
				x = damagedArea.x;
				for (int column = cMin; column <= cMax; column++) {
					int w = cm.getColumn(column).getWidth();
					x += w;
					g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
				}
			} else {
				x = damagedArea.x + damagedArea.width;
				for (int column = cMin; column < cMax; column++) {
					int w = cm.getColumn(column).getWidth();
					x -= w;
					g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
				}
				x -= cm.getColumn(cMax).getWidth();
				g.drawLine(x, 0, x, tableHeight - 1);
			}
		}

	}

	private Component getFooterRenderer(int columnIndex) {
		JSyTable table = (JSyTable) footer.getTable();
		TableCellRenderer renderer = table.getCellSumRenderer(columnIndex);
		return table.prepareFooterRenderer(renderer,
				columnIndex);
	}

	private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
		Component component = getFooterRenderer(columnIndex);
		rendererPane.paintComponent(g, component, footer, cellRect.x,
				cellRect.y, cellRect.width, cellRect.height, true);
	}

	private int viewIndexForColumn(TableColumn aColumn) {
		TableColumnModel cm = footer.getTable().getColumnModel();
		for (int column = 0; column < cm.getColumnCount(); column++) {
			if (cm.getColumn(column) == aColumn) {
				return column;
			}
		}
		return -1;
	}

	//
	// Size Methods
	//

	private int getFooterHeight() {
		int height = footer.getTable().getRowHeight();
		boolean accomodatedDefault = false;
		TableColumnModel columnModel = footer.getTable().getColumnModel();
		for (int column = 0; column < columnModel.getColumnCount(); column++) {
			TableColumn aColumn = columnModel.getColumn(column);
			// Configuring the header renderer to calculate its preferred size
			// is expensive.
			// Optimise this by assuming the default renderer always has the
			// same height.
			if (aColumn.getHeaderRenderer() != null || !accomodatedDefault) {
				SyTableColumn ytc = (SyTableColumn) aColumn;
				if (ytc.isExistSumValue()) {
					Component comp = getFooterRenderer(column);
					int rendererHeight = comp.getPreferredSize().height;
					height = Math.max(height, rendererHeight);
					// If the header value is empty (== "") in the
					// first column (and this column is set up
					// to use the default renderer) we will
					// return zero from this routine and the header
					// will disappear altogether. Avoiding the calculation
					// of the preferred size is such a performance win for
					// most applications that we will continue to
					// use this cheaper calculation, handling these
					// issues as `edge cases'.
					if (rendererHeight > 0) {
						accomodatedDefault = true;
					}
				}
			}
		}
		return height;
	}

	private Dimension createHeaderSize(long width) {
		// None of the callers include the intercell spacing, do it here.
		if (width > Integer.MAX_VALUE) {
			width = Integer.MAX_VALUE;
		}
		return new Dimension((int) width, getFooterHeight());
	}

	/**
	 * Return the minimum size of the header. The minimum width is the sum of
	 * the minimum widths of each column (plus inter-cell spacing).
	 */
	public Dimension getMinimumSize(JComponent c) {
		long width = 0;
		Enumeration enumeration = footer.getTable().getColumnModel()
				.getColumns();
		while (enumeration.hasMoreElements()) {
			TableColumn aColumn = (TableColumn) enumeration.nextElement();
			width = width + aColumn.getMinWidth();
		}
		return createHeaderSize(width);
	}

	/**
	 * Return the preferred size of the header. The preferred height is the
	 * maximum of the preferred heights of all of the components provided by the
	 * header renderers. The preferred width is the sum of the preferred widths
	 * of each column (plus inter-cell spacing).
	 */
	public Dimension getPreferredSize(JComponent c) {
		long width = 0;
		Enumeration enumeration = footer.getTable().getColumnModel()
				.getColumns();
		while (enumeration.hasMoreElements()) {
			TableColumn aColumn = (TableColumn) enumeration.nextElement();
			width = width + aColumn.getPreferredWidth();
		}
		return createHeaderSize(width);
	}

	/**
	 * Return the maximum size of the header. The maximum width is the sum of
	 * the maximum widths of each column (plus inter-cell spacing).
	 */
	public Dimension getMaximumSize(JComponent c) {
		long width = 0;
		Enumeration enumeration = footer.getTable().getColumnModel()
				.getColumns();
		while (enumeration.hasMoreElements()) {
			TableColumn aColumn = (TableColumn) enumeration.nextElement();
			width = width + aColumn.getMaxWidth();
		}
		return createHeaderSize(width);
	}

}
