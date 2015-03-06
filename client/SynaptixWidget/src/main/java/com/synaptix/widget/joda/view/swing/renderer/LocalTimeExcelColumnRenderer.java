package com.synaptix.widget.joda.view.swing.renderer;

import javax.swing.table.TableModel;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.synaptix.swing.table.ExcelColumnRenderer;
import com.synaptix.widget.util.StaticWidgetHelper;

public class LocalTimeExcelColumnRenderer extends AbstractLocalGenericObjectToString<LocalTime> implements ExcelColumnRenderer {

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
		cellStyle.setDataFormat(df.getFormat("text"));
	}

	@Override
	public void setWritableCell(TableModel tableModel, XSSFCell cell, Object value, int row, int col) {
		String string = getString((LocalTime) value);
		XSSFWorkbook wb = cell.getSheet().getWorkbook();
		cell.setCellStyle(cellStyle);
		cell.setCellType(XSSFCell.CELL_TYPE_STRING);
		if (string != null) {
			cell.setCellValue(wb.getCreationHelper().createRichTextString(string));
		}
	}

	@Override
	protected DateTimeFormatter getDateTimeFormatter() {
		return new DateTimeFormatterBuilder().appendPattern(StaticWidgetHelper.getSynaptixDateConstantsBundle().displayTimeFormat()).toFormatter();
	}
}
