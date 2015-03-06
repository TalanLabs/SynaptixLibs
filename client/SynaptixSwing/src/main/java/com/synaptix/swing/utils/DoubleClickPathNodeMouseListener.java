package com.synaptix.swing.utils;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;

import com.synaptix.swing.path.JPath;

public class DoubleClickPathNodeMouseListener extends MouseAdapter {

	private JPath path;

	private Action action;

	public DoubleClickPathNodeMouseListener(JPath path, Action action) {
		super();

		this.path = path;
		this.action = action;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
				&& path.getPathSelectionModel().isSelectionNode()
				&& action.isEnabled()) {
			action.actionPerformed(new ActionEvent(path,
					ActionEvent.ACTION_PERFORMED, ""));
		}
	}
}
