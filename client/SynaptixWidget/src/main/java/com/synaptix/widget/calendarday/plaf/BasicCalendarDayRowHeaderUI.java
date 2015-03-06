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
import com.synaptix.widget.calendarday.CalendarDayRowHeaderRenderer;
import com.synaptix.widget.calendarday.JCalendarDay;
import com.synaptix.widget.calendarday.JCalendarDayRowHeader;

public class BasicCalendarDayRowHeaderUI extends CalendarDayRowHeaderUI {

	public static ComponentUI createUI(JComponent h) {
		return new BasicCalendarDayRowHeaderUI();
	}

	private JCalendarDayRowHeader calendarDayRowHeader;

	private JCalendarDay calendarDay;

	private CellRendererPane rendererPane;

	private PropertyChangeListener propertyChangeListener;

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		calendarDayRowHeader = (JCalendarDayRowHeader) c;
		calendarDay = calendarDayRowHeader.getCalendarDay();

		rendererPane = new CellRendererPane();
		c.add(rendererPane);

		propertyChangeListener = new MyPropertyChangeListener();
		calendarDay.addPropertyChangeListener(propertyChangeListener);
		calendarDayRowHeader.addPropertyChangeListener(propertyChangeListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		c.remove(rendererPane);

		calendarDay.removePropertyChangeListener(propertyChangeListener);
		calendarDayRowHeader.removePropertyChangeListener(propertyChangeListener);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		int rowHeight = calendarDayRowHeader.getRowHeight();
		int rowHeaderWidth = calendarDayRowHeader.getRowHeaderWidth();
		int rowCount = calendarDayRowHeader.getRowCount();
		return new Dimension(rowHeaderWidth, rowHeight * rowCount + 1);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);

		Rectangle clip = g.getClipBounds();
		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width, clip.y + clip.height);

		int rMin = calendarDayRowHeader.rowAtPoint(upperLeft);
		if (rMin == -1) {
			rMin = 0;
		}
		int rMax = calendarDayRowHeader.rowAtPoint(lowerRight);
		if (rMax == -1) {
			rMax = calendarDayRowHeader.getRowCount() - 1;
		}

		Graphics2D g2 = (Graphics2D) g.create();

		GraphicsHelper.activeAntiAliasing(g2);

		if (calendarDayRowHeader.getRowHeaderRenderer() != null) {
			paintRowHeader(g2, rMin, rMax);
		}

		g2.dispose();

		rendererPane.removeAll();
	}

	protected void paintRowHeader(Graphics g, int rMin, int rMax) {
		Graphics2D g2 = (Graphics2D) g.create();

		int rowHeight = calendarDayRowHeader.getRowHeight();
		int rowHeaderWidth = calendarDayRowHeader.getRowHeaderWidth();
		CalendarDayRowHeaderRenderer cellRenderer = calendarDayRowHeader.getRowHeaderRenderer();
		int y = rMin * rowHeight;
		for (int i = rMin; i <= rMax; i++) {
			Component header = cellRenderer.getCalendarDayRowHeaderRendererComponent(calendarDay, i);

			rendererPane.paintComponent(g2, header, calendarDayRowHeader, 0, y, rowHeaderWidth, rowHeight + 1, true);
			y += rowHeight;
		}

		g2.dispose();
	}

	private void redrawList() {
		calendarDayRowHeader.revalidate();
		calendarDayRowHeader.repaint();
	}

	private class MyPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			redrawList();
		}
	}
}
