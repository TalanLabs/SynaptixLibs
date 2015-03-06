package com.synaptix.swing.search;

import java.awt.Color;
import java.awt.Font;

public interface ISearchTableRowHighlight {

	public abstract Color getForegroundColor(JSearchTable searchTable,
			Result result, boolean isSelected, boolean hasFocus, int rowModel,
			int rowView, int columnModel, int columnView);

	public abstract Color getBackgroundColor(JSearchTable searchTable,
			Result result, boolean isSelected, boolean hasFocus, int rowModel,
			int rowView, int columnModel, int columnView);

	public abstract Font getFont(JSearchTable searchTable, Result result,
			boolean isSelected, boolean hasFocus, int rowModel, int rowView,
			int columnModel, int columnView);

}
