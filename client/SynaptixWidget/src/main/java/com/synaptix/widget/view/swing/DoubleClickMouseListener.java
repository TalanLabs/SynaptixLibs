package com.synaptix.widget.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;

public class DoubleClickMouseListener extends MouseAdapter {

	private Action action;

	public DoubleClickMouseListener(Action action) {
		super();
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			action.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "", e.getModifiers()));
		}
	}
}
