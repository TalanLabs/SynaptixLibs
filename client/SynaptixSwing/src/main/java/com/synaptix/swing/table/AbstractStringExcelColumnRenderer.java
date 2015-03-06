package com.synaptix.swing.table;

import javax.swing.table.TableModel;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class AbstractStringExcelColumnRenderer extends AbstractExcelColumnRenderer {

	private XSSFCellStyle cellStyle;

	public AbstractStringExcelColumnRenderer() {
		super();
	}

	@Override
	public void prepareColumn(TableModel tableModel, XSSFSheet sheet, int col) {
		XSSFWorkbook wb = sheet.getWorkbook();
		XSSFDataFormat df = wb.createDataFormat();

		cellStyle = wb.createCellStyle();
		XSSFFont f = wb.createFont();
		f.setFontHeightInPoints((short) 12);
		f.setColor(IndexedColors.BLACK.getIndex());

		cellStyle.setFont(f);
		cellStyle.setDataFormat(df.getFormat("text"));
	}

	@Override
	public void setWritableCell(TableModel tableModel, XSSFCell cell, Object value, int row, int col) {
		XSSFWorkbook wb = cell.getSheet().getWorkbook();
		cell.setCellStyle(cellStyle);
		cell.setCellValue(wb.getCreationHelper().createRichTextString(getString(tableModel, value, row, col)));
		cell.setCellType(XSSFCell.CELL_TYPE_STRING);
	}

	public abstract String getString(TableModel tableModel, Object value, int row, int col);

}
