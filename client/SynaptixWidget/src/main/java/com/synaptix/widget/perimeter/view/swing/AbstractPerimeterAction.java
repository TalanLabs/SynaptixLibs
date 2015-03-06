package com.synaptix.widget.perimeter.view.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 * Special button used in perimeters that has two methods called when the perimeter refreshes or a value has been changed
 */
public abstract class AbstractPerimeterAction extends AbstractAction {

	private static final long serialVersionUID = -5134597635678973501L;

	public AbstractPerimeterAction(String text) {
		this(text, null);
	}

	public AbstractPerimeterAction(String text, ImageIcon icon) {
		super(text, icon);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	/**
	 * Fired when refreshing the perimeter
	 */
	public void fireRefreshAction() {
	}

	/**
	 * Fired when a value has changed
	 */
	public void fireValuesChangedAction() {
	}
}
