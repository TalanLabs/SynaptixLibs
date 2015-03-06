package com.synaptix.widget.hierarchical.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
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
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.swing.utils.Manager;
import com.synaptix.swing.utils.SyDesktop;
import com.synaptix.widget.filefilter.view.ExcelFileFilter;
import com.synaptix.widget.hierarchical.context.IHierarchicalContext;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.viewworker.view.AbstractWorkInProgressViewWorker;

public class DefaultHierarchicalExportWriter<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> implements IHierarchicalExportWriter<E, F, L> {

	public DefaultHierarchicalExportWriter() {
		super();
	}

	@Override
	public void export(final IHierarchicalContext<E, F, L> hierarchicalExportContext, final IExportableTable[][] exportTableParts,
			final Map<Class<?>, GenericObjectToString<?>> objectsToStringByClassMap) {
		File chooseFile = null;
		if (Manager.isAutoSaveExportExcel()) {
			try {
				chooseFile = File.createTempFile("Export", ".xlsx");
				chooseFile.deleteOnExit();
			} catch (IOException e) {
				hierarchicalExportContext.getViewFactory().showErrorMessageDialog(hierarchicalExportContext.getView(), e);
			}
		} else {
			chooseFile = hierarchicalExportContext.getViewFactory().chooseSaveFile(hierarchicalExportContext.getView(), null, new ExcelFileFilter());
		}
		final File file = chooseFile;
		if (file != null) {
			hierarchicalExportContext.getViewFactory().waitComponentViewWorker(hierarchicalExportContext.getView(), new AbstractWorkInProgressViewWorker<Void>() {

				@Override
				protected Void doLoading() throws Exception {
					doExport(file, exportTableParts, objectsToStringByClassMap);
					return null;
				}

				@Override
				public void success(Void e) {
					try {
						SyDesktop.open(file);
					} catch (Exception e1) {
						hierarchicalExportContext.getViewFactory().showErrorMessageDialog(hierarchicalExportContext.getView(), e1);
					}
				}

				@Override
				public void fail(Throwable t) {
					hierarchicalExportContext.getViewFactory().showErrorMessageDialog(hierarchicalExportContext.getView(), t);
				}
			});
		}
	}

	protected void doExport(final File file, final IExportableTable[][] exportTableParts, final Map<Class<?>, GenericObjectToString<?>> objectsToStringByClassMap) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();

		XSSFSheet tableSheet = workbook.createSheet(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().hierarchicalTable());

		XSSFFont f = workbook.createFont();
		f.setFontHeightInPoints((short) 12);
		f.setColor(IndexedColors.BLACK.getIndex());

		XSSFDataFormat df = workbook.createDataFormat();

		XSSFCellStyle textCs = workbook.createCellStyle();
		textCs.setFont(f);
		textCs.setDataFormat(df.getFormat("text"));
		textCs.setWrapText(true);
		putBorders(textCs);

		XSSFCellStyle numberCs = workbook.createCellStyle();
		numberCs.setFont(f);
		numberCs.setDataFormat(df.getFormat("#,##0.0"));
		putBorders(numberCs);

		XSSFCellStyle integerCs = workbook.createCellStyle();
		integerCs.setFont(f);
		integerCs.setDataFormat(df.getFormat("#"));
		putBorders(integerCs);

		XSSFCellStyle dateCs = workbook.createCellStyle();
		dateCs.setDataFormat(df.getFormat("m/d/yy h:mm"));
		dateCs.setFont(f);
		putBorders(dateCs);

		XSSFCellStyle titleCs = workbook.createCellStyle();
		titleCs.setFont(f);
		titleCs.setDataFormat(df.getFormat("text"));
		titleCs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		titleCs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		titleCs.setAlignment(CellStyle.ALIGN_CENTER);
		putBorders(titleCs);

		XSSFCreationHelper createHelper = workbook.getCreationHelper();

		int group = 0;
		int rowOffset = 0;
		int maxRowCreated = 0;
		int maxColumnCreated = 0;
		int maxColumnWithoutComputed = 0;
		int headerSize = 0;
		int titleSize = 0;
		Map<Integer, Integer> columnMap = new HashMap<Integer, Integer>();
		for (IExportableTable[] exportableTableSubPart : exportTableParts) {
			int maxHeight = 0;
			for (IExportableTable exportTable : exportableTableSubPart) {
				maxHeight = Math.max(maxHeight, exportTable.getExportHeight());
			}
			while (maxRowCreated <= rowOffset + maxHeight) {
				tableSheet.createRow(maxRowCreated++);
			}
			int columnOffset = 0;
			int groupColumn = 0;
			for (IExportableTable exportTable : exportableTableSubPart) {

				if (columnOffset == 0) {
					titleSize = Math.max(titleSize, exportTable.getExportWidth());
				}
				boolean title = (group == 0) || (columnOffset == 0);
				for (int rowIndex = 0; rowIndex < exportTable.getExportHeight(); rowIndex++) {
					for (int columnIndex = 0; columnIndex < exportTable.getExportWidth(); columnIndex++) {
						int cellRow = rowIndex + rowOffset;
						XSSFRow rh = tableSheet.getRow(cellRow);
						int cellColumn = columnIndex + columnOffset;

						if (rh.getCell(cellColumn) == null) {
							XSSFCell cell = rh.createCell(cellColumn);
							maxColumnCreated = Math.max(maxColumnCreated, cellColumn);
							if (groupColumn < 2) { // we don't want to add compted columns
								maxColumnWithoutComputed = Math.max(maxColumnWithoutComputed, cellColumn);
							}
							columnMap.put(cellColumn, Math.max(exportTable.getColumnWidth(columnIndex), columnMap.get(cellColumn) != null ? columnMap.get(cellColumn) : 0));

							CellInformation cellInformation = exportTable.getCellInformation(rowIndex, columnIndex);
							if ((title) || ((cellInformation != null) && (cellInformation.getObject() != null))) {

								displayObjectInCell(cell, cellInformation.getObject(), objectsToStringByClassMap, textCs, numberCs, integerCs, dateCs, createHelper);
								if ((cellInformation != null) && (cellInformation.getHorizontalCellSpan() > 1)) {
									for (int k = columnIndex + 1; k <= cellInformation.getHorizontalCellSpan() - 1; k++) {
										XSSFCell cell2 = rh.createCell(cellColumn + k);
										cell2.setCellStyle(textCs);
									}
									tableSheet.addMergedRegion(new CellRangeAddress(cellRow, cellRow, cellColumn, cellColumn + cellInformation.getHorizontalCellSpan() - 1));
									columnIndex += cellInformation.getHorizontalCellSpan() - 1;
								}
								if ((cellInformation != null) && (cellInformation.getVerticalCellSpan() > 1)) {
									for (int k = 1; k < cellInformation.getVerticalCellSpan(); k++) {
										XSSFRow rh2 = tableSheet.getRow(k + cellRow);
										if (rh2.getCell(cellColumn) == null) {
											XSSFCell cell2 = rh2.createCell(cellColumn);
											cell2.setCellStyle(title ? titleCs : textCs);
										}
									}
									tableSheet.addMergedRegion(new CellRangeAddress(cellRow, cellInformation.getVerticalCellSpan() + cellRow - 1, cellColumn, cellColumn));
								} else if ((maxHeight > exportTable.getExportHeight()) && (rowIndex == 0)) {
									for (int k = 1; k <= maxHeight - exportTable.getExportHeight() + rowIndex; k++) {
										XSSFRow rh2 = tableSheet.getRow(k + rowIndex);
										XSSFCell cell2 = rh2.createCell(cellColumn);
										cell2.setCellStyle(title ? titleCs : textCs);
									}
									tableSheet.addMergedRegion(new CellRangeAddress(0, maxHeight - exportTable.getExportHeight() + rowIndex, cellColumn, cellColumn));
								}
								if (title) {
									cell.setCellStyle(titleCs);
								}
							} else {
								cell.setCellStyle(textCs);
							}
						}
					}
				}
				columnOffset += exportTable.getExportWidth();
				maxHeight = Math.max(maxHeight, exportTable.getExportHeight());
				groupColumn++;
			}
			rowOffset += maxHeight;

			if (group == 0) {
				headerSize = maxHeight;
			}
			group++;
		}
		tableSheet.setAutoFilter(new CellRangeAddress(headerSize - 1, tableSheet.getLastRowNum(), 0, maxColumnWithoutComputed));

		for (int i = 0; i < maxColumnCreated; i++) {
			tableSheet.setColumnWidth(i, columnMap.get(i) * 50);
		}
		tableSheet.createFreezePane(titleSize, headerSize, titleSize, headerSize);

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

	private void putBorders(XSSFCellStyle cs) {
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
	}

	public void displayObjectInCell(XSSFCell cell, Object d, Map<Class<?>, GenericObjectToString<?>> objectsToStringByClassMap, XSSFCellStyle textCs, XSSFCellStyle numberCs, XSSFCellStyle integerCs,
			XSSFCellStyle dateCs, XSSFCreationHelper createHelper) {
		if (d != null) {
			if (d instanceof Date) {
				cell.setCellValue((Date) d);
				cell.setCellStyle(dateCs);
			} else if (d instanceof Integer) {
				cell.setCellValue(((Integer) d).doubleValue());
				cell.setCellStyle(integerCs);
				cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
			} else if (d instanceof BigInteger) {
				cell.setCellValue(((BigInteger) d).doubleValue());
				cell.setCellStyle(integerCs);
				cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
			} else if (d instanceof Number) {
				cell.setCellValue(((Number) d).doubleValue());
				cell.setCellStyle(numberCs);
				cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
			} else if (d instanceof Boolean) {
				cell.setCellValue(createHelper.createRichTextString((Boolean) d ? StaticWidgetHelper.getSynaptixWidgetConstantsBundle().printTrue() : StaticWidgetHelper
						.getSynaptixWidgetConstantsBundle().printFalse()));
				cell.setCellStyle(textCs);
				cell.setCellType(XSSFCell.CELL_TYPE_STRING);
			} else if (d instanceof String) {
				cell.setCellValue(createHelper.createRichTextString((String) d));
				cell.setCellStyle(textCs);
				cell.setCellType(XSSFCell.CELL_TYPE_STRING);
			} else {
				String text = getString(d, objectsToStringByClassMap);
				cell.setCellValue(createHelper.createRichTextString(text));
				cell.setCellStyle(textCs);
				cell.setCellType(XSSFCell.CELL_TYPE_STRING);
			}
		} else {
			cell.setCellValue(createHelper.createRichTextString(""));
			cell.setCellStyle(textCs);
			cell.setCellType(XSSFCell.CELL_TYPE_STRING);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> String getString(T d, Map<Class<?>, GenericObjectToString<?>> objectsToStringByClassMap) {
		GenericObjectToString<?> objToString = objectsToStringByClassMap.get(d.getClass());
		if (objToString != null) {
			return ((GenericObjectToString<T>) objToString).getString(d);
		}
		return d.toString();
	}
}
