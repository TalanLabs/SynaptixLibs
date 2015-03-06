package com.synaptix.swing.search;

import java.awt.Color;

import com.synaptix.swing.JSearch;

public class DefaultSearchRowHighlight extends AbstractSearchRowHighlight {

	private static final Color c = new Color(200, 227, 255);

	public Color getForegroundColor(JSearch search, Result result,
			boolean isSelected, boolean hasFocus, int rowModel, int rowView,
			int columnModel, int columnView) {
		if (isSelected) {
			return null;
		}

		if (rowView % 2 == 0) {
			return Color.BLACK;
		}
		return Color.BLACK;
	}

	public Color getBackgroundColor(JSearch search, Result result,
			boolean isSelected, boolean hasFocus, int rowModel, int rowView,
			int columnModel, int columnView) {
		if (isSelected) {
			return null;
		}

		if (rowView % 2 == 0) {
			return Color.WHITE;
		}
		return c;
	}
}
