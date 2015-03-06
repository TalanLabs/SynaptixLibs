package com.synaptix.swing.utils;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JTable;

/**
 * Permet de gerer le double clique dans un tableau
 * 
 * @author PGAE02801
 * 
 */
public class DoubleClickTableMouseListener extends MouseAdapter {

	private JTable table;

	private Action action;

	public DoubleClickTableMouseListener(JTable table, Action action) {
		super();

		this.table = table;
		this.action = action;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
				&& table.getSelectedRowCount() == 1 && action.isEnabled()) {
			action.actionPerformed(new ActionEvent(table,
					ActionEvent.ACTION_PERFORMED, ""));
		}
	}
}