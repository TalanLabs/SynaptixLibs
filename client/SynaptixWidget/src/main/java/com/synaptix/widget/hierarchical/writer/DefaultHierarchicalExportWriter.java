package com.synaptix.widget.hierarchical.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

import com.synaptix.client.view.IView;
import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.swing.utils.Manager;
import com.synaptix.swing.utils.SyDesktop;
import com.synaptix.widget.filefilter.view.ExcelFileFilter;
import com.synaptix.widget.hierarchical.context.IHierarchicalContext;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.viewworker.view.AbstractWorkInProgressViewWorker;

public class DefaultHierarchicalExportWriter<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> implements IHierarchicalExportWriter<E, F, L> {

	public DefaultHierarchicalExportWriter() {
		super();
	}

	@Override
	public void export(final IHierarchicalContext<E, F, L> hierarchicalExportContext, final IExportableTable[][] exportTableParts,
			final Map<Class<?>, GenericObjectToString<?>> objectsToStringByClassMap) {
		File chooseFile = null;
		final ISynaptixViewFactory viewFactory = hierarchicalExportContext.getViewFactory();
		final IView view = hierarchicalExportContext.getView();
		if (Manager.isAutoSaveExportExcel()) {
			try {
				chooseFile = File.createTempFile("Export", ".xlsx");
				chooseFile.deleteOnExit();
			} catch (IOException e) {
				viewFactory.showErrorMessageDialog(view, e);
			}
		} else {
			chooseFile = viewFactory.chooseSaveFile(view, null, new ExcelFileFilter());
		}
		final File file = chooseFile;
		if (file != null) {
			viewFactory.waitComponentViewWorker(view, new AbstractWorkInProgressViewWorker<Void>() {

				@Override
				protected Void doLoading() throws Exception {
					doExport(file, hierarchicalExportContext, exportTableParts, objectsToStringByClassMap);
					return null;
				}

				@Override
				public void success(Void e) {
					try {
						SyDesktop.open(file);
					} catch (Exception e1) {
						viewFactory.showErrorMessageDialog(view, e1);
					}
				}

				@Override
				public void fail(Throwable t) {
					viewFactory.showErrorMessageDialog(view, t);
				}
			});
		}
	}

	protected void doExport(final File file, IHierarchicalContext<E, F, L> hierarchicalExportContext, final IExportableTable[][] exportTableParts,
			final Map<Class<?>, GenericObjectToString<?>> objectsToStringByClassMap) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();

		XSSFSheet tableSheet = workbook.createSheet(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().hierarchicalTable());

		XSSFFont f = workbook.createFont();
		f.setFontHeightInPoints((short) 12);
		f.setColor(IndexedColors.BLACK.getIndex());

		XSSFDataFormat df = workbook.createDataFormat();

		Map<IHierarchicalExportWriter.Type, XSSFCellStyle> styleMap = new HashMap<IHierarchicalExportWriter.Type, XSSFCellStyle>();

		XSSFCellStyle textCs = workbook.createCellStyle();
		textCs.setFont(f);
		textCs.setDataFormat(df.getFormat("text"));
		textCs.setWrapText(true);
		putBorders(textCs);
		styleMap.put(IHierarchicalExportWriter.Type.TEXT, textCs);

		XSSFCellStyle numberCs = workbook.createCellStyle();
		numberCs.setFont(f);
		numberCs.setDataFormat(df.getFormat("#,##0.0"));
		putBorders(numberCs);
		styleMap.put(IHierarchicalExportWriter.Type.NUMBER, numberCs);

		XSSFCellStyle integerCs = workbook.createCellStyle();
		integerCs.setFont(f);
		integerCs.setDataFormat(df.getFormat("#"));
		putBorders(integerCs);
		styleMap.put(IHierarchicalExportWriter.Type.INTEGER, integerCs);

		XSSFCellStyle dateCs = workbook.createCellStyle();
		dateCs.setDataFormat(df.getFormat("m/d/yy h:mm"));
		dateCs.setFont(f);
		putBorders(dateCs);
		styleMap.put(IHierarchicalExportWriter.Type.DATE, dateCs);

		XSSFCellStyle percentCs = workbook.createCellStyle();
		percentCs.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
		percentCs.setDataFormat(df.getFormat("0.0%"));
		putBorders(percentCs);
		styleMap.put(IHierarchicalExportWriter.Type.PERCENT, percentCs);

		XSSFCellStyle titleCs = workbook.createCellStyle();
		titleCs.setFont(f);
		titleCs.setDataFormat(df.getFormat("text"));
		titleCs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		titleCs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		titleCs.setAlignment(CellStyle.ALIGN_CENTER);
		putBorders(titleCs);
		styleMap.put(IHierarchicalExportWriter.Type.TITLE, titleCs);

		XSSFCreationHelper createHelper = workbook.getCreationHelper();

		int group = 0;
		int rowOffset = 0;
		int maxRowCreated = 0;
		int maxColumnCreated = 0;
		int maxColumnWithoutComputed = 0;
		int headerSize = 0;
		int titleSize = 0;
		Map<Integer, Integer> columnMap = new HashMap<Integer, Integer>();
		Set<String> table = new HashSet<String>();
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
						int cellColumn = columnIndex + columnOffset;

						XSSFCell cell = createCell(tableSheet, cellRow, cellColumn, table);
						if (cell != null) {
							maxColumnCreated = Math.max(maxColumnCreated, cellColumn);
							if (groupColumn < 2) { // we don't want to add computed columns
								maxColumnWithoutComputed = Math.max(maxColumnWithoutComputed, cellColumn);
							}
							columnMap.put(cellColumn, Math.max(exportTable.getColumnWidth(columnIndex), columnMap.get(cellColumn) != null ? columnMap.get(cellColumn) : 0));

							CellInformation cellInformation = exportTable.getCellInformation(rowIndex, columnIndex);
							if ((title) || ((cellInformation != null) && (cellInformation.getObject() != null))) {

								displayObjectInCell(cell, cellInformation.getObject(), objectsToStringByClassMap, styleMap, exportTable.getForcedType(rowIndex, columnIndex), createHelper);

								if (title) {
									cell.setCellStyle(styleMap.get(IHierarchicalExportWriter.Type.TITLE));
								}

								if ((cellInformation != null) && (cellInformation.getHorizontalCellSpan() > 1)) {
									if ((maxHeight <= 1) || (cellInformation.getHorizontalCellSpan() < exportTable.getExportWidth())) { // otherwise will be done by the last region
																																		// merge
										for (int k = columnIndex + 1; k <= cellInformation.getHorizontalCellSpan() - 1; k++) {
											XSSFCell cell2 = createCell(tableSheet, cellRow, cellColumn + k, table);
											if (cell2 != null) {
												cell2.setCellStyle(title ? styleMap.get(IHierarchicalExportWriter.Type.TITLE) : styleMap.get(IHierarchicalExportWriter.Type.TEXT));
											}
										}
										tableSheet.addMergedRegion(new CellRangeAddress(cellRow, cellRow, cellColumn, cellColumn + cellInformation.getHorizontalCellSpan() - 1));
									}
								}
								if ((cellInformation != null) && (cellInformation.getVerticalCellSpan() > 1)) {
									for (int k = 1; k < cellInformation.getVerticalCellSpan(); k++) {
										XSSFCell cell2 = createCell(tableSheet, k + cellRow, cellColumn, table);
										if (cell2 != null) {
											cell2.setCellStyle(title ? styleMap.get(IHierarchicalExportWriter.Type.TITLE) : styleMap.get(IHierarchicalExportWriter.Type.TEXT));
										}
									}
									tableSheet.addMergedRegion(new CellRangeAddress(cellRow, cellInformation.getVerticalCellSpan() + cellRow - 1, cellColumn, cellColumn));
								} else if ((maxHeight > exportTable.getExportHeight()) && (rowIndex == 0)) {
									for (int k = 0; k <= maxHeight - exportTable.getExportHeight(); k++) {
										for (int p = 0; p < cellInformation.getHorizontalCellSpan(); p++) {
											XSSFCell cell2 = createCell(tableSheet, k + cellRow, cellColumn + p, table);
											if (cell2 != null) {
												cell2.setCellStyle(title ? styleMap.get(IHierarchicalExportWriter.Type.TITLE) : styleMap.get(IHierarchicalExportWriter.Type.TEXT));
											}
										}
									}
									tableSheet.addMergedRegion(new CellRangeAddress(cellRow, cellRow + maxHeight - exportTable.getExportHeight(), cellColumn, cellColumn
											+ cellInformation.getHorizontalCellSpan() - 1));
								}
							} else {
								cell.setCellStyle(styleMap.get(IHierarchicalExportWriter.Type.TEXT));
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

	private XSSFCell createCell(XSSFSheet tableSheet, int cellRow, int cellColumn, Set<String> table) {
		String key = cellRow + "-" + cellColumn;
		if (!table.add(key)) {
			return null;
		}
		XSSFRow rh = tableSheet.getRow(cellRow);
		table.add(key);
		return rh.createCell(cellColumn);
	}

	private void putBorders(XSSFCellStyle cs) {
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderTop(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderBottom(BorderStyle.THIN);
	}

	public void displayObjectInCell(XSSFCell cell, Object d, Map<Class<?>, GenericObjectToString<?>> objectsToStringByClassMap, Map<IHierarchicalExportWriter.Type, XSSFCellStyle> styleMap,
			IHierarchicalExportWriter.Type type, XSSFCreationHelper createHelper) {
		if (d instanceof Date) {
			cell.setCellValue((Date) d);
			cell.setCellStyle(styleMap.get(IHierarchicalExportWriter.Type.DATE));
		} else if (d instanceof Integer) {
			double v = ((Integer) d).doubleValue();
			cell.setCellValue(type == IHierarchicalExportWriter.Type.PERCENT ? v / 100d : v);
			cell.setCellStyle(styleMap.get(type == IHierarchicalExportWriter.Type.PERCENT ? type : IHierarchicalExportWriter.Type.INTEGER));
			cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
		} else if (d instanceof BigInteger) {
			BigInteger v = (BigInteger) d;
			cell.setCellValue((type == IHierarchicalExportWriter.Type.PERCENT ? v.divide(BigInteger.TEN).divide(BigInteger.TEN) : v).doubleValue());
			cell.setCellStyle(styleMap.get(type == IHierarchicalExportWriter.Type.PERCENT ? type : IHierarchicalExportWriter.Type.INTEGER));
			cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
		} else if (d instanceof Number) {
			double v = ((Number) d).doubleValue();
			cell.setCellValue(type == IHierarchicalExportWriter.Type.PERCENT ? v / 100d : v);
			cell.setCellStyle(styleMap.get(type == IHierarchicalExportWriter.Type.PERCENT ? type : IHierarchicalExportWriter.Type.NUMBER));
			cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
		} else if (d instanceof Boolean) {
			cell.setCellValue(createHelper.createRichTextString((Boolean) d ? StaticWidgetHelper.getSynaptixWidgetConstantsBundle().printTrue() : StaticWidgetHelper.getSynaptixWidgetConstantsBundle()
					.printFalse()));
			cell.setCellStyle(styleMap.get(IHierarchicalExportWriter.Type.TEXT));
			cell.setCellType(XSSFCell.CELL_TYPE_STRING);
		} else if (d instanceof String) {
			cell.setCellValue(createHelper.createRichTextString((String) d));
			cell.setCellStyle(styleMap.get(IHierarchicalExportWriter.Type.TEXT));
			cell.setCellType(XSSFCell.CELL_TYPE_STRING);
		} else {
			String text = "";
			if (d != null) {
				text = getString(d, objectsToStringByClassMap);
			}
			cell.setCellValue(createHelper.createRichTextString(text));
			cell.setCellStyle(styleMap.get(IHierarchicalExportWriter.Type.TEXT));
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
