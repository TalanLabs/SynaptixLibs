package com.synaptix.swing.search;

import java.awt.Color;
import java.awt.Font;

public abstract class AbstractSearchTableRowHighlight implements
		ISearchTableRowHighlight {

	@Override
	public Color getBackgroundColor(JSearchTable searchTable, Result result,
			boolean isSelected, boolean hasFocus, int rowModel, int rowView,
			int columnModel, int columnView) {
		return null;
	}

	@Override
	public Font getFont(JSearchTable searchTable, Result result,
			boolean isSelected, boolean hasFocus, int rowModel, int rowView,
			int columnModel, int columnView) {
		return null;
	}

	@Override
	public Color getForegroundColor(JSearchTable searchTable, Result result,
			boolean isSelected, boolean hasFocus, int rowModel, int rowView,
			int columnModel, int columnView) {
		return null;
	}
}
