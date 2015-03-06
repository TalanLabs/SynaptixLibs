package com.synaptix.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

public class GridFlowLayout implements LayoutManager {

	public static final int HORIZONTAL = 0;

	public static final int VERTICAL = 1;

	private int direction;

	public GridFlowLayout() {
		this(HORIZONTAL);
	}

	public GridFlowLayout(int direction) {
		this.direction = direction;
	}

	private List<List<Component>> buildLineComponentListHorizontal(
			Container parent) {
		Insets insets = parent.getInsets();
		Dimension size = parent.getSize();

		int width = size.width - insets.left - insets.right;

		int xCurrent = 0;
		List<List<Component>> lineComponentList = new ArrayList<List<Component>>();
		boolean premier = true;
		for (int i = 0, c = parent.getComponentCount(); i < c; i++) {
			Component m = parent.getComponent(i);
			if (m.isVisible()) {
				Dimension componentPreferredSize = m.getPreferredSize();
				if (premier || width <= xCurrent + componentPreferredSize.width) {
					lineComponentList.add(new ArrayList<Component>());
					xCurrent = 0;
					premier = false;
				}

				lineComponentList.get(lineComponentList.size() - 1).add(m);
				xCurrent += componentPreferredSize.width;
			}
		}

		return lineComponentList;
	}

	private void layoutContainerHorizontal(Container parent) {
		Insets insets = parent.getInsets();
		Dimension size = parent.getSize();

		int width = size.width - insets.left - insets.right;

		List<List<Component>> lineComponentList = buildLineComponentListHorizontal(parent);

		int y = insets.top;
		for (List<Component> lineComponent : lineComponentList) {
			int x = 0;
			int h = 0;
			int plus = 0;

			for (Component m : lineComponent) {
				Dimension componentPreferredSize = m.getPreferredSize();
				h = Math.max(h, componentPreferredSize.height);
				x += componentPreferredSize.width;
			}

			if (width > x) {
				int d = width - x;
				plus = d / lineComponent.size();
			}

			x = insets.left;
			for (Component m : lineComponent) {
				Dimension componentPreferredSize = m.getPreferredSize();
				m.setBounds(x, y, componentPreferredSize.width + plus, h);
				x += componentPreferredSize.width + plus;
			}
			y += h;
		}
	}

	private List<List<Component>> buildLineComponentListVertical(
			Container parent) {
		Insets insets = parent.getInsets();
		Dimension size = parent.getSize();

		int height = size.height - insets.top - insets.bottom;

		int yCurrent = 0;
		List<List<Component>> lineComponentList = new ArrayList<List<Component>>();
		boolean premier = true;
		for (int i = 0, c = parent.getComponentCount(); i < c; i++) {
			Component m = parent.getComponent(i);
			if (m.isVisible()) {
				Dimension componentPreferredSize = m.getPreferredSize();
				if (premier
						|| height <= yCurrent + componentPreferredSize.height) {
					lineComponentList.add(new ArrayList<Component>());
					yCurrent = 0;
					premier = false;
				}

				lineComponentList.get(lineComponentList.size() - 1).add(m);
				yCurrent += componentPreferredSize.height;
			}
		}

		return lineComponentList;
	}

	private void layoutContainerVertical(Container parent) {
		Insets insets = parent.getInsets();
		Dimension size = parent.getSize();

		int height = size.height - insets.top - insets.bottom;

		List<List<Component>> lineComponentList = buildLineComponentListVertical(parent);

		int x = insets.left;
		for (List<Component> lineComponent : lineComponentList) {
			int y = 0;
			int w = 0;
			int plus = 0;

			for (Component m : lineComponent) {
				Dimension componentPreferredSize = m.getPreferredSize();
				w = Math.max(w, componentPreferredSize.width);
				y += componentPreferredSize.height;
			}

			if (height > y) {
				int d = height - y;
				plus = d / lineComponent.size();
			}

			y = insets.left;
			for (Component m : lineComponent) {
				Dimension componentPreferredSize = m.getPreferredSize();
				m.setBounds(x, y, w, componentPreferredSize.height + plus);
				y += componentPreferredSize.height + plus;
			}
			x += w;
		}
	}

	public void layoutContainer(Container parent) {
		if (direction == HORIZONTAL) {
			layoutContainerHorizontal(parent);
		} else {
			layoutContainerVertical(parent);
		}
	}

	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	private Dimension buildPreferredLayoutSizeDefault(Container parent) {
		Insets insets = parent.getInsets();

		Dimension pref = new Dimension(0, 0);

		for (int i = 0, c = parent.getComponentCount(); i < c; i++) {
			Component m = parent.getComponent(i);
			if (m.isVisible()) {
				Dimension componentPreferredSize = m.getPreferredSize();
				if (direction == HORIZONTAL) {
					pref.width += componentPreferredSize.width;
					pref.height = Math.max(pref.height,
							componentPreferredSize.height);
				} else {
					pref.width = Math.max(pref.width,
							componentPreferredSize.width);
					pref.height += componentPreferredSize.height;
				}
			}
		}

		pref.width += insets.left + insets.right;
		pref.height += insets.top + insets.bottom;

		return pref;
	}

	private Dimension buildPreferredLayoutSizeHorizontal(Container parent) {
		Insets insets = parent.getInsets();

		Dimension pref = new Dimension(0, 0);

		List<List<Component>> lineComponentList = buildLineComponentListHorizontal(parent);

		int widthMax = 0;
		int heightMax = 0;
		for (List<Component> lineComponent : lineComponentList) {
			int w = 0;
			int h = 0;
			for (Component m : lineComponent) {
				Dimension componentPreferredSize = m.getPreferredSize();
				w += componentPreferredSize.width;
				h = Math.max(h, componentPreferredSize.height);
			}
			widthMax = Math.max(widthMax, w);
			heightMax += h;
		}

		pref.width = widthMax + insets.left + insets.right;
		pref.height = heightMax + insets.top + insets.bottom;

		return pref;
	}

	private Dimension buildPreferredLayoutSizeVertical(Container parent) {
		Insets insets = parent.getInsets();

		Dimension pref = new Dimension(0, 0);

		List<List<Component>> lineComponentList = buildLineComponentListVertical(parent);

		int widthMax = 0;
		int heightMax = 0;
		for (List<Component> lineComponent : lineComponentList) {
			int w = 0;
			int h = 0;
			for (Component m : lineComponent) {
				Dimension componentPreferredSize = m.getPreferredSize();
				w = Math.max(w, componentPreferredSize.width);
				h += componentPreferredSize.height;

			}
			widthMax += w;
			heightMax = Math.max(heightMax, h);
		}

		pref.width = widthMax + insets.left + insets.right;
		pref.height = heightMax + insets.top + insets.bottom;

		return pref;
	}

	public Dimension preferredLayoutSize(Container parent) {
		Dimension size = parent.getSize();

		Dimension pref;
		if (size == null || (size.width == 0 && size.height == 0)) {
			pref = buildPreferredLayoutSizeDefault(parent);
		} else {
			if (direction == HORIZONTAL) {
				pref = buildPreferredLayoutSizeHorizontal(parent);
			} else {
				pref = buildPreferredLayoutSizeVertical(parent);
			}
		}
		return pref;
	}

	public void addLayoutComponent(String name, Component comp) {
	}

	public void removeLayoutComponent(Component comp) {
	}
}
