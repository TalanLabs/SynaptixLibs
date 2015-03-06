package com.synaptix.swing.plaf.basic;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.synaptix.swing.JSyTable;
import com.synaptix.swing.plaf.SyTableLinesUI;
import com.synaptix.swing.table.JSyTableLines;

public class BasicSyTableLinesUI extends SyTableLinesUI {

	private static Cursor resizeCursor = Cursor
			.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);

	protected JSyTableLines ytableLines;

	protected CellRendererPane rendererPane;

	protected MouseInputListener mouseInputListener;

	@Override
	public void installUI(JComponent c) {
		ytableLines = (JSyTableLines) c;

		rendererPane = new CellRendererPane();
		ytableLines.add(rendererPane);

		installListeners();
	}

	@Override
	public void uninstallUI(JComponent c) {
		ytableLines.remove(rendererPane);
		rendererPane = null;

		uninstallListeners();

		ytableLines = null;
	}

	protected void installListeners() {
		mouseInputListener = createMouseInputListener();

		ytableLines.addMouseListener(mouseInputListener);
		ytableLines.addMouseMotionListener(mouseInputListener);
	}

	protected void uninstallListeners() {
		ytableLines.removeMouseListener(mouseInputListener);
		ytableLines.removeMouseMotionListener(mouseInputListener);

		mouseInputListener = null;
	}

	public static ComponentUI createUI(JComponent c) {
		return new BasicSyTableLinesUI();
	}

	public void paint(Graphics g, JComponent c) {
		Rectangle clip = g.getClipBounds();
		Point up = clip.getLocation();
		Point down = new Point(clip.x, clip.y + clip.height - 1);

		JSyTable table = ytableLines.getTable();
		if (table == null) {
			return;
		}

		TableModel model = table.getModel();
		if (model == null) {
			return;
		}

		int rowCount = 0;
		rowCount = table.getRowCount();

		if (rowCount <= 0) {
			paintCell(g,
					new Rectangle(0, 0, ytableLines.getWidth(), down.y + 1),
					"", false, -1); //$NON-NLS-1$
			return;
		}

		int rMin = table.rowAtPoint(up);
		int rMax = table.rowAtPoint(down);

		if (rMin == -1) {
			rMin = 0;
		}

		if (rMax == -1) {
			rMax = rowCount - 1;
		}

		if (rMax > rowCount) {
			rMax = rowCount - 1;
		}

		int x = 0;
		int y = 0;

		for (int row = 0; row < rMin; row++) {
			int rowHeight = table.getRowHeight(row);
			y += rowHeight;
		}

		for (int row = rMin; row <= rMax; row++) {
			int rowHeight = table.getRowHeight(row);

			int rowModel = table.convertRowIndexToModel(row);

			String text = String.valueOf(rowModel + 1);
			paintCell(g,
					new Rectangle(x, y, ytableLines.getWidth(), rowHeight),
					text, table.getSelectionModel().isSelectedIndex(row), row);

			y += rowHeight;
		}

		for (int row = rMax + 1; row < rowCount; row++) {
			int rowHeight = table.getRowHeight(row);
			y += rowHeight;
		}

		if (y < ytableLines.getHeight() && y < down.y) {
			paintCell(g, new Rectangle(x, y, ytableLines.getWidth(), down.y - y
					+ 1), "", false, -1); //$NON-NLS-1$
		}

		rendererPane.removeAll();
	}

	private Component getLinesRenderer(String text, boolean isSelected, int row) {
		TableCellRenderer renderer = ytableLines.getDefaultRenderer();

		return renderer.getTableCellRendererComponent(ytableLines.getTable(),
				text, false, isSelected, row, 0);
	}

	private void paintCell(Graphics g, Rectangle cellRect, String text,
			boolean isSelected, int row) {
		Component component = getLinesRenderer(text, isSelected, row);
		rendererPane.paintComponent(g, component, ytableLines, cellRect.x,
				cellRect.y, cellRect.width, cellRect.height, true);
	}

	private Dimension createLinesSize() {
		long height = 0;
		long width = 0;

		JTable table = ytableLines.getTable();
		if (table != null) {
			TableModel model = table.getModel();
			if (model != null) {
				int rowCount = model.getRowCount();

				for (int i = 0; i < rowCount; i++) {
					height += table.getRowHeight(i);
				}

				if (ytableLines.getFont() != null) {
					FontMetrics fm = ytableLines.getFontMetrics(ytableLines
							.getFont());
					width = fm.stringWidth(String.valueOf(rowCount)) + 16;
				} else {
					width = 0;
				}

				if (height > Integer.MAX_VALUE) {
					height = Integer.MAX_VALUE;
				}
				if (width > Integer.MAX_VALUE) {
					width = Integer.MAX_VALUE;
				}
			}
		}
		return new Dimension((int) width, (int) height);
	}

	@Override
	public Dimension getMaximumSize(JComponent c) {
		return createLinesSize();
	}

	@Override
	public Dimension getMinimumSize(JComponent c) {
		return createLinesSize();
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		return createLinesSize();
	}

	protected MouseInputListener createMouseInputListener() {
		return new MouseInputHandler();
	}

	private static boolean canResize(int row, JSyTableLines yTableLines) {
		return row != -1 && yTableLines.isRowResinzingAllowed();
	}

	private int changeRowHeight(int resizingRow, JSyTableLines tl,
			int oldHeight, int newHeight) {
		if (newHeight < 1) {
			return 0;
		}

		JTable table = tl.getTable();
		if (table == null) {
			return 0;
		}

		table.setRowHeight(resizingRow, newHeight);

		return 0;
	}

	public class MouseInputHandler implements MouseInputListener {

		private Cursor otherCursor = resizeCursor;

		private int mouseYOffset;

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
				JTable table = ytableLines.getTable();
				if (table == null) {
					return;
				}

				int row = table.rowAtPoint(e.getPoint());
				if (row != -1) {
					if (e.isControlDown()) {
						table.getSelectionModel()
								.addSelectionInterval(row, row);
					} else if (e.isShiftDown()) {
						int anchor = table.getSelectionModel()
								.getAnchorSelectionIndex();
						table.getSelectionModel().setSelectionInterval(anchor,
								row);
					} else {
						table.getSelectionModel()
								.setSelectionInterval(row, row);
					}
				}
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			JTable table = ytableLines.getTable();
			if (table == null) {
				return;
			}

			ytableLines.setResizingRow(-1);

			Point p = e.getPoint();

			int resizingRow = getResizingRow(p);
			if (canResize(resizingRow, ytableLines)) {
				ytableLines.setResizingRow(resizingRow);
				mouseYOffset = p.y - table.getRowHeight(resizingRow);
			}
		}

		public void mouseReleased(MouseEvent e) {
			ytableLines.setResizingRow(-1);
		}

		public void mouseDragged(MouseEvent e) {
			JTable table = ytableLines.getTable();
			if (table == null) {
				return;
			}

			int mouseY = e.getY();

			int resizingRow = ytableLines.getResizingRow();
			if (resizingRow != -1) {
				int oldHeight = table.getRowHeight(resizingRow);
				int newHeight = mouseY - mouseYOffset;
				mouseYOffset += changeRowHeight(resizingRow, ytableLines,
						oldHeight, newHeight);
			} else {
				int row = table.rowAtPoint(e.getPoint());
				if (row != -1) {
					if (e.isControlDown()) {
						table.getSelectionModel()
								.addSelectionInterval(row, row);
					} else {
						int anchor = table.getSelectionModel()
								.getAnchorSelectionIndex();
						table.getSelectionModel().setSelectionInterval(anchor,
								row);
					}
				}
			}
		}

		private void swapCursor() {
			Cursor tmp = ytableLines.getCursor();
			ytableLines.setCursor(otherCursor);
			otherCursor = tmp;
		}

		private int getResizingRow(Point p) {
			JTable table = ytableLines.getTable();
			if (table == null) {
				return -1;
			}
			return getResizingRow(p, table.rowAtPoint(p));
		}

		private int getResizingRow(Point p, int row) {
			if (row == -1) {
				return -1;
			}

			JTable table = ytableLines.getTable();
			int y = 0;
			for (int i = 0; i < row; i++) {
				int rowHeight = table.getRowHeight(i);
				y += rowHeight;
			}

			Rectangle r = new Rectangle(0, y, ytableLines.getWidth(), table
					.getRowHeight(row));
			r.grow(0, -3);
			if (r.contains(p)) {
				return -1;
			}

			int midPoint = r.y + r.height / 2;
			int rowIndex = (p.y < midPoint) ? row - 1 : row;

			return rowIndex;
		}

		public void mouseMoved(MouseEvent e) {
			if (canResize(getResizingRow(e.getPoint()), ytableLines) != (ytableLines
					.getCursor() == resizeCursor)) {
				swapCursor();
			}
		}
	}
}
