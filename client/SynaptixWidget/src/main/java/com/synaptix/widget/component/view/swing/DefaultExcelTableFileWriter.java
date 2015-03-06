package com.synaptix.widget.component.view.swing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.synaptix.component.IComponent;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.table.ExcelColumnRenderer;
import com.synaptix.widget.view.swing.tablemodel.DefaultComponentTableModel;

public class DefaultExcelTableFileWriter<E extends IComponent> implements IExcelTableFileWriter<E> {

	protected final Class<E> componentClass;

	protected final JSyTable table;

	protected final DefaultComponentTableModel<E> componentTableModel;

	public DefaultExcelTableFileWriter(Class<E> componentClass, JSyTable table, DefaultComponentTableModel<E> componentTableModel) {
		super();
		this.componentClass = componentClass;
		this.table = table;
		this.componentTableModel = new DefaultComponentTableModel<E>(componentClass, componentTableModel.getColumnTableFactory(), componentTableModel.getPropertyNames());
	}

	@Override
	public void createExcelFromTable(File file, List<E> components) throws Exception {
		componentTableModel.setComponents(components);

		XSSFWorkbook workbook = new XSSFWorkbook();

		// Feuille
		XSSFSheet tableSheet = workbook.createSheet(SwingMessages.getString("TableFileWriter.11"));

		createExcelHeader(workbook, tableSheet);
		createExcelData(workbook, tableSheet, components);

		tableSheet.createFreezePane(0, 1);
		tableSheet.setAutoFilter(new CellRangeAddress(0, tableSheet.getLastRowNum(), 0, table.getColumnCount() - 1));

		for (int i = 0; i < table.getColumnCount(); i++) {
			// tableSheet.autoSizeColumn(i);
			tableSheet.setColumnWidth(i, 20 * 256);
		}

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			workbook.write(out);
		} catch (IOException e) {
			throw e;
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	protected void createExcelHeader(XSSFWorkbook workbook, XSSFSheet tableSheet) throws Exception {
		XSSFFont f = workbook.createFont();
		f.setFontHeightInPoints((short) 12);
		f.setColor(IndexedColors.BLACK.getIndex());

		XSSFDataFormat df = workbook.createDataFormat();

		XSSFCellStyle cs = workbook.createCellStyle();
		cs.setFont(f);
		cs.setDataFormat(df.getFormat("text"));
		cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cs.setWrapText(true);

		XSSFCreationHelper createHelper = workbook.getCreationHelper();

		XSSFRow rh = tableSheet.createRow(0);
		for (int i = 0; i < table.getColumnCount(); i++) {
			XSSFCell c1 = rh.createCell(i);
			c1.setCellStyle(cs);
			c1.setCellValue(createHelper.createRichTextString(table.getColumnName(i)));
			c1.setCellType(XSSFCell.CELL_TYPE_STRING);
		}
	}

	protected void createExcelData(XSSFWorkbook workbook, XSSFSheet tableSheet, List<E> components) throws Exception {
		XSSFFont f = workbook.createFont();
		f.setFontHeightInPoints((short) 12);
		f.setColor(IndexedColors.BLACK.getIndex());

		XSSFDataFormat df = workbook.createDataFormat();

		XSSFCellStyle textCs = workbook.createCellStyle();
		textCs.setFont(f);
		textCs.setDataFormat(df.getFormat("text"));
		textCs.setWrapText(true);

		XSSFCellStyle numberCs = workbook.createCellStyle();
		numberCs.setFont(f);
		numberCs.setDataFormat(df.getFormat("#,##0.0"));

		XSSFCellStyle dateCs = workbook.createCellStyle();
		dateCs.setDataFormat(df.getFormat("m/d/yy h:mm"));
		dateCs.setFont(f);

		XSSFCreationHelper createHelper = workbook.getCreationHelper();

		for (int i = 0; i < table.getColumnCount(); i++) {
			ExcelColumnRenderer ecr = table.getExcelColumnRenderer(i);
			if (ecr != null) {
				ecr.prepareColumn(componentTableModel, tableSheet, i);
			}
		}

		if (components != null && !components.isEmpty()) {
			for (int j = 0; j < components.size(); j++) {
				E component = components.get(j);

				int y = j + 1;

				XSSFRow rh = tableSheet.createRow(y);

				for (int i = 0; i < table.getColumnCount(); i++) {
					XSSFCell cell = rh.createCell(i);

					int index = table.convertColumnIndexToModel(i);
					Object d = componentTableModel.getColumnTableFactory().getValue(componentTableModel.getPropertyNames()[index], component);

					ExcelColumnRenderer ecr = table.getExcelColumnRenderer(i);

					if (ecr != null) {
						ecr.setWritableCell(componentTableModel, cell, d, j, index);
					} else {
						if (d != null) {
							if (d instanceof Date) {
								cell.setCellValue((Date) d);
								cell.setCellStyle(dateCs);
							} else if (d instanceof Number) {
								cell.setCellValue(((Number) d).doubleValue());
								cell.setCellStyle(numberCs);
								cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
							} else if (d instanceof Boolean) {
								cell.setCellValue(createHelper.createRichTextString((Boolean) d ? SwingMessages.getString("TableFileWriter.13") //$NON-NLS-1$
										: SwingMessages.getString("TableFileWriter.14")));
								cell.setCellStyle(textCs);
								cell.setCellType(XSSFCell.CELL_TYPE_STRING);
							} else if (d instanceof String) {
								cell.setCellValue(createHelper.createRichTextString((String) d));
								cell.setCellStyle(textCs);
								cell.setCellType(XSSFCell.CELL_TYPE_STRING);
							} else {
								cell.setCellValue(createHelper.createRichTextString(d.toString()));
								cell.setCellStyle(textCs);
								cell.setCellType(XSSFCell.CELL_TYPE_STRING);
							}
						} else {
							cell.setCellValue(createHelper.createRichTextString(""));
							cell.setCellStyle(textCs);
							cell.setCellType(XSSFCell.CELL_TYPE_STRING);
						}
					}
				}
			}
		}
	}

	@Override
	public final E getComponent(int rowIndex) {
		return componentTableModel.getComponent(rowIndex);
	}
}
