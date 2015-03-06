package com.synaptix.widget.pathbar.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.utils.GraphicsHelper;
import com.synaptix.widget.pathbar.JPathBar;
import com.synaptix.widget.pathbar.PathBarCellRenderer;

public class BasicPathBarUI extends PathBarUI {

	public static ComponentUI createUI(JComponent h) {
		return new BasicPathBarUI();
	}

	private JPathBar pathBar;

	private CellRendererPane rendererPane;

	private Dimension itemDimension;

	private boolean itemDimensionChanged = true;

	private PropertyChangeListener propertyChangeListener;

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		pathBar = (JPathBar) c;

		rendererPane = new CellRendererPane();
		c.add(rendererPane);

		propertyChangeListener = new MyPropertyChangeListener();
		pathBar.addPropertyChangeListener(propertyChangeListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		c.remove(rendererPane);

		pathBar.removePropertyChangeListener(propertyChangeListener);
	}

	private Dimension getItemDimension(JPathBar pathBar) {
		if (itemDimensionChanged) {
			itemDimensionChanged = false;

			itemDimension = new Dimension();
			int itemWidth = pathBar.getFixedItemWidth();
			int itemHeight = pathBar.getFixedItemHeight();
			ListModel model = pathBar.getModel();
			int size = model.getSize();
			PathBarCellRenderer renderer = pathBar.getCellRenderer();
			for (int i = 0; i < size; i++) {
				Component c = renderer.getPathBarCellRendererComponent(pathBar, model.getElementAt(i), i, true);
				rendererPane.add(c);
				Dimension cellSize = c.getPreferredSize();
				if (itemHeight == -1) {
					itemDimension.height = Math.max(itemDimension.height, cellSize.height);
				}
			}
			if (itemHeight >= 0) {
				itemDimension.height = itemHeight;
			}

			int diff = itemDimension.height;

			for (int i = 0; i < size; i++) {
				Component c = renderer.getPathBarCellRendererComponent(pathBar, model.getElementAt(i), i, true);
				rendererPane.add(c);
				Dimension cellSize = c.getPreferredSize();
				if (itemWidth == -1) {
					itemDimension.width = Math.max(itemDimension.width, cellSize.width + diff);
				}
			}
			if (itemWidth >= 0) {
				itemDimension.width = itemWidth;
			}
		}
		rendererPane.removeAll();
		return itemDimension;
	}

	private Dimension getRealItemDimension(JPathBar pathBar) {
		Dimension itemDimension = getItemDimension(pathBar);
		int itemWidth = pathBar.getFixedItemWidth();
		int itemHeight = pathBar.getFixedItemHeight();
		ListModel model = pathBar.getModel();
		int size = model.getSize();
		Insets insets = pathBar.getInsets();
		int h = itemHeight != -1 ? itemHeight : Math.max(itemDimension.height, pathBar.getHeight() - (insets.top + insets.bottom));
		int w = size == 0 ? 0 : itemWidth != -1 ? itemWidth : Math.max(itemDimension.width, ((pathBar.getWidth() - (insets.left + insets.right)) + h * (size - 1) / 2) / size);
		return new Dimension(w, h);
	}

	@Override
	public int indexAtPoint(JPathBar pathBar, Point p) {
		Dimension itemDimension = getRealItemDimension(pathBar);
		int itemWidth = itemDimension.width;
		int diff = itemDimension.height;
		int mItemWidth = diff / 2;
		ListModel model = pathBar.getModel();
		int size = model.getSize();
		Insets insets = pathBar.getInsets();
		int x = p.x - insets.left;
		int a = itemWidth - mItemWidth;
		int r = a == 0 ? 0 : x / a;
		r = r == size && x < r * a + mItemWidth ? r - 1 : r;
		return r >= 0 && r < size ? r : -1;
	}

	@Override
	public Rectangle getCellRect(JPathBar pathBar, int index) {
		Rectangle r = new Rectangle();
		Dimension itemDimension = getRealItemDimension(pathBar);
		int itemWidth = itemDimension.width;
		int itemHeight = itemDimension.height;
		int diff = itemDimension.height;
		int mItemWidth = diff / 2;
		ListModel model = pathBar.getModel();
		int size = model.getSize();
		Insets insets = pathBar.getInsets();
		if (index >= 0 && index < size) {
			r.setBounds(index * (itemWidth - mItemWidth) + insets.left, insets.top, itemWidth, itemHeight);
		}
		return r;
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension itemDimension = getItemDimension(pathBar);
		int itemWidth = itemDimension.width;
		int itemHeight = itemDimension.height;
		int size = pathBar.getModel().getSize();
		int diff = itemDimension.height;
		int mItemWidth = diff / 2;
		Insets insets = pathBar.getInsets();
		return new Dimension(size * itemWidth - (size - 1) * mItemWidth + insets.left + insets.right, itemHeight + insets.top + insets.bottom);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);

		ListModel model = pathBar.getModel();
		int size = model.getSize();

		Rectangle clip = g.getClipBounds();
		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width, clip.y + clip.height);

		int cMin = pathBar.indexAtPoint(upperLeft);
		if (cMin == -1) {
			cMin = 0;
		}
		if (cMin > 0) {
			cMin = cMin - 1;
		}
		int cMax = pathBar.indexAtPoint(lowerRight);
		if (cMax == -1) {
			cMax = size - 1;
		}
		if (cMax < size - 1) {
			cMax = cMax + 1;
		}

		Graphics2D g2 = (Graphics2D) g.create();

		GraphicsHelper.activeAntiAliasing(g2);

		Insets insets = pathBar.getInsets();
		Dimension itemDimension = getRealItemDimension(pathBar);
		int itemWidth = itemDimension.width;
		int itemHeight = itemDimension.height;
		int mItemHeight = itemHeight / 2;
		int rItemHeight = mItemHeight / 2;
		int diff = itemDimension.height;
		int mItemWidth = diff / 2;
		int rItemWidth = mItemWidth / 2;
		Color gridColor = pathBar.getGridColor();
		boolean showGrid = pathBar.isShowGrid();

		PathBarCellRenderer renderer = pathBar.getCellRenderer();
		ListSelectionModel selectionModel = pathBar.getSelectionModel();
		int x = cMin * (itemWidth - mItemHeight) + insets.left;
		int y = insets.top;
		for (int i = cMin; i <= cMax; i++) {
			Path2D p = new Path2D.Float();
			if (i == 0) {
				p.moveTo(x, y + rItemHeight);
				p.curveTo(x, y, x, y, x + rItemWidth, y);
			} else {
				p.moveTo(x, y);
			}
			if (i < size - 1) {
				p.lineTo(x + itemWidth - mItemWidth, y);
				p.lineTo(x + itemWidth, y + mItemHeight);
				p.lineTo(x + itemWidth - mItemWidth, y + itemHeight - 1);
			} else {
				p.lineTo(x + itemWidth - rItemWidth - 1, y);
				p.curveTo(x + itemWidth - 1, y, x + itemWidth - 1, y, x + itemWidth - 1, y + rItemHeight);
				p.lineTo(x + itemWidth - 1, y + itemHeight - rItemHeight - 1);
				p.curveTo(x + itemWidth - 1, y + itemHeight - 1, x + itemWidth - 1, y + itemHeight - 1, x + itemWidth - rItemWidth - 1, y + itemHeight - 1);
			}

			if (i == 0) {
				p.lineTo(x + rItemWidth, y + itemHeight - 1);
				p.curveTo(x, y + itemHeight - 1, x, y + itemHeight - 1, x, y + itemHeight - rItemHeight - 1);
				p.lineTo(x, y + rItemHeight);
			} else {
				p.lineTo(x, y + itemHeight - 1);
				p.lineTo(x + mItemWidth, y + mItemHeight);
				p.lineTo(x, y);
			}

			Graphics2D g22 = (Graphics2D) g2.create();
			g22.clip(p);

			Component cell = renderer.getPathBarCellRendererComponent(pathBar, model.getElementAt(i), i, selectionModel.isSelectedIndex(i));
			rendererPane.paintComponent(g22, cell, pathBar, x, y, itemWidth, itemHeight, true);

			g22.dispose();

			if (showGrid) {
				g2.setColor(gridColor);
				g2.draw(p);
			}

			x += (itemWidth - mItemWidth);

		}

		g2.dispose();

		rendererPane.removeAll();
	}

	private void redrawList() {
		pathBar.revalidate();
		pathBar.repaint();
	}

	private class MyPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ("model".equals(evt.getPropertyName())) {
				itemDimensionChanged = true;
				redrawList();
			} else if ("fixedItemWidth".equals(evt.getPropertyName())) {
				itemDimensionChanged = true;
				redrawList();
			} else if ("fixedItemHeight".equals(evt.getPropertyName())) {
				itemDimensionChanged = true;
				redrawList();
			} else if ("cellRenderer".equals(evt.getPropertyName())) {
				itemDimensionChanged = true;
				redrawList();
			} else {
				redrawList();
			}
		}
	}
}
