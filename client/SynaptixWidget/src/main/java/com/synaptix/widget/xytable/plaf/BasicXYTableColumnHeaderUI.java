package com.synaptix.widget.xytable.plaf;

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
import com.synaptix.widget.xytable.JXYTable;
import com.synaptix.widget.xytable.JXYTableColumnHeader;
import com.synaptix.widget.xytable.XYTableColumnHeaderRenderer;
import com.synaptix.widget.xytable.XYTableModel;

public class BasicXYTableColumnHeaderUI extends XYTableColumnHeaderUI {

	public static ComponentUI createUI(JComponent h) {
		return new BasicXYTableColumnHeaderUI();
	}

	private JXYTableColumnHeader xyTableColumnHeader;

	private JXYTable xyTable;

	private CellRendererPane rendererPane;

	private PropertyChangeListener propertyChangeListener;

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		xyTableColumnHeader = (JXYTableColumnHeader) c;
		xyTable = xyTableColumnHeader.getXYTable();

		rendererPane = new CellRendererPane();
		c.add(rendererPane);

		propertyChangeListener = new MyPropertyChangeListener();
		xyTable.addPropertyChangeListener(propertyChangeListener);
		xyTableColumnHeader.addPropertyChangeListener(propertyChangeListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		c.remove(rendererPane);

		xyTable.removePropertyChangeListener(propertyChangeListener);
		xyTableColumnHeader.removePropertyChangeListener(propertyChangeListener);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		int columnWidth = xyTableColumnHeader.getColumnWidth();
		int columnHeaderHeight = xyTableColumnHeader.getColumnHeaderHeight();
		int columnCount = xyTableColumnHeader.getColumnCount();
		return new Dimension(columnCount * columnWidth + 1, columnHeaderHeight);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);

		Rectangle clip = g.getClipBounds();
		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width, clip.y + clip.height);

		int cMin = xyTableColumnHeader.columnAtPoint(upperLeft);
		if (cMin == -1) {
			cMin = 0;
		}
		int cMax = xyTableColumnHeader.columnAtPoint(lowerRight);
		if (cMax == -1) {
			cMax = xyTableColumnHeader.getColumnCount() - 1;
		}

		Graphics2D g2 = (Graphics2D) g.create();

		GraphicsHelper.activeAntiAliasing(g2);

		if (xyTableColumnHeader.getColumnHeaderRenderer() != null) {
			paintColumnHeader(g2, cMin, cMax, lowerRight.x);
		}

		g2.dispose();

		rendererPane.removeAll();
	}

	protected void paintColumnHeader(Graphics g, int cMin, int cMax, int xMax) {
		Graphics2D g2 = (Graphics2D) g.create();

		XYTableModel model = xyTable.getModel();

		int columnWidth = xyTableColumnHeader.getColumnWidth();
		int columnHeaderHeight = xyTableColumnHeader.getColumnHeaderHeight();
		XYTableColumnHeaderRenderer columnCellRenderer = xyTableColumnHeader.getColumnHeaderRenderer();
		int x = cMin * columnWidth;
		for (int i = cMin; i <= cMax; i++) {
			Component header = columnCellRenderer.getXYTableColumnHeaderRendererComponent(xyTable, model.getColumnName(i), i, false);

			rendererPane.paintComponent(g2, header, xyTableColumnHeader, x, 0, columnWidth + 1, columnHeaderHeight, true);
			x += columnWidth;
		}

		if (x < xMax) {
			Component header = columnCellRenderer.getXYTableColumnHeaderRendererComponent(xyTable, null, -1, false);
			rendererPane.paintComponent(g2, header, xyTableColumnHeader, x, 0, xMax - x + 1, columnHeaderHeight, true);
		}

		g2.dispose();
	}

	private void redrawList() {
		xyTableColumnHeader.revalidate();
		xyTableColumnHeader.repaint();
	}

	private class MyPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			redrawList();
		}
	}
}
