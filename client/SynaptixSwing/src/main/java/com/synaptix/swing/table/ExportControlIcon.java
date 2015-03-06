/*
 * $Id: ColumnControlIcon.java,v 1.5 2006/05/14 08:19:44 dmouse Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.synaptix.swing.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Icon class for rendering icon which indicates user control of column
 * visibility.
 * 
 * @author Amy Fowler
 * @version 1.0
 */
public class ExportControlIcon implements Icon {

	private int width = 10;

	private int height = 10;

	private static Image image;

	static {
		image = new ImageIcon(ExportControlIcon.class
				.getResource("/com/synaptix/swing/table/res/icon_excel.gif")).getImage(); //$NON-NLS-1$
	}

	public ExportControlIcon() {
	}

	public int getIconWidth() {
		return width;
	}

	public int getIconHeight() {
		return height;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Color color = c.getForeground();
		g.setColor(color);

		g.drawImage(image, x, y, getIconWidth(), getIconHeight(),null);
	}

	public static void main(String args[]) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel label = new JLabel(new ExportControlIcon());
		frame.getContentPane().add(BorderLayout.CENTER, label);
		frame.pack();
		frame.setVisible(true);
	}

}
