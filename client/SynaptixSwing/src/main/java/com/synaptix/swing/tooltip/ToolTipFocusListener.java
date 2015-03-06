package com.synaptix.swing.tooltip;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;

import javax.swing.JWindow;

import com.synaptix.swing.AWTUtilitiesWrapper;

public class ToolTipFocusListener extends AbstractToolTipFocusListener {

	private boolean enabled;

	public ToolTipFocusListener(String promptText) {
		super();

		this.toolTip = new JBlueToolTip(promptText);
		this.enabled = true;
	}

	public JDefaultToolTip getToolTip() {
		return toolTip;
	}

	@Override
	protected Point getPosition() {
		return new Point(0, field.getHeight());
	}

	@Override
	public void showWindow() {
		hideWindow();

		if (enabled) {
			field.addHierarchyBoundsListener(hierachyBoundsListener);

			if (parent == null) {
				Component c = field;
				while (!(c instanceof Window) && c != null) {
					c = c.getParent();
				}
				parent = (Window) c;
			}

			window = new JWindow(parent);
			window.addComponentListener(componentListener);
			AWTUtilitiesWrapper.setWindowOpaque(window, false);
			window.getContentPane().add(toolTip);
			window.pack();
			computeLocation();
			window.setVisible(true);
		}
	}

	@Override
	public void hideWindow() {
		if (window != null) {
			window.setVisible(false);
			window.removeComponentListener(componentListener);
			window = null;
			field.removeHierarchyBoundsListener(hierachyBoundsListener);
		}
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		if (!enabled) {
			hideWindow();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
}