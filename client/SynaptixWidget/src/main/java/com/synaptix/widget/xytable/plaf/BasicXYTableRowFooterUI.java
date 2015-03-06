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
import com.synaptix.widget.xytable.JXYTableRowFooter;
import com.synaptix.widget.xytable.XYTableModel;
import com.synaptix.widget.xytable.XYTableRowFooterRenderer;

public class BasicXYTableRowFooterUI extends XYTableRowFooterUI {

	public static ComponentUI createUI(JComponent h) {
		return new BasicXYTableRowFooterUI();
	}

	private JXYTableRowFooter xyTableRowFooter;

	private JXYTable xyTable;

	private CellRendererPane rendererPane;

	private PropertyChangeListener propertyChangeListener;

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		xyTableRowFooter = (JXYTableRowFooter) c;
		xyTable = xyTableRowFooter.getXYTable();

		rendererPane = new CellRendererPane();
		c.add(rendererPane);

		propertyChangeListener = new MyPropertyChangeListener();
		xyTable.addPropertyChangeListener(propertyChangeListener);
		xyTableRowFooter.addPropertyChangeListener(propertyChangeListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		c.remove(rendererPane);

		xyTable.removePropertyChangeListener(propertyChangeListener);
		xyTableRowFooter.removePropertyChangeListener(propertyChangeListener);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		int rowHeight = xyTableRowFooter.getRowHeight();
		int rowFooterWidth = xyTableRowFooter.getRowFooterWidth();
		int rowCount = xyTableRowFooter.getRowCount();
		return new Dimension(rowFooterWidth, rowHeight * rowCount + 1);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);

		Rectangle clip = g.getClipBounds();
		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width, clip.y + clip.height);

		int rMin = xyTableRowFooter.rowAtPoint(upperLeft);
		if (rMin == -1) {
			rMin = 0;
		}
		int rMax = xyTableRowFooter.rowAtPoint(lowerRight);
		if (rMax == -1) {
			rMax = xyTableRowFooter.getRowCount() - 1;
		}

		Graphics2D g2 = (Graphics2D) g.create();

		GraphicsHelper.activeAntiAliasing(g2);

		if (xyTableRowFooter.getRowFooterRenderer() != null) {
			paintRowFooter(g2, rMin, rMax, lowerRight.y);
		}

		g2.dispose();

		rendererPane.removeAll();
	}

	protected void paintRowFooter(Graphics g, int rMin, int rMax, int yMax) {
		Graphics2D g2 = (Graphics2D) g.create();

		XYTableModel model = xyTable.getModel();

		int rowHeight = xyTableRowFooter.getRowHeight();
		int rowFooterWidth = xyTableRowFooter.getRowFooterWidth();
		XYTableRowFooterRenderer cellRenderer = xyTableRowFooter.getRowFooterRenderer();
		int y = rMin * rowHeight;
		for (int i = rMin; i <= rMax; i++) {
			Component header = cellRenderer.getXYTableRowFooterRendererComponent(xyTable, model.getRowValue(i), i, false);

			rendererPane.paintComponent(g2, header, xyTableRowFooter, 0, y, rowFooterWidth, rowHeight + 1, true);

			y += rowHeight;
		}

		if (y < yMax) {
			Component header = cellRenderer.getXYTableRowFooterRendererComponent(xyTable, null, -1, false);
			rendererPane.paintComponent(g2, header, xyTableRowFooter, 0, y, rowFooterWidth, yMax - y + 1, true);
		}

		g2.dispose();
	}

	private void redrawList() {
		xyTableRowFooter.revalidate();
		xyTableRowFooter.repaint();
	}

	private class MyPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			redrawList();
		}
	}
}
