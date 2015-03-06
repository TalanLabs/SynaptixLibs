package com.synaptix.swing.search;

import java.awt.Color;
import java.awt.Font;

import com.synaptix.swing.JSearch;

public interface SearchRowHighlight {

	public Color getForegroundColor(JSearch search, Result result,
			boolean isSelected, boolean hasFocus, int rowModel, int rowView,
			int columnModel, int columnView);

	public Color getBackgroundColor(JSearch search, Result result,
			boolean isSelected, boolean hasFocus, int rowModel, int rowView,
			int columnModel, int columnView);

	public Font getFont(JSearch search, Result result, boolean isSelected,
			boolean hasFocus, int rowModel, int rowView, int columnModel,
			int columnView);
}
