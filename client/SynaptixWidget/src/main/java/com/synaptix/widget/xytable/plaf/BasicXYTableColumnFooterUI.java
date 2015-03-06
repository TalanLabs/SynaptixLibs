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
import com.synaptix.widget.xytable.JXYTableColumnFooter;
import com.synaptix.widget.xytable.XYTableColumnFooterRenderer;
import com.synaptix.widget.xytable.XYTableModel;

public class BasicXYTableColumnFooterUI extends XYTableColumnFooterUI {

	public static ComponentUI createUI(JComponent h) {
		return new BasicXYTableColumnFooterUI();
	}

	private JXYTableColumnFooter xyTableColumnFooter;

	private JXYTable xyTable;

	private CellRendererPane rendererPane;

	private PropertyChangeListener propertyChangeListener;

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		xyTableColumnFooter = (JXYTableColumnFooter) c;
		xyTable = xyTableColumnFooter.getXYTable();

		rendererPane = new CellRendererPane();
		c.add(rendererPane);

		propertyChangeListener = new MyPropertyChangeListener();
		xyTable.addPropertyChangeListener(propertyChangeListener);
		xyTableColumnFooter.addPropertyChangeListener(propertyChangeListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		c.remove(rendererPane);

		xyTable.removePropertyChangeListener(propertyChangeListener);
		xyTableColumnFooter.removePropertyChangeListener(propertyChangeListener);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		int columnWidth = xyTableColumnFooter.getColumnWidth();
		int columnFooterHeight = xyTableColumnFooter.getColumnFooterHeight();
		int columnCount = xyTableColumnFooter.getColumnCount();
		return new Dimension(columnCount * columnWidth + 1, columnFooterHeight);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);

		Rectangle clip = g.getClipBounds();
		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width, clip.y + clip.height);

		int cMin = xyTableColumnFooter.columnAtPoint(upperLeft);
		if (cMin == -1) {
			cMin = 0;
		}
		int cMax = xyTableColumnFooter.columnAtPoint(lowerRight);
		if (cMax == -1) {
			cMax = xyTableColumnFooter.getColumnCount() - 1;
		}

		Graphics2D g2 = (Graphics2D) g.create();

		GraphicsHelper.activeAntiAliasing(g2);

		if (xyTableColumnFooter.getColumnFooterRenderer() != null) {
			paintColumnFooter(g2, cMin, cMax, lowerRight.x);
		}

		g2.dispose();

		rendererPane.removeAll();
	}

	protected void paintColumnFooter(Graphics g, int cMin, int cMax, int xMax) {
		Graphics2D g2 = (Graphics2D) g.create();

		XYTableModel model = xyTable.getModel();

		int columnWidth = xyTableColumnFooter.getColumnWidth();
		int columnFooterHeight = xyTableColumnFooter.getColumnFooterHeight();
		XYTableColumnFooterRenderer columnCellRenderer = xyTableColumnFooter.getColumnFooterRenderer();
		int x = cMin * columnWidth;
		for (int i = cMin; i <= cMax; i++) {
			Component header = columnCellRenderer.getXYTableColumnFooterRendererComponent(xyTable, model.getColumnValue(i), i, false);

			rendererPane.paintComponent(g2, header, xyTableColumnFooter, x, 0, columnWidth + 1, columnFooterHeight, true);

			x += columnWidth;
		}

		if (x < xMax) {
			Component header = columnCellRenderer.getXYTableColumnFooterRendererComponent(xyTable, null, -1, false);

			rendererPane.paintComponent(g2, header, xyTableColumnFooter, x, 0, xMax - x + 1, columnFooterHeight, true);
		}

		g2.dispose();
	}

	private void redrawList() {
		xyTableColumnFooter.revalidate();
		xyTableColumnFooter.repaint();
	}

	private class MyPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			redrawList();
		}
	}
}
