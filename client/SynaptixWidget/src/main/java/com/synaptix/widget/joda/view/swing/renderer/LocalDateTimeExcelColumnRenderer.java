package com.synaptix.widget.joda.view.swing.renderer;

import javax.swing.table.TableModel;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDateTime;

import com.synaptix.swing.table.AbstractExcelColumnRenderer;

public class LocalDateTimeExcelColumnRenderer extends AbstractExcelColumnRenderer {

	private XSSFCellStyle cellStyle;

	@Override
	public void prepareColumn(TableModel tableModel, XSSFSheet sheet, int col) {
		XSSFWorkbook wb = sheet.getWorkbook();
		XSSFDataFormat df = wb.createDataFormat();

		cellStyle = wb.createCellStyle();
		XSSFFont f = wb.createFont();
		f.setFontHeightInPoints((short) 12);
		f.setColor(IndexedColors.BLACK.getIndex());

		cellStyle.setFont(f);
		cellStyle.setDataFormat(df.getFormat("m/d/yy h:mm"));
	}

	@Override
	public void setWritableCell(TableModel tableModel, XSSFCell cell, Object value, int row, int col) {
		LocalDateTime localDateTime = (LocalDateTime) value;
		cell.setCellStyle(cellStyle);
		if (localDateTime != null) {
			cell.setCellValue(localDateTime.toDate());
		}
	}
}
