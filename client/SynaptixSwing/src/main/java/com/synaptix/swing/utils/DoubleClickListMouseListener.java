package com.synaptix.swing.utils;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JList;

public class DoubleClickListMouseListener extends MouseAdapter {

	private JList list;

	private Action action;

	public DoubleClickListMouseListener(JList list, Action action) {
		super();

		this.list = list;
		this.action = action;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
				&& list.getSelectedIndex() != -1 && action.isEnabled()) {
			action.actionPerformed(new ActionEvent(list,
					ActionEvent.ACTION_PERFORMED, ""));
		}
	}
}
