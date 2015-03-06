package com.synaptix.swing.search;

import java.awt.Color;
import java.awt.Font;

import com.synaptix.swing.JSearch;

public abstract class AbstractSearchRowHighlight implements SearchRowHighlight {

	@Override
	public Color getBackgroundColor(JSearch search, Result result,
			boolean isSelected, boolean hasFocus, int rowModel, int rowView,int columnModel,int columnView) {
		return null;
	}

	@Override
	public Font getFont(JSearch search, Result result, boolean isSelected,
			boolean hasFocus, int rowModel, int rowView,int columnModel,int columnView) {
		return null;
	}

	@Override
	public Color getForegroundColor(JSearch search, Result result,
			boolean isSelected, boolean hasFocus, int rowModel, int rowView,int columnModel,int columnView) {
		return null;
	}
}
