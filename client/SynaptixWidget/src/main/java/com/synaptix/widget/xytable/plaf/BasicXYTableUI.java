package com.synaptix.widget.xytable.plaf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.selection.XYSelectionModel;
import com.synaptix.swing.utils.GraphicsHelper;
import com.synaptix.widget.xytable.JXYTable;
import com.synaptix.widget.xytable.JXYTableColumnFooter;
import com.synaptix.widget.xytable.JXYTableColumnHeader;
import com.synaptix.widget.xytable.JXYTableRowFooter;
import com.synaptix.widget.xytable.JXYTableRowHeader;
import com.synaptix.widget.xytable.XYTableCellRenderer;
import com.synaptix.widget.xytable.XYTableModel;

public class BasicXYTableUI extends XYTableUI {

	public static ComponentUI createUI(JComponent h) {
		return new BasicXYTableUI();
	}

	private JXYTable xyTable;

	private CellRendererPane rendererPane;

	private Handler handler;

	private PropertyChangeListener propertyChangeListener;

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		xyTable = (JXYTable) c;

		rendererPane = new CellRendererPane();
		c.add(rendererPane);

		handler = new Handler();
		xyTable.addMouseListener(handler);
		xyTable.addMouseMotionListener(handler);

		propertyChangeListener = new MyPropertyChangeListener();
		xyTable.addPropertyChangeListener(propertyChangeListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		c.remove(rendererPane);

		xyTable.removeMouseListener(handler);
		xyTable.removeMouseMotionListener(handler);

		xyTable.removePropertyChangeListener(propertyChangeListener);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		int columnWidth = xyTable.getColumnWidth();
		int columnHeaderHeight = xyTable.isShowColumnHeaderInner() && xyTable.getXYTableColumnHeader() != null ? xyTable.getColumnHeaderHeight() : 0;
		int rowHeaderWidth = xyTable.isShowRowHeaderInner() && xyTable.getXYTableRowHeader() != null ? xyTable.getRowHeaderWidth() : 0;
		int columnFooterHeight = xyTable.isShowColumnFooterInner() && xyTable.getXYTableColumnFooter() != null ? xyTable.getColumnFooterHeight() : 0;
		int rowFooterWidth = xyTable.isShowRowFooterInner() && xyTable.getXYTableRowFooter() != null ? xyTable.getRowFooterWidth() : 0;
		int columnCount = xyTable.getColumnCount();
		int rowHeight = xyTable.getRowHeight();
		int rowCount = xyTable.getRowCount();
		return new Dimension(columnCount * columnWidth + rowHeaderWidth + rowFooterWidth + 1, rowCount * rowHeight + columnHeaderHeight + columnFooterHeight + 1);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);

		Rectangle clip = g.getClipBounds();
		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width, clip.y + clip.height);

		int cMin = xyTable.columnAtPoint(upperLeft);
		if (cMin == -1) {
			cMin = 0;
		}
		int cMax = xyTable.columnAtPoint(lowerRight);
		if (cMax == -1) {
			cMax = xyTable.getColumnCount() - 1;
		}
		int rMin = xyTable.rowAtPoint(upperLeft);
		if (rMin == -1) {
			rMin = 0;
		}
		int rMax = xyTable.rowAtPoint(lowerRight);
		if (rMax == -1) {
			rMax = xyTable.getRowCount() - 1;
		}

		Graphics2D g2 = (Graphics2D) g.create();

		GraphicsHelper.activeAntiAliasing(g2);

		if (xyTable.isShowColumnHeaderInner() && xyTable.getXYTableColumnHeader() != null) {
			paintColumnHeader(g2);
		}
		if (xyTable.isShowColumnFooterInner() && xyTable.getXYTableColumnFooter() != null) {
			paintColumnFooter(g2);
		}
		if (xyTable.isShowRowHeaderInner() && xyTable.getXYTableRowHeader() != null) {
			paintRowHeader(g2);
		}
		if (xyTable.isShowRowFooterInner() && xyTable.getXYTableRowFooter() != null) {
			paintRowFooter(g2);
		}
		paintCells(g2, cMin, rMin, cMax, rMax, lowerRight.x, lowerRight.y);
		if (xyTable.isShowGrid()) {
			paintGrid(g2, cMin, rMin, cMax, rMax);
		}

		g2.dispose();

		rendererPane.removeAll();
	}

	protected void paintColumnHeader(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();

		int columnWidth = xyTable.getColumnWidth();
		int columnHeaderHeight = xyTable.getColumnHeaderHeight();
		int columnCount = xyTable.getColumnCount();
		int rowHeaderWidth = xyTable.isShowRowHeaderInner() && xyTable.getXYTableRowHeader() != null ? xyTable.getRowHeaderWidth() : 0;

		JXYTableColumnHeader header = xyTable.getXYTableColumnHeader();
		rendererPane.paintComponent(g2, header, xyTable, rowHeaderWidth, 0, columnCount * columnWidth + 1, columnHeaderHeight, true);

		g2.dispose();
	}

	protected void paintColumnFooter(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();

		int columnWidth = xyTable.getColumnWidth();
		int rowHeight = xyTable.getRowHeight();
		int columnHeaderHeight = xyTable.isShowColumnHeaderInner() && xyTable.getXYTableColumnHeader() != null ? xyTable.getColumnHeaderHeight() : 0;
		int columnFooterHeight = xyTable.getColumnFooterHeight();
		int columnCount = xyTable.getColumnCount();
		int rowCount = xyTable.getRowCount();
		int rowHeaderWidth = xyTable.isShowRowHeaderInner() && xyTable.getXYTableRowHeader() != null ? xyTable.getRowHeaderWidth() : 0;

		JXYTableColumnFooter header = xyTable.getXYTableColumnFooter();
		rendererPane.paintComponent(g2, header, xyTable, rowHeaderWidth, columnHeaderHeight + rowCount * rowHeight, columnCount * columnWidth + 1, columnFooterHeight, true);

		g2.dispose();
	}

	protected void paintRowHeader(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();

		int rowHeight = xyTable.getRowHeight();
		int rowHeaderWidth = xyTable.getRowHeaderWidth();
		int rowCount = xyTable.getRowCount();
		int columnHeaderHeight = xyTable.isShowColumnHeaderInner() && xyTable.getXYTableColumnHeader() != null ? xyTable.getColumnHeaderHeight() : 0;

		JXYTableRowHeader header = xyTable.getXYTableRowHeader();
		rendererPane.paintComponent(g2, header, xyTable, 0, columnHeaderHeight, rowHeaderWidth, rowCount * rowHeight + 1, true);

		g2.dispose();
	}

	protected void paintRowFooter(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();

		int columnWidth = xyTable.getColumnWidth();
		int rowHeight = xyTable.getRowHeight();
		int rowHeaderWidth = xyTable.isShowRowHeaderInner() && xyTable.getXYTableRowHeader() != null ? xyTable.getRowHeaderWidth() : 0;
		int rowFooterWidth = xyTable.getRowFooterWidth();
		int columnCount = xyTable.getColumnCount();
		int rowCount = xyTable.getRowCount();
		int columnHeaderHeight = xyTable.isShowColumnHeaderInner() && xyTable.getXYTableColumnHeader() != null ? xyTable.getColumnHeaderHeight() : 0;

		JXYTableRowFooter header = xyTable.getXYTableRowFooter();
		rendererPane.paintComponent(g2, header, xyTable, rowHeaderWidth + columnCount * columnWidth, columnHeaderHeight, rowFooterWidth, rowCount * rowHeight + 1, true);

		g2.dispose();
	}

	protected void paintCells(Graphics g, int cMin, int rMin, int cMax, int rMax, int xMax, int yMax) {
		XYTableModel model = xyTable.getModel();

		int columnWidth = xyTable.getColumnWidth();
		int columnHeaderHeight = xyTable.isShowColumnHeaderInner() && xyTable.getXYTableColumnHeader() != null ? xyTable.getColumnHeaderHeight() : 0;
		int rowHeaderWidth = xyTable.isShowRowHeaderInner() && xyTable.getXYTableRowHeader() != null ? xyTable.getRowHeaderWidth() : 0;
		int rowHeight = xyTable.getRowHeight();
		XYSelectionModel selectionModel = xyTable.getSelectionModel();
		XYTableCellRenderer cellRenderer = xyTable.getCellRenderer();

		int x = cMin * columnWidth + rowHeaderWidth;
		for (int i = cMin; i <= cMax; i++) {
			int y = rMin * rowHeight + columnHeaderHeight;
			for (int j = rMin; j <= rMax; j++) {
				Object value = model.getValue(i, j);

				boolean selected = selectionModel.isSelected(i, j);
				Component cell = cellRenderer.getXYTableCellRendererComponent(xyTable, value, i, j, selected);
				rendererPane.paintComponent(g, cell, xyTable, x, y, columnWidth, rowHeight, true);

				y += rowHeight;
			}

			if (y < yMax) {
				Component cell = cellRenderer.getXYTableCellRendererComponent(xyTable, null, i, -1, false);
				rendererPane.paintComponent(g, cell, xyTable, x, y, columnWidth, yMax - y + 1, true);
			}

			x += columnWidth;
		}

		if (x < xMax) {
			int y = rMin * rowHeight + columnHeaderHeight;
			for (int j = rMin; j <= rMax; j++) {
				Component cell = cellRenderer.getXYTableCellRendererComponent(xyTable, null, -1, j, false);
				rendererPane.paintComponent(g, cell, xyTable, x, y, xMax - x + 1, rowHeight, true);

				y += rowHeight;
			}
		}
	}

	protected void paintGrid(Graphics g, int cMin, int rMin, int cMax, int rMax) {
		Graphics2D g2 = (Graphics2D) g.create();

		int columnWidth = xyTable.getColumnWidth();
		int rowHeaderWidth = xyTable.isShowRowHeaderInner() && xyTable.getXYTableRowHeader() != null ? xyTable.getRowHeaderWidth() : 0;
		int rowHeight = xyTable.getRowHeight();
		int columnHeaderHeight = xyTable.isShowColumnHeaderInner() && xyTable.getXYTableColumnHeader() != null ? xyTable.getColumnHeaderHeight() : 0;

		g2.setColor(xyTable.getGridColor());
		int x = cMin * columnWidth + rowHeaderWidth;
		int yMin = rMin * rowHeight + columnHeaderHeight;
		int yMax = (rMax + 1) * rowHeight + columnHeaderHeight;
		for (int i = cMin; i <= cMax; i++) {
			g2.drawLine(x, yMin, x, yMax);
			x += columnWidth;
			g2.drawLine(x, yMin, x, yMax);
		}

		int y = rMin * rowHeight + columnHeaderHeight;
		int xMin = cMin * columnWidth + rowHeaderWidth;
		int xMax = (cMax + 1) * columnWidth + rowHeaderWidth;
		for (int j = rMin; j <= rMax; j++) {
			g2.drawLine(xMin, y, xMax, y);
			y += rowHeight;
			g2.drawLine(xMin, y, xMax, y);
		}

		g2.dispose();
	}

	private class Handler implements MouseListener, MouseMotionListener {

		private boolean selectedOnPress;

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isConsumed()) {
				selectedOnPress = false;
				return;
			}

			Point p = e.getPoint();
			int pressedRow = xyTable.rowAtPoint(p);
			int pressedCol = xyTable.columnAtPoint(p);

			xyTable.requestFocus();

			if (e.getButton() != MouseEvent.BUTTON1 && !e.isControlDown() && xyTable.getSelectionModel().isSelected(pressedCol, pressedRow)) {
				return;
			}

			selectedOnPress = true;

			setValueIsAdjusting(true);
			xyTable.changeSelection(pressedRow, pressedCol, e.isControlDown(), e.isShiftDown());
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (selectedOnPress) {
				setValueIsAdjusting(false);
			}
		}

		private void setValueIsAdjusting(boolean flag) {
			xyTable.getSelectionModel().setValueIsAdjusting(flag);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			Point p = e.getPoint();
			int row = xyTable.rowAtPoint(p);
			int column = xyTable.columnAtPoint(p);

			xyTable.changeSelection(row, column, e.isControlDown(), true);
		}

		@Override
		public void mouseMoved(MouseEvent e) {

		}
	}

	private void redrawList() {
		xyTable.revalidate();
		xyTable.repaint();
	}

	private class MyPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			redrawList();
		}
	}
}
