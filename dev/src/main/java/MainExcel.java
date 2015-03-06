import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.synaptix.swing.utils.SyDesktop;

public class MainExcel {

	public static void main(String[] args) throws Exception {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFCreationHelper createHelper = wb.getCreationHelper();

		// create a new sheet
		XSSFSheet s = wb.createSheet();

		s.createFreezePane(0, 1);

		s.setDefaultRowHeight((short) (20 * 20));

		// create 2 cell styles
		XSSFCellStyle cs = wb.createCellStyle();
		XSSFCellStyle cs2 = wb.createCellStyle();
		XSSFDataFormat df = wb.createDataFormat();

		// create 2 fonts objects
		XSSFFont f = wb.createFont();
		XSSFFont f2 = wb.createFont();

		// Set font 1 to 12 point type, blue and bold
		f.setFontHeightInPoints((short) 12);
		f.setColor(new XSSFColor(new Color(10, 100, 200)));
		f.setBoldweight(Font.BOLDWEIGHT_BOLD);

		// Set font 2 to 10 point type, red and bold
		f2.setFontHeightInPoints((short) 10);
		f2.setColor(IndexedColors.RED.getIndex());
		f2.setBoldweight(Font.BOLDWEIGHT_BOLD);

		// Set cell style and formatting
		cs.setFont(f);
		cs.setDataFormat(df.getFormat("text"));
		cs.setFillForegroundColor(new XSSFColor(new Color(200, 100, 10)));
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		// Set the other cell style and formatting
		cs2.setBorderBottom(CellStyle.BORDER_THIN);
		cs2.setDataFormat(df.getFormat("m/d/yy h:mm"));
		cs2.setFont(f2);

		XSSFRow rh = s.createRow(0);
		for (int cellnum = 0; cellnum < 10; cellnum++) {
			XSSFCell c1 = rh.createCell(cellnum);
			c1.setCellStyle(cs);
			c1.setCellValue(createHelper.createRichTextString("Colonne! " + cellnum));
			c1.setCellType(XSSFCell.CELL_TYPE_STRING);
		}

		// Define a few rows
		for (int rownum = 1; rownum < 60; rownum++) {
			XSSFRow r2 = s.createRow(rownum);
			for (int cellnum = 0; cellnum < 10; cellnum++) {
				XSSFCell c1 = r2.createCell(cellnum);
				c1.setCellStyle(cs2);
				c1.setCellValue(new Date());
			}
		}

		for (int cellnum = 0; cellnum < 10; cellnum++) {
			// s.autoSizeColumn(cellnum);
			s.setColumnWidth(cellnum, 16 * 256);
		}

		s.setAutoFilter(new CellRangeAddress(0, s.getLastRowNum(), 0, 9));

		// Save
		String filename = "D:/workbook.xlsx";

		FileOutputStream out = new FileOutputStream(filename);
		wb.write(out);
		out.close();

		SyDesktop.open(new File(filename));
	}

}
