package com.synaptix.swing.utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

public class PopupMenuMouseListener extends MouseAdapter {

	private JPopupMenu popupMenu;

	public PopupMenuMouseListener(JPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			if (e.getComponent() instanceof JTable) {
				JTable table = (JTable) e.getComponent();
				int row = table.rowAtPoint(e.getPoint());
				boolean found = false;
				int i = 0;
				int[] selectedRows = table.getSelectedRows();
				int max = selectedRows.length;
				while ((!found) && (i < max)) {
					if (selectedRows[i] == row) {
						found = true;
					}
					i++;
				}
				if (!found) {
					table.setRowSelectionInterval(row, row);
				}
			}
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
