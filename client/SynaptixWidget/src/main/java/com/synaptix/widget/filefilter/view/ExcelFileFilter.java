package com.synaptix.widget.filefilter.view;

import com.synaptix.widget.util.StaticWidgetHelper;

public class ExcelFileFilter extends AbstractFileFilter {

	public static final String XLSX_EXTENSION = ".xlsx";

	public ExcelFileFilter() {
		super(XLSX_EXTENSION);
	}

	@Override
	public String getDescription() {
		return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().excelFile();
	}
}
