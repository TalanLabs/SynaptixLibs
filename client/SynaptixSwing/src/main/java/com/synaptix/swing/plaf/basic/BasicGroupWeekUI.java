package com.synaptix.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.GroupWeekModel;
import com.synaptix.swing.JGroupWeek;
import com.synaptix.swing.plaf.GroupWeekUI;

public class BasicGroupWeekUI extends GroupWeekUI {

	private JGroupWeek groupWeek;

	private CellRendererPane rendererPane;

	private MouseHandler mouseHandler;

	public static ComponentUI createUI(JComponent h) {
		return new BasicGroupWeekUI();
	}

	public void installUI(JComponent c) {
		groupWeek = (JGroupWeek) c;

		mouseHandler = new MouseHandler();

		rendererPane = new CellRendererPane();
		groupWeek.add(rendererPane);

		installListeners();
	}

	private void installListeners() {
		groupWeek.addMouseListener(mouseHandler);
	}

	public void uninstallUI(JComponent c) {
		uninstallListeners();

		mouseHandler = null;

		groupWeek.remove(rendererPane);
		rendererPane = null;
		groupWeek = null;
	}

	private void uninstallListeners() {
		groupWeek.removeMouseListener(mouseHandler);
	}

	public void paint(Graphics g, JComponent c) {
		GroupWeekModel model = groupWeek.getModel();
		if (model.getGroupCount() > 0) {
			Rectangle clip = g.getClipBounds();
			Point left = clip.getLocation();
			Point right = new Point(clip.x + clip.width - 1, clip.y
					+ clip.height - 1);

			int cMin = groupWeek.dayAtPoint(left);
			if (cMin == -1) {
				cMin = 0;
			}
			int cMax = groupWeek.dayAtPoint(right);
			if (cMax == -1) {
				cMax = 6;
			}

			int gMin = groupWeek.groupAtPoint(left);
			if (gMin == -1) {
				gMin = 0;
			}
			int gMax = groupWeek.groupAtPoint(right);
			if (gMax == -1) {
				gMax = model.getGroupCount() - 1;
			}

			int rMin = groupWeek.rowAtPoint(left);
			if (rMin == -1) {
				rMin = 0;
			}
			int rMax = groupWeek.rowAtPoint(right);
			if (rMax == -1) {
				rMax = model.getGroupRowCount(gMax) - 1;
			}

			int marge = groupWeek.getRowMargin();

			paintGrid(g, gMin, rMin, cMin, gMax, rMax, cMax);

			Rectangle groupRect = groupWeek.getGroupRect(gMin);
			Rectangle rowRect = groupWeek.getRowRect(gMin, rMin);
			for (int group = gMin; group <= gMax; group++) {
				int hg = groupWeek.getGroupHeight(group);
				groupRect.height = hg;

				if (gMin == gMax) {
					for (int row = rMin; row <= rMax; row++) {
						int h = groupWeek.getRowHeight(group, row);
						rowRect.height = h - marge;
						paintCells(g, rowRect, group, row, cMin, cMax);
						rowRect.y += h;
					}
				} else if (group == gMin) {
					for (int row = rMin; row <= model.getGroupRowCount(gMin) - 1; row++) {
						int h = groupWeek.getRowHeight(group, row);
						rowRect.height = h - marge;
						paintCells(g, rowRect, group, row, cMin, cMax);
						rowRect.y += h;
					}
				} else if (group == gMax) {
					for (int row = 0; row <= rMax; row++) {
						int h = groupWeek.getRowHeight(group, row);
						rowRect.height = h - marge;
						paintCells(g, rowRect, group, row, cMin, cMax);
						rowRect.y += h;
					}
				} else {
					for (int row = 0; row <= model.getGroupRowCount(group) - 1; row++) {
						int h = groupWeek.getRowHeight(group, row);
						rowRect.height = h - marge;
						paintCells(g, rowRect, group, row, cMin, cMax);
						rowRect.y += h;
					}
				}
				rowRect.y += groupWeek.getSpaceGroupHeight();
				groupRect.y += groupWeek.getGroupHeight(group);
				if (group < model.getGroupCount() - 1) {
					paintSpaceGroup(g, left.x, groupRect.y, clip.width);
				}
				groupRect.y += groupWeek.getSpaceGroupHeight();
			}
		}

		rendererPane.removeAll();
	}

	private void paintGrid(Graphics g, int gMin, int rMin, int cMin, int gMax,
			int rMax, int cMax) {
		if (groupWeek.isShowGrid()) {
			GroupWeekModel model = groupWeek.getModel();

			g.setColor(groupWeek.getGridColor());

			Rectangle minCell = groupWeek.getCellRect(gMin, rMin, cMin);
			Rectangle maxCell = groupWeek.getCellRect(gMax, rMax, cMax);
			Rectangle damagedArea = minCell.union(maxCell);

			int tableHeight = damagedArea.y + damagedArea.height;
			int x = damagedArea.x;
			for (int day = cMin; day <= cMax; day++) {
				x += groupWeek.getDayWidth(day);
				g.drawLine(x - 1, damagedArea.y, x - 1, tableHeight);
			}

			int tableWidth = damagedArea.x + damagedArea.width;
			int y = minCell.y;
			for (int group = gMin; group <= gMax; group++) {
				if (gMin == gMax) {
					for (int row = rMin; row <= rMax; row++) {
						int h = groupWeek.getRowHeight(group, row);
						y += h;
						g.drawLine(damagedArea.x, y - 1, tableWidth, y - 1);
					}
				} else if (group == gMin) {
					for (int row = rMin; row <= model.getGroupRowCount(gMin) - 1; row++) {
						int h = groupWeek.getRowHeight(group, row);
						y += h;
						g.drawLine(damagedArea.x, y - 1, tableWidth, y - 1);
					}
				} else if (group == gMax) {
					for (int row = 0; row <= rMax; row++) {
						int h = groupWeek.getRowHeight(group, row);
						y += h;
						g.drawLine(damagedArea.x, y - 1, tableWidth, y - 1);
					}
				} else {
					for (int row = 0; row <= model.getGroupRowCount(group) - 1; row++) {
						int h = groupWeek.getRowHeight(group, row);
						y += h;
						g.drawLine(damagedArea.x, y - 1, tableWidth, y - 1);
					}
				}
				y += groupWeek.getSpaceGroupHeight();
			}
		}
	}

	private void paintSpaceGroup(Graphics g, int x, int y, int width) {
		g.setColor(groupWeek.getSpaceGroupColor());
		g.fillRect(x, y, width, groupWeek.getSpaceGroupHeight());
	}

	private void paintCells(Graphics g, Rectangle rowRect, int group, int row,
			int cMin, int cMax) {
		int marge = groupWeek.getDayMargin();

		Rectangle cellRect = new Rectangle(rowRect);
		for (int day = cMin; day <= cMax; day++) {
			int w = groupWeek.getDayWidth(day);
			cellRect.width = w - marge;
			paintCell(g, cellRect, group, row, day);
			cellRect.x += w;
		}
	}

	private void paintCell(Graphics g, Rectangle cellRect, int group, int row,
			int day) {
		Component component = groupWeek.prepareCellRenderer(group, row, day);
		rendererPane.paintComponent(g, component, groupWeek, cellRect.x,
				cellRect.y, cellRect.width, cellRect.height, true);
	}

	public Dimension getPreferredSize(JComponent c) {
		int height = 0;
		GroupWeekModel model = groupWeek.getModel();
		if (model.getGroupCount() > 0) {
			Rectangle r = groupWeek.getGroupRect(model.getGroupCount() - 1);
			height = r.y + r.height;
		}
		return new Dimension(0, height);
	}

	private final class MouseHandler extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			groupWeek.requestFocus();

			if (groupWeek.isEnabled()) {
				GroupWeekModel model = groupWeek.getModel();

				Point p = e.getPoint();

				int group = groupWeek.groupAtPoint(p);
				int row = groupWeek.rowAtPoint(p);
				int day = groupWeek.dayAtPoint(p);

				if (group != -1 && row != -1 && day != -1
						&& model.isSelected(group, row, day)) {
					groupWeek.setSelected(group, row, day);
				} else if (groupWeek.isSelection()) {
					groupWeek.clearSelection();
				}
			}
		}
	}
}
