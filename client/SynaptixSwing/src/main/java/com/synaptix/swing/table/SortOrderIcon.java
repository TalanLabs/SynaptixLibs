package com.synaptix.swing.table;

import java.awt.Color;

import javax.swing.Icon;

public abstract class SortOrderIcon implements Icon {

	protected int numberOrder;

	protected Color numberOrderColor;

	public Color getNumberOrderColor() {
		return numberOrderColor;
	}

	public void setNumberOrderColor(Color numberOrderColor) {
		this.numberOrderColor = numberOrderColor;
	}

	public int getNumberOrder() {
		return numberOrder;
	}

	public void setNumberOrder(int numberOrder) {
		this.numberOrder = numberOrder;
	}

}
