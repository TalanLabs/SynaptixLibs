package com.synaptix.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.groupweek.JGroupWeekColumnHeader;
import com.synaptix.swing.plaf.GroupWeekColumnHeaderUI;

public class BasicGroupWeekColumnHeaderUI extends GroupWeekColumnHeaderUI {

	private JGroupWeekColumnHeader groupWeekColumnHeader;

	private CellRendererPane rendererPane;

	public static ComponentUI createUI(JComponent h) {
		return new BasicGroupWeekColumnHeaderUI();
	}

	public void installUI(JComponent c) {
		groupWeekColumnHeader = (JGroupWeekColumnHeader) c;

		rendererPane = new CellRendererPane();
		groupWeekColumnHeader.add(rendererPane);

		installListeners();
	}

	private void installListeners() {
	}

	public void uninstallUI(JComponent c) {
		uninstallListeners();

		groupWeekColumnHeader.remove(rendererPane);
		rendererPane = null;
		groupWeekColumnHeader = null;
	}

	private void uninstallListeners() {
	}

	public void paint(Graphics g, JComponent c) {
		Rectangle clip = g.getClipBounds();
		Point left = clip.getLocation();
		Point right = new Point(clip.x + clip.width - 1, clip.y);

		int cMin = groupWeekColumnHeader.dayAtPoint(left);
		int cMax = groupWeekColumnHeader.dayAtPoint(right);
		if (cMin == -1) {
			cMin = 0;
		}
		if (cMax == -1) {
			cMax = 6;
		}

		Rectangle cellRect = groupWeekColumnHeader.getHeaderDayRect(cMin);
		for (int day = cMin; day <= cMax; day++) {
			int dayWidth = groupWeekColumnHeader.getDayWidth(day);
			cellRect.width = dayWidth;
			paintCell(g, cellRect, day);
			cellRect.x += dayWidth;
		}

		rendererPane.removeAll();
	}

	private void paintCell(Graphics g, Rectangle cellRect, int day) {
		Component component = getHeaderRenderer(day);
		rendererPane.paintComponent(g, component, groupWeekColumnHeader,
				cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
	}

	private Component getHeaderRenderer(int day) {
		return groupWeekColumnHeader
				.prepareGroupWeekColumnHeaderCellRendererComponent(day);
	}

	private int getHeaderHeight() {
		int height = 0;
		for (int day = 0; day < 7; day++) {
			Component comp = getHeaderRenderer(day);
			int rendererHeight = comp.getPreferredSize().height;
			height = Math.max(height, rendererHeight);
		}
		return height;
	}

	private Dimension createHeaderSize(long width) {
		if (width > Integer.MAX_VALUE) {
			width = Integer.MAX_VALUE;
		}
		return new Dimension((int) width, getHeaderHeight());
	}

	public Dimension getPreferredSize(JComponent c) {
		int width = 0;
		for (int day = 0; day < 7; day++) {
			width += groupWeekColumnHeader.getDayWidth(day);
		}
		return createHeaderSize(0);
	}
}
