package com.synaptix.swing.table;

import java.awt.Component;
import java.awt.Graphics;

public class NullIcon extends SortOrderIcon {

	public int getIconHeight() {
		return 6;
	}

	public int getIconWidth() {
		return 12;
	}

	public void paintIcon(Component pComponent, Graphics pGraphics, int pX,
			int pY) {
	}

	protected float invertAsNeed(float pBright, boolean pNeed) {
		return pNeed ? 1 - pBright : pBright;
	}
}