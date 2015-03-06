package com.synaptix.widget.calendarday.plaf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.utils.GraphicsHelper;
import com.synaptix.widget.calendarday.CalendarDayColumnHeaderRenderer;
import com.synaptix.widget.calendarday.CalendarDayModel;
import com.synaptix.widget.calendarday.JCalendarDay;
import com.synaptix.widget.calendarday.JCalendarDayColumnHeader;

public class BasicCalendarDayColumnHeaderUI extends CalendarDayColumnHeaderUI {

	public static ComponentUI createUI(JComponent h) {
		return new BasicCalendarDayColumnHeaderUI();
	}

	private JCalendarDayColumnHeader calendarDayColumnHeader;

	private JCalendarDay calendarDay;

	private CellRendererPane rendererPane;

	private PropertyChangeListener propertyChangeListener;

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		calendarDayColumnHeader = (JCalendarDayColumnHeader) c;
		calendarDay = calendarDayColumnHeader.getCalendarDay();

		rendererPane = new CellRendererPane();
		c.add(rendererPane);

		propertyChangeListener = new MyPropertyChangeListener();
		calendarDay.addPropertyChangeListener(propertyChangeListener);
		calendarDayColumnHeader.addPropertyChangeListener(propertyChangeListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		c.remove(rendererPane);

		calendarDay.removePropertyChangeListener(propertyChangeListener);
		calendarDayColumnHeader.removePropertyChangeListener(propertyChangeListener);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		int columnWidth = calendarDayColumnHeader.getColumnWidth();
		int columnHeaderHeight = calendarDayColumnHeader.getColumnHeaderHeight();
		int columnCount = calendarDayColumnHeader.getColumnCount();
		return new Dimension(columnCount * columnWidth + 1, columnHeaderHeight);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);

		Rectangle clip = g.getClipBounds();
		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width, clip.y + clip.height);

		int cMin = calendarDayColumnHeader.columnAtPoint(upperLeft);
		if (cMin == -1) {
			cMin = 0;
		}
		int cMax = calendarDayColumnHeader.columnAtPoint(lowerRight);
		if (cMax == -1) {
			cMax = calendarDayColumnHeader.getColumnCount() - 1;
		}

		Graphics2D g2 = (Graphics2D) g.create();

		GraphicsHelper.activeAntiAliasing(g2);

		if (calendarDayColumnHeader.getColumnHeaderRenderer() != null) {
			paintColumnHeader(g2, cMin, cMax);
		}

		g2.dispose();

		rendererPane.removeAll();
	}

	protected void paintColumnHeader(Graphics g, int cMin, int cMax) {
		Graphics2D g2 = (Graphics2D) g.create();

		CalendarDayModel model = calendarDay.getModel();

		int columnWidth = calendarDayColumnHeader.getColumnWidth();
		int columnHeaderHeight = calendarDayColumnHeader.getColumnHeaderHeight();
		CalendarDayColumnHeaderRenderer columnCellRenderer = calendarDayColumnHeader.getColumnHeaderRenderer();
		int x = cMin * columnWidth;
		for (int i = cMin; i <= cMax; i++) {
			Component header = columnCellRenderer.getCalendarDayColumnHeaderRendererComponent(calendarDay, model.getColumnName(i), i);

			rendererPane.paintComponent(g2, header, calendarDayColumnHeader, x, 0, columnWidth + 1, columnHeaderHeight, true);
			x += columnWidth;
		}

		g2.dispose();
	}

	private void redrawList() {
		calendarDayColumnHeader.revalidate();
		calendarDayColumnHeader.repaint();
	}

	private class MyPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			redrawList();
		}
	}
}
