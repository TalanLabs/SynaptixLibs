/* 
 * Copyright (c) 2002, Cameron Zemek
 * 
 * This file is part of JGrid.
 * 
 * JGrid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * JGrid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sf.jeppers.grid;

import java.awt.Color;
import java.awt.Font;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

/**
 * GridUI for use with row and column headers.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class BasicGridHeaderUI extends BasicGridUI {
	private static boolean installedHeader;
	protected transient JGrid gridHeader;

	public BasicGridHeaderUI() {
	}

	public static ComponentUI createUI(JComponent jComponent) {
		return new BasicGridHeaderUI();
	}

	public void installUI(JComponent jComponent) {
		grid = (JGrid) jComponent;
		rendererPane = new CellRendererPane();
		grid.add(rendererPane);
		gridHeader = (JGrid) jComponent;
		jComponent.setOpaque(false);
		LookAndFeel.installColorsAndFont(
			jComponent,
			"TableHeader.background",
			"TableHeader.foreground",
			"TableHeader.font");
		installDefaults();
		installListeners();
		installKeyboardActions();
	}

	protected void installDefaults() {
		String string = UIManager.getLookAndFeel().getName();
		Color defaultGridColor = UIManager.getColor("Table.gridColor");
		Color defaultForegroundColor = UIManager.getColor("TableHeader.foreground");
		Color defaultBackgroundColor = UIManager.getColor("TableHeader.background");
		Font defaultGridFont = UIManager.getFont("Table.font");
		Border defaultGridBorder = UIManager.getBorder("TableHeader.border");
		Color defaultSelectionForegroundColor = defaultForegroundColor.brighter();
		Color defaultSelectionBackgroundColor = defaultBackgroundColor;
		Color defaultFocusForegroundColor = defaultForegroundColor.brighter();
		Color defaultFocusBackgroundColor = defaultBackgroundColor.brighter();
		if (!installedHeader) {
			UIManager.getDefaults().put("GridHeader.gridColor", defaultGridColor);
			UIManager.getDefaults().put("GridHeader.foreground", defaultForegroundColor);
			UIManager.getDefaults().put("GridHeader.background", defaultBackgroundColor);
			UIManager.getDefaults().put(
				"GridHeader.selectionForegroundColor",
				defaultSelectionForegroundColor);
			UIManager.getDefaults().put(
				"GridHeader.selectionBackgroundColor",
				defaultSelectionBackgroundColor);
			UIManager.getDefaults().put(
				"GridHeader.focusForegroundColor",
				defaultFocusForegroundColor);
			UIManager.getDefaults().put(
				"GridHeader.focusBackgroundColor",
				defaultFocusBackgroundColor);
			UIManager.getDefaults().put("GridHeader.border", defaultGridBorder);
			UIManager.getDefaults().put("GridHeader.font", defaultGridFont);
		}
		Color foregroundColor = gridHeader.getForeground();
		Color backgroundColor = gridHeader.getBackground();
		Font gridFont = gridHeader.getFont();
		Border gridBorder = gridHeader.getBorder();
		Color gridColor = gridHeader.getGridColor();
		Color selectionForegroundColor = gridHeader.getSelectionForegroundColor();
		Color selectionBackgroundColor = gridHeader.getSelectionBackgroundColor();
		Color focusForegroundColor = gridHeader.getFocusForegroundColor();
		Color focusBackgroundColor = gridHeader.getFocusBackgroundColor();
		if (foregroundColor == null || foregroundColor instanceof UIResource)
			gridHeader.setForeground(defaultForegroundColor);
		if (backgroundColor == null || backgroundColor instanceof UIResource)
			gridHeader.setBackground(defaultBackgroundColor);
		if (gridColor == null || gridColor instanceof UIResource)
			gridHeader.setGridColor(defaultGridColor);
		if (gridFont == null || gridFont instanceof UIResource)
			gridHeader.setFont(defaultGridFont);
		if (gridBorder == null || gridBorder instanceof UIResource)
			gridHeader.setBorder(defaultGridBorder);
		if (selectionForegroundColor == null || selectionForegroundColor instanceof UIResource)
			gridHeader.setSelectionForegroundColor(defaultSelectionForegroundColor);
		if (selectionBackgroundColor == null || selectionBackgroundColor instanceof UIResource)
			gridHeader.setSelectionBackgroundColor(defaultSelectionBackgroundColor);
		if (focusForegroundColor == null || focusForegroundColor instanceof UIResource)
			gridHeader.setFocusForegroundColor(defaultFocusForegroundColor);
		if (focusBackgroundColor == null || focusBackgroundColor instanceof UIResource)
			gridHeader.setFocusBackgroundColor(defaultFocusBackgroundColor);
	}
}
