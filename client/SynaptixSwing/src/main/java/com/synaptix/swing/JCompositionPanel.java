package com.synaptix.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JLayeredPane;

public class JCompositionPanel extends JLayeredPane {

	private static final long serialVersionUID = -9100049250111968121L;

	public JCompositionPanel() {
		super();

		this.setLayout(new SimpleLayout());
	}

	private final class SimpleLayout implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			Dimension res = null;
			if (parent.getComponentCount() > 0) {
				int w = 0;
				int h = 0;
				for (Component c : parent.getComponents()) {
					Dimension d = c.getPreferredSize();
					w = Math.max(w, d.width);
					h = Math.max(h, d.height);
				}
				res = new Dimension(w, h);
			} else {
				res = new Dimension();
			}
			return res;
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			Dimension res = null;
			if (parent.getComponentCount() > 0) {
				int w = 0;
				int h = 0;
				for (Component c : parent.getComponents()) {
					Dimension d = c.getMinimumSize();
					w = Math.max(w, d.width);
					h = Math.max(h, d.height);
				}
				res = new Dimension(w, h);
			} else {
				res = new Dimension();
			}
			return res;
		}

		@Override
		public void layoutContainer(Container parent) {
			Dimension size = parent.getSize();
			for (Component c : parent.getComponents()) {
				c.setBounds(0, 0, size.width, size.height);
			}
		}
	}
}
