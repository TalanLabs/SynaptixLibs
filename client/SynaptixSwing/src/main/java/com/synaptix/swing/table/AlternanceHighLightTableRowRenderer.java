package com.synaptix.swing.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import com.synaptix.swing.table.TableRowRenderer;

public class AlternanceHighLightTableRowRenderer implements TableRowRenderer {

	private static final Color color = new Color(200, 227, 255);
	
	public Component getTableRowRenderer(Component c, JTable table,
			boolean isSelected, boolean hasFocus, int row, int col) {
		
		if (isSelected) {
			c.setBackground(table.getSelectionBackground());
			c.setForeground(table.getSelectionForeground());
		} else {
			c.setForeground(Color.BLACK);
			if (row % 2 == 0) {
				c.setBackground(Color.WHITE);
			} else {
				c.setBackground(color);
			}
		}
		
		return c;
	}

}
