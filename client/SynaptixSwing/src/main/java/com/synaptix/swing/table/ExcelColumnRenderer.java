package com.synaptix.swing.table;

import javax.swing.table.TableModel;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public interface ExcelColumnRenderer {

	public abstract void prepareColumn(TableModel tableModel, XSSFSheet sheet, int col);

	public abstract void setWritableCell(TableModel tableModel, XSSFCell cell, Object value, int row, int col);

}
