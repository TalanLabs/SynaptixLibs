package com.synaptix.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.GroupWeekModel;
import com.synaptix.swing.JGroupWeek;
import com.synaptix.swing.groupweek.JGroupWeekRowHeader;
import com.synaptix.swing.plaf.GroupWeekRowHeaderUI;

public class BasicGroupWeekRowHeaderUI extends GroupWeekRowHeaderUI {

	private JGroupWeekRowHeader groupWeekRowHeader;

	private CellRendererPane rendererPane;

	public static ComponentUI createUI(JComponent h) {
		return new BasicGroupWeekRowHeaderUI();
	}

	public void installUI(JComponent c) {
		groupWeekRowHeader = (JGroupWeekRowHeader) c;

		rendererPane = new CellRendererPane();
		groupWeekRowHeader.add(rendererPane);

		installListeners();
	}

	private void installListeners() {
	}

	public void uninstallUI(JComponent c) {
		uninstallListeners();

		groupWeekRowHeader.remove(rendererPane);
		rendererPane = null;
		groupWeekRowHeader = null;
	}

	private void uninstallListeners() {
	}

	public void paint(Graphics g, JComponent c) {
		JGroupWeek groupWeek = groupWeekRowHeader.getGroupWeek();
		GroupWeekModel model = groupWeek.getModel();
		if (model.getGroupCount() > 0) {
			Rectangle clip = g.getClipBounds();
			Point left = clip.getLocation();
			Point right = new Point(clip.x + clip.width - 1, clip.y
					+ clip.height - 1);

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

			int m = groupWeekRowHeader.getWidth() / 2;

			Rectangle groupRect = groupWeek.getGroupRect(gMin);
			groupRect.x = 0;
			groupRect.width = m;
			Rectangle rowRect = groupWeek.getRowRect(gMin, rMin);
			rowRect.x = m;
			rowRect.width = m;
			for (int group = gMin; group <= gMax; group++) {
				int hg = groupWeek.getGroupHeight(group);
				groupRect.height = hg;
				paintCell(g, groupRect, group, -1);

				if (gMin == gMax) {
					for (int row = rMin; row <= rMax; row++) {
						int h = groupWeek.getRowHeight(group, row);
						rowRect.height = h;
						paintCell(g, rowRect, group, row);
						rowRect.y += h;
					}
				} else if (group == gMin) {
					for (int row = rMin; row <= model.getGroupRowCount(gMin) - 1; row++) {
						int h = groupWeek.getRowHeight(group, row);
						rowRect.height = h;
						paintCell(g, rowRect, group, row);
						rowRect.y += h;
					}
				} else if (group == gMax) {
					for (int row = 0; row <= rMax; row++) {
						int h = groupWeek.getRowHeight(group, row);
						rowRect.height = h;
						paintCell(g, rowRect, group, row);
						rowRect.y += h;
					}
				} else {
					for (int row = 0; row <= model.getGroupRowCount(group) - 1; row++) {
						int h = groupWeek.getRowHeight(group, row);
						rowRect.height = h;
						paintCell(g, rowRect, group, row);
						rowRect.y += h;
					}
				}
				rowRect.y += groupWeek.getSpaceGroupHeight();
				groupRect.y += groupWeek.getGroupHeight(group);
				if (group < model.getGroupCount() - 1) {
					paintSpaceGroup(g, clip.x, groupRect.y, clip.width);
				}
				groupRect.y += groupWeek.getSpaceGroupHeight();
			}

			rendererPane.removeAll();
		}
	}

	private void paintCell(Graphics g, Rectangle cellRect, int group, int row) {
		Component component = getHeaderRenderer(group, row);
		rendererPane.paintComponent(g, component, groupWeekRowHeader,
				cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
	}

	private void paintSpaceGroup(Graphics g, int x, int y, int width) {
		g.setColor(groupWeekRowHeader.getGroupWeek().getSpaceGroupColor());
		g.fillRect(x, y, width, groupWeekRowHeader.getGroupWeek()
				.getSpaceGroupHeight());
	}

	private Component getHeaderRenderer(int group, int row) {
		return groupWeekRowHeader
				.prepareGroupWeekRowHeaderCellRendererComponent(group, row);
	}

	public Dimension getPreferredSize(JComponent c) {
		Dimension res = null;
		GroupWeekModel model = groupWeekRowHeader.getGroupWeek().getModel();
		if (model.getGroupCount() > 0) {
			Rectangle r = groupWeekRowHeader.getGroupWeek().getGroupRect(
					model.getGroupCount() - 1);
			int height = r.y + r.height;

			int wg = 0;
			int wr = 0;
			for (int group = 0; group < model.getGroupCount(); group++) {
				Component cg = getHeaderRenderer(group, -1);
				wg = Math.max(wg, cg.getPreferredSize().width);
				for (int row = 0; row < model.getGroupRowCount(group); row++) {
					Component cr = getHeaderRenderer(group, row);
					wr = Math.max(wr, cr.getPreferredSize().width);
				}
			}
			res = new Dimension(Math.max(wg, wr) * 2, height);
		} else {
			res = new Dimension();
		}
		return res;
	}
}
