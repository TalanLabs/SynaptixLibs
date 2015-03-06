package com.synaptix.swing.utils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;

import com.synaptix.swing.JChoixPanel;

public class DoubleClickChoixMouseListener<E extends Component> extends
		MouseAdapter {

	private JChoixPanel<E> choix;

	private Action action;

	public DoubleClickChoixMouseListener(JChoixPanel<E> choix, Action action) {
		super();

		this.choix = choix;
		this.action = action;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
				&& choix.getSelectedComponent() != null && action.isEnabled()) {
			action.actionPerformed(new ActionEvent(choix,
					ActionEvent.ACTION_PERFORMED, ""));
		}
	}
}
