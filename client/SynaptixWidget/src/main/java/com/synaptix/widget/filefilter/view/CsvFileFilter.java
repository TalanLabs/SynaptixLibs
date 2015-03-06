package com.synaptix.widget.filefilter.view;

import com.synaptix.widget.util.StaticWidgetHelper;

public class CsvFileFilter extends AbstractFileFilter {

	public static final String CSV_EXTENSION = ".csv";

	public CsvFileFilter() {
		super(CSV_EXTENSION);
	}

	public String getDescription() {
		return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().csvFile();
	}
}
