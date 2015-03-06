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
import com.synaptix.widget.xytable.JXYTableRowHeader;
import com.synaptix.widget.xytable.XYTableModel;
import com.synaptix.widget.xytable.XYTableRowHeaderRenderer;

public class BasicXYTableRowHeaderUI extends XYTableRowHeaderUI {

	public static ComponentUI createUI(JComponent h) {
		return new BasicXYTableRowHeaderUI();
	}

	private JXYTableRowHeader xyTableRowHeader;

	private JXYTable xyTable;

	private CellRendererPane rendererPane;

	private PropertyChangeListener propertyChangeListener;

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		xyTableRowHeader = (JXYTableRowHeader) c;
		xyTable = xyTableRowHeader.getXYTable();

		rendererPane = new CellRendererPane();
		c.add(rendererPane);

		propertyChangeListener = new MyPropertyChangeListener();
		xyTable.addPropertyChangeListener(propertyChangeListener);
		xyTableRowHeader.addPropertyChangeListener(propertyChangeListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		c.remove(rendererPane);

		xyTable.removePropertyChangeListener(propertyChangeListener);
		xyTableRowHeader.removePropertyChangeListener(propertyChangeListener);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		int rowHeight = xyTableRowHeader.getRowHeight();
		int rowHeaderWidth = xyTableRowHeader.getRowHeaderWidth();
		int rowCount = xyTableRowHeader.getRowCount();
		return new Dimension(rowHeaderWidth, rowHeight * rowCount + 1);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);

		Rectangle clip = g.getClipBounds();
		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width, clip.y + clip.height);

		int rMin = xyTableRowHeader.rowAtPoint(upperLeft);
		if (rMin == -1) {
			rMin = 0;
		}
		int rMax = xyTableRowHeader.rowAtPoint(lowerRight);
		if (rMax == -1) {
			rMax = xyTableRowHeader.getRowCount() - 1;
		}

		Graphics2D g2 = (Graphics2D) g.create();

		GraphicsHelper.activeAntiAliasing(g2);

		if (xyTableRowHeader.getRowHeaderRenderer() != null) {
			paintRowHeader(g2, rMin, rMax, lowerRight.y);
		}

		g2.dispose();

		rendererPane.removeAll();
	}

	protected void paintRowHeader(Graphics g, int rMin, int rMax, int yMax) {
		Graphics2D g2 = (Graphics2D) g.create();

		XYTableModel model = xyTable.getModel();

		int rowHeight = xyTableRowHeader.getRowHeight();
		int rowHeaderWidth = xyTableRowHeader.getRowHeaderWidth();
		XYTableRowHeaderRenderer cellRenderer = xyTableRowHeader.getRowHeaderRenderer();
		int y = rMin * rowHeight;
		for (int i = rMin; i <= rMax; i++) {
			Component header = cellRenderer.getXYTableRowHeaderRendererComponent(xyTable, model.getRowName(i), i, false);
			rendererPane.paintComponent(g2, header, xyTableRowHeader, 0, y, rowHeaderWidth, rowHeight + 1, true);
			y += rowHeight;
		}

		if (y < yMax) {
			Component header = cellRenderer.getXYTableRowHeaderRendererComponent(xyTable, "", -1, false);
			rendererPane.paintComponent(g2, header, xyTableRowHeader, 0, y, rowHeaderWidth, yMax - y + 1, true);
		}

		g2.dispose();
	}

	private void redrawList() {
		xyTableRowHeader.revalidate();
		xyTableRowHeader.repaint();
	}

	private class MyPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			redrawList();
		}
	}
}
