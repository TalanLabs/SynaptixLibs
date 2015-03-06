package com.synaptix.widget.view.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

public class CenterLayout implements LayoutManager, Serializable {

	private static final long serialVersionUID = 7043767686153723828L;

	public void addLayoutComponent(String name, Component comp) {
	}

	public void removeLayoutComponent(Component comp) {
	}

	public Dimension preferredLayoutSize(Container container) {
		Component c = container.getComponent(0);
		if (c != null) {
			Dimension size = c.getPreferredSize();
			Insets insets = container.getInsets();

			return new Dimension(size.width + insets.left + insets.right, size.height + insets.top + insets.bottom);
		} else {
			return new Dimension(0, 0);
		}
	}

	public Dimension minimumLayoutSize(Container cont) {
		return preferredLayoutSize(cont);
	}

	public void layoutContainer(Container container) {
		if (container.getComponentCount() > 0) {
			Component c = container.getComponent(0);
			Dimension pref = c.getPreferredSize();
			int containerWidth = container.getWidth();
			int containerHeight = container.getHeight();
			Insets containerInsets = container.getInsets();

			containerWidth -= containerInsets.left + containerInsets.right;
			containerHeight -= containerInsets.top + containerInsets.bottom;

			int left = (containerWidth - pref.width) / 2 + containerInsets.left;
			int right = (containerHeight - pref.height) / 2 + containerInsets.top;

			c.setBounds(left, right, pref.width, pref.height);
		}
	}
}