package com.synaptix.swing.tooltip;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;

import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

abstract class AbstractToolTipFocusListener implements FocusListener {

	protected Window parent;

	protected JWindow window;

	protected final HierarchyBoundsListener hierachyBoundsListener;

	protected final ComponentListener componentListener;

	protected Component field;

	protected Component realField;

	protected JDefaultToolTip toolTip;

	public AbstractToolTipFocusListener() {
		super();

		this.componentListener = new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				computeLocation();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				computeLocation();
			}
		};

		hierachyBoundsListener = new HierarchyBoundsListener() {
			@Override
			public void ancestorMoved(HierarchyEvent e) {
				computeLocation();
			}

			@Override
			public void ancestorResized(HierarchyEvent e) {
				computeLocation();
			}
		};
	}

	protected void computeLocation() {
		if (field != null && window != null) {
			boolean visible = true;
			Component c = field.getParent();
			Point point = new Point(0, 0);
			while (c != null && visible) {
				Point p = SwingUtilities.convertPoint(field, point, c);
				visible = p.x >= 0 && p.y >= 0 && p.x + field.getWidth() <= c.getWidth() && p.y + field.getHeight() <= c.getHeight();
				c = c.getParent();
			}

			if (visible) {
				point = new Point(getPosition());
				SwingUtilities.convertPointToScreen(point, field);
				window.setLocation(point);
				if (!window.isVisible()) {
					window.setVisible(true);
				}
			} else if (window.isVisible()) {
				window.setVisible(false);
			}
		}
	}

	protected abstract Point getPosition();

	@Override
	public void focusLost(FocusEvent e) {
		hideWindow();
		field = null;
		realField = null;
	}

	@Override
	public void focusGained(FocusEvent e) {
		realField = e.getComponent();
		field = e.getComponent();
		if (field.getParent() != null && field.getParent().getParent() instanceof JScrollPane) {
			field = field.getParent().getParent();
		}
		showWindow();
	}

	/**
	 * Show a window tooltips
	 */
	public abstract void showWindow();

	/**
	 * Hide a window tooltips
	 */
	public abstract void hideWindow();
}
