package com.synaptix.swing.table;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

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

import com.synaptix.swing.JSyTable;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.swing.utils.Manager;
import com.synaptix.swing.utils.SyDesktop;
import com.synaptix.view.swing.error.ErrorViewManager;

public class TableFileWriter {

	private JSyTable table;

	public TableFileWriter(JSyTable table) {
		this.table = table;
	}

	public void export() {
		File file = null;
		if (Manager.isAutoSaveExportExcel()) {
			try {
				file = File.createTempFile("Export", ".xlsx");
				file.deleteOnExit();
			} catch (IOException e) {
				ErrorViewManager.getInstance().showErrorDialog(null, e);
			}
		} else {
			JFileChooser chooser = new JFileChooser();
			SaveCSVXLSFileFilter filter = new SaveCSVXLSFileFilter();
			chooser.setFileFilter(filter);
			int returnVal = chooser.showSaveDialog(GUIWindow.getActiveWindow());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				if (!f.isDirectory()) {
					file = f;
					if (getExtension(file) == null) {
						file = new File(file.getAbsolutePath() + ".xlsx"); //$NON-NLS-1$
					}
				}
			}
		}
		if (file != null) {
			if (Manager.isAutoSaveExportExcel()
					|| (!file.exists() || JOptionPane.showConfirmDialog(GUIWindow.getActiveWindow(), MessageFormat.format(SwingMessages.getString("TableFileWriter.1"), file //$NON-NLS-1$
							.getName()), SwingMessages.getString("TableFileWriter.2"), //$NON-NLS-1$
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)) {
				String extension = getExtension(file);
				if (extension.equals("xlsx")) { //$NON-NLS-1$
					try {
						createExcelFromTable(file);
					} catch (Exception e) {
						ErrorViewManager.getInstance().showErrorDialog(null, e);
					}
				} else if (extension.equals("csv")) { //$NON-NLS-1$
					try {
						createCSVFromTable(file);
					} catch (IOException e) {
						ErrorViewManager.getInstance().showErrorDialog(null, e);
					}
				}
				try {
					SyDesktop.open(file);
				} catch (Exception e) {
				}
			}
		}
	}

	private String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	private class SaveCSVXLSFileFilter extends FileFilter {

		public boolean accept(File pathname) {
			boolean res = false;
			if (pathname.isDirectory())
				res = true;
			else if (pathname.isFile()) {
				String extension = getExtension(pathname);
				if (extension != null) {
					if (extension.equals("csv") || extension.equals("xlsx")) { //$NON-NLS-1$ //$NON-NLS-2$
						res = true;
					}
				}
			}
			return res;
		}

		public String getDescription() {
			return "CSV, XLSX table"; //$NON-NLS-1$
		}

	}

	private void createCSVFromTable(File file) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));

		// Header
		for (int i = 0; i < table.getColumnCount(); i++) {
			if (i > 0)
				out.print(";"); //$NON-NLS-1$
			out.print(table.getColumnName(i));
		}
		out.println();

		// Data
		for (int j = 0; j < table.getRowCount(); j++) {
			for (int i = 0; i < table.getColumnCount(); i++) {
				Object o = table.getValueAt(j, i);
				if (i > 0)
					out.print(";"); //$NON-NLS-1$
				if (o != null) {
					out.print(o.toString());
				} else {
					out.print(""); //$NON-NLS-1$
				}
			}
			out.println();
		}

		out.close();
	}

	/**
	 * Create excel from table in file
	 * 
	 * @param file
	 * @throws IOException
	 * @throws WriteException
	 */
	public void createExcelFromTable(File file) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Feuille
		XSSFSheet tableSheet = workbook.createSheet(SwingMessages.getString("TableFileWriter.11"));

		createExcelHeader(workbook, tableSheet);
		createExcelData(workbook, tableSheet);

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

	private void createExcelHeader(XSSFWorkbook workbook, XSSFSheet tableSheet) throws Exception {
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

	private void createExcelData(XSSFWorkbook workbook, XSSFSheet tableSheet) throws Exception {
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
				ecr.prepareColumn(table.getModel(), tableSheet, table.convertColumnIndexToModel(i));
			}
		}

		for (int j = 0; j < table.getRowCount(); j++) {
			int y = j + 1;

			XSSFRow rh = tableSheet.createRow(y);

			for (int i = 0; i < table.getColumnCount(); i++) {
				XSSFCell cell = rh.createCell(i);

				Object d = table.getValueAt(j, i);

				ExcelColumnRenderer ecr = table.getExcelColumnRenderer(i);
				if (ecr != null) {
					ecr.setWritableCell(table.getModel(), cell, d, table.convertRowIndexToModel(j), table.convertColumnIndexToModel(i));
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
