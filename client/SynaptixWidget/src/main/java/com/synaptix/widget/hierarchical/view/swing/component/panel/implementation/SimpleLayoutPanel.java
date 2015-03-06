package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JViewport;

/*protected*/class SimpleLayoutPanel extends JPanel {

	private static final long serialVersionUID = -7918976106929962992L;

	private MyLayout layout;

	private boolean useDays;

	private boolean useRows;

	private int nbDays;

	private int nbRows;

	public SimpleLayoutPanel(int cellWidth, boolean useDays, int cellHeight, boolean useRows) {
		super();

		this.layout = new MyLayout(cellWidth, cellHeight);
		setLayout(this.layout);

		this.useDays = useDays;
		this.useRows = useRows;
	}

	public void setNbDays(int nbDays) {
		this.nbDays = nbDays;
	}

	public void setNbRows(int nbRows) {
		this.nbRows = nbRows;
	}

	public void setWidth(final int width) {
		this.layout.cellWidth = width;
	}

	private class MyLayout implements LayoutManager {

		private int cellWidth;

		private int cellHeight;

		public MyLayout(int cellWidth, int cellHeight) {
			super();

			this.cellWidth = cellWidth;
			this.cellHeight = cellHeight;
		}

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public void layoutContainer(Container parent) {
			if (parent.getComponentCount() == 1) {
				int w = useDays ? cellWidth * nbDays : cellWidth;
				int h = useRows ? cellHeight * nbRows : cellHeight;
				if (parent.getParent() instanceof JViewport) {
					JViewport vp = (JViewport) parent.getParent();
					Dimension d = vp.getViewSize();
					w = Math.max(w, d.width);
					h = Math.max(h, d.height);
				}
				parent.getComponent(0).setBounds(0, 0, w, h);
			}
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return preferredLayoutSize(parent);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			return new Dimension(useDays ? cellWidth * nbDays : cellWidth, useRows ? cellHeight * nbRows : cellHeight);
		}
	}
}
