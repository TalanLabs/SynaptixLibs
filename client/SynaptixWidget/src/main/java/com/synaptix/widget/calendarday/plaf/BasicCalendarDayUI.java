package com.synaptix.widget.calendarday.plaf;

import java.awt.Color;
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
import com.synaptix.widget.calendarday.CalendarDayCellRenderer;
import com.synaptix.widget.calendarday.CalendarDayModel;
import com.synaptix.widget.calendarday.JCalendarDay;
import com.synaptix.widget.calendarday.JCalendarDayColumnHeader;
import com.synaptix.widget.calendarday.JCalendarDayRowHeader;

public class BasicCalendarDayUI extends CalendarDayUI {

	public static ComponentUI createUI(JComponent h) {
		return new BasicCalendarDayUI();
	}

	private JCalendarDay calendarDay;

	private CellRendererPane rendererPane;

	private Handler handler;

	private PropertyChangeListener propertyChangeListener;

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		calendarDay = (JCalendarDay) c;

		rendererPane = new CellRendererPane();
		c.add(rendererPane);

		handler = new Handler();
		calendarDay.addMouseListener(handler);
		calendarDay.addMouseMotionListener(handler);

		propertyChangeListener = new MyPropertyChangeListener();
		calendarDay.addPropertyChangeListener(propertyChangeListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		c.remove(rendererPane);

		calendarDay.removeMouseListener(handler);
		calendarDay.removeMouseMotionListener(handler);

		calendarDay.removePropertyChangeListener(propertyChangeListener);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		int columnWidth = calendarDay.getColumnWidth();
		int columnHeaderHeight = calendarDay.isShowColumnHeaderInner() && calendarDay.getCalendarDayColumnHeader() != null ? calendarDay.getColumnHeaderHeight() : 0;
		int rowHeaderWidth = calendarDay.isShowRowHeaderInner() && calendarDay.getCalendarDayRowHeader() != null ? calendarDay.getRowHeaderWidth() : 0;
		int columnCount = calendarDay.getColumnCount();
		int rowHeight = calendarDay.getRowHeight();
		int rowCount = calendarDay.getRowCount();
		return new Dimension(columnCount * columnWidth + rowHeaderWidth + 1, rowCount * rowHeight + columnHeaderHeight + 1);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);

		Rectangle clip = g.getClipBounds();
		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width, clip.y + clip.height);

		int cMin = calendarDay.columnAtPoint(upperLeft);
		if (cMin == -1) {
			cMin = 0;
		}
		int cMax = calendarDay.columnAtPoint(lowerRight);
		if (cMax == -1) {
			cMax = calendarDay.getColumnCount() - 1;
		}
		int rMin = calendarDay.rowAtPoint(upperLeft);
		if (rMin == -1) {
			rMin = 0;
		}
		int rMax = calendarDay.rowAtPoint(lowerRight);
		if (rMax == -1) {
			rMax = calendarDay.getRowCount();
		}

		Graphics2D g2 = (Graphics2D) g.create();

		GraphicsHelper.activeAntiAliasing(g2);

		if (calendarDay.isShowColumnHeaderInner() && calendarDay.getCalendarDayColumnHeader() != null) {
			paintColumnHeader(g2);
		}
		if (calendarDay.isShowRowHeaderInner() && calendarDay.getCalendarDayRowHeader() != null) {
			paintRowHeader(g2);
		}
		paintMonths(g2, cMin, rMin, cMax, rMax);

		g2.dispose();

		rendererPane.removeAll();
	}

	protected void paintColumnHeader(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();

		int columnWidth = calendarDay.getColumnWidth();
		int columnHeaderHeight = calendarDay.getColumnHeaderHeight();
		int columnCount = calendarDay.getColumnCount();
		int rowHeaderWidth = calendarDay.isShowRowHeaderInner() && calendarDay.getCalendarDayRowHeader() != null ? calendarDay.getRowHeaderWidth() : 0;

		JCalendarDayColumnHeader header = calendarDay.getCalendarDayColumnHeader();
		rendererPane.paintComponent(g2, header, calendarDay, rowHeaderWidth, 0, columnCount * columnWidth + 1, columnHeaderHeight, true);

		g2.dispose();
	}

	protected void paintRowHeader(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();

		int rowHeight = calendarDay.getRowHeight();
		int rowHeaderWidth = calendarDay.getRowHeaderWidth();
		int rowCount = calendarDay.getRowCount();
		int columnHeaderHeight = calendarDay.isShowColumnHeaderInner() && calendarDay.getCalendarDayColumnHeader() != null ? calendarDay.getColumnHeaderHeight() : 0;

		JCalendarDayRowHeader header = calendarDay.getCalendarDayRowHeader();
		rendererPane.paintComponent(g2, header, calendarDay, 0, columnHeaderHeight, rowHeaderWidth, rowCount * rowHeight + 1, true);

		g2.dispose();
	}

	protected void paintMonths(Graphics g, int cMin, int rMin, int cMax, int rMax) {
		Graphics2D g2 = (Graphics2D) g.create();

		CalendarDayModel model = calendarDay.getModel();

		int columnWidth = calendarDay.getColumnWidth();
		int columnHeaderHeight = calendarDay.isShowColumnHeaderInner() && calendarDay.getCalendarDayColumnHeader() != null ? calendarDay.getColumnHeaderHeight() : 0;
		int rowHeaderWidth = calendarDay.isShowRowHeaderInner() && calendarDay.getCalendarDayRowHeader() != null ? calendarDay.getRowHeaderWidth() : 0;
		int rowHeight = calendarDay.getRowHeight();
		boolean showGrid = calendarDay.isShowGrid();
		Color gridColor = calendarDay.getGridColor();
		int columnCount = model.getColumnCount();
		XYSelectionModel selectionModel = calendarDay.getSelectionModel();
		CalendarDayCellRenderer cellRenderer = calendarDay.getCellRenderer();
		int x = cMin * columnWidth + rowHeaderWidth;
		for (int i = cMin; i <= cMax; i++) {
			int nbRow = model.getRowCount(i);
			if (rMin < nbRow) {
				int rCur = Math.min(rMax, nbRow - 1);

				int y = rMin * rowHeight + columnHeaderHeight;
				for (int j = rMin; j <= rCur; j++) {
					Object value = model.getValue(i, j);

					boolean selected = selectionModel.isSelected(i, j);
					Component cell = cellRenderer.getCalendarDayCellRendererComponent(calendarDay, value, i, j, selected);
					rendererPane.paintComponent(g2, cell, calendarDay, x, y, columnWidth, rowHeight, true);

					y += rowHeight;
				}

				if (showGrid) {
					g2.setColor(gridColor);
					int prevNbRow = i > 0 ? model.getRowCount(i - 1) : (rCur + 1);
					g2.drawLine(x, columnHeaderHeight, x, columnHeaderHeight + (Math.max(rCur + 1, prevNbRow)) * rowHeight);

					int nextNbRow = i < columnCount - 1 ? model.getRowCount(i + 1) : (rCur + 1);
					g2.drawLine(x + columnWidth, columnHeaderHeight, x + columnWidth, columnHeaderHeight + (Math.max(rCur + 1, nextNbRow)) * rowHeight);

					y = rMin * rowHeight + columnHeaderHeight;
					for (int j = rMin; j <= rCur; j++) {
						g2.drawLine(x, y, x + columnWidth, y);
						g2.drawLine(x, y + rowHeight, x + columnWidth, y + rowHeight);
						y += rowHeight;
					}
				}
			}

			x += columnWidth;
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
			int pressedRow = calendarDay.rowAtPoint(p);
			int pressedCol = calendarDay.columnAtPoint(p);

			calendarDay.requestFocus();

			if (e.getButton() != MouseEvent.BUTTON1 && !e.isControlDown() && calendarDay.getSelectionModel().isSelected(pressedCol, pressedRow)) {
				return;
			}

			selectedOnPress = true;

			setValueIsAdjusting(true);
			calendarDay.changeSelection(pressedRow, pressedCol, e.isControlDown(), e.isShiftDown());
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (selectedOnPress) {
				setValueIsAdjusting(false);
			}
		}

		private void setValueIsAdjusting(boolean flag) {
			calendarDay.getSelectionModel().setValueIsAdjusting(flag);
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
			int row = calendarDay.rowAtPoint(p);
			int column = calendarDay.columnAtPoint(p);

			calendarDay.changeSelection(row, column, e.isControlDown(), true);
		}

		@Override
		public void mouseMoved(MouseEvent e) {

		}
	}

	private void redrawList() {
		calendarDay.revalidate();
		calendarDay.repaint();
	}

	private class MyPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			redrawList();
		}
	}
}
