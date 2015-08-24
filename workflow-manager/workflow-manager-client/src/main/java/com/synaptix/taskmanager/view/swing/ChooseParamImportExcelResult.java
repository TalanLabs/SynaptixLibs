package com.synaptix.taskmanager.view.swing;

import com.synaptix.taskmanager.model.XlsSheet;

public class ChooseParamImportExcelResult {

	private XlsSheet sheet;

	private int columnBundleName;

	private int columnKey;

	private int columnTraduction;

	private int rowStart;

	public ChooseParamImportExcelResult(XlsSheet sheet, int columnBundleName, int columnKey, int columnTraduction, int rowStart) {
		super();
		this.columnBundleName = columnBundleName;
		this.sheet = sheet;
		this.columnKey = columnKey;
		this.columnTraduction = columnTraduction;
		this.rowStart = rowStart;
	}

	public XlsSheet getSheet() {
		return sheet;
	}

	public int getColumnBundleName() {
		return columnBundleName;
	}

	public int getColumnKey() {
		return columnKey;
	}

	public int getColumnTraduction() {
		return columnTraduction;
	}

	public int getRowStart() {
		return rowStart;
	}
}
