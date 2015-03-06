import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;

import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.renderer.HourCellRenderer;
import com.synaptix.swing.table.AbstractExcelColumnRenderer;
import com.synaptix.swing.table.AbstractSpecialTableModel;
import com.synaptix.swing.table.Function;
import com.synaptix.swing.table.SyTableColumn;
import com.synaptix.swing.table.filter.HourFilterColumn;
import com.synaptix.swing.utils.DateTimeUtils;
import com.synaptix.swing.utils.Manager;
import com.synaptix.swing.widget.HourExcelColumnRenderer;

public class TestSyTable {

	private static class MySyTableModel extends AbstractSpecialTableModel {

		private static final long serialVersionUID = -878825691309640954L;

		@Override
		public String getColumnId(int columnIndex) {
			return getColumnName(columnIndex);
		}

		@Override
		public String getColumnName(int column) {
			return "Column " + String.valueOf(column);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			Class<?> res = Object.class;
			switch (columnIndex) {
			case 0:
				res = String.class;
				break;
			case 1:
				res = Date.class;
				break;
			case 2:
				res = Integer.class;
				break;
			case 3:
				res = Double.class;
				break;
			case 4:
				res = BigDecimal.class;
				break;
			case 5:
				res = Boolean.class;
				break;
			case 6:
				res = Integer.class;
				break;
			case 7:
				res = Integer.class;
				break;
			}
			return res;
		}

		@Override
		public boolean isDefaultVisible(int columnIndex) {
			if (columnIndex % 2 == 0) {
				return false;
			}
			return true;
		}

		@Override
		public int getColumnCount() {
			return 8;
		}

		@Override
		public int getRowCount() {
			return 40;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object res = null;
			switch (columnIndex) {
			case 0:
				if (rowIndex % 5 == 0) {
					res = null;
				} else {
					res = "String_" + rowIndex;
				}
				break;
			case 1:
				Calendar c = Calendar.getInstance();
				c.add(Calendar.HOUR_OF_DAY, rowIndex * 3);
				if (rowIndex % 5 == 2) {
					res = null;
				} else {
					res = c.getTime();
				}
				break;
			case 2:
				if (rowIndex % 5 == 3) {
					res = null;
				} else {
					res = rowIndex;
				}
				break;
			case 3:
				res = Math.exp(rowIndex) * 3.4;
				break;
			case 4:
				res = rowIndex;
				break;
			case 5:
				if (rowIndex % 5 == 4) {
					res = true;
				} else {
					res = false;
				}
				break;
			case 6:
				res = rowIndex * columnIndex;
				break;
			case 7:
				res = rowIndex * columnIndex;
				break;
			}
			return res;
		}

		@Override
		public boolean isExistSumValue(int columnIndex) {
			if (columnIndex == 1 || columnIndex == 2 || columnIndex == 3 || columnIndex == 4) {
				return true;
			}
			return false;
		}

		@Override
		public Object getSumValueAt(int columnIndex) {
			Object res = null;
			switch (columnIndex) {
			case 1:
				res = new Date();
				break;
			case 2:
				res = Function.sumFunction(true);
				break;
			case 3:
				res = Function.sumFunction(true);
				break;
			case 4:
				res = Function.sumFunction(true);
				break;
			default:
				res = null;
				break;
			}
			return res;
		}

		@Override
		public Class<?> getSumColumnClass(int columnIndex) {
			Class<?> res = null;
			switch (columnIndex) {
			case 1:
				res = Date.class;
				break;
			case 2:
				res = Integer.class;
				break;
			case 3:
				res = Double.class;
				break;
			case 4:
				res = BigDecimal.class;
				break;
			default:
				res = Object.class;
				break;
			}
			return res;
		}
	}

	private static class DateHourCellRenderer extends JLabel implements TableCellRenderer {

		private static final long serialVersionUID = 7388584552867300961L;

		public DateHourCellRenderer() {
			super("", JLabel.LEFT);
			this.setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			} else {
				setBackground(table.getBackground());
				setForeground(table.getForeground());
			}
			if (value != null && value instanceof Date) {
				Date date = (Date) value;
				this.setText(DateTimeUtils.formatLongDate(date));
			} else {
				this.setText(null);
			}
			return this;
		}
	}

	public static class MyExcelColumnRenderer extends AbstractExcelColumnRenderer {

		private XSSFCellStyle cellStyle;

		@Override
		public void prepareColumn(TableModel tableModel, XSSFSheet sheet, int col) {
			XSSFWorkbook wb = sheet.getWorkbook();
			XSSFDataFormat df = wb.createDataFormat();

			cellStyle = wb.createCellStyle();
			XSSFFont f = wb.createFont();
			f.setFontHeightInPoints((short) 12);
			f.setBold(true);
			f.setColor(IndexedColors.BLACK.getIndex());

			cellStyle.setFont(f);
			cellStyle.setDataFormat(df.getFormat("text"));
			cellStyle.setBorderTop(CellStyle.BORDER_THIN);
			cellStyle.setBorderColor(BorderSide.TOP, new XSSFColor(new Color(0, 0, 0)));
			cellStyle.setBorderRight(CellStyle.BORDER_THIN);
			cellStyle.setBorderColor(BorderSide.RIGHT, new XSSFColor(new Color(0, 0, 0)));
			cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
			cellStyle.setBorderColor(BorderSide.BOTTOM, new XSSFColor(new Color(0, 0, 0)));
			cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
			cellStyle.setBorderColor(BorderSide.LEFT, new XSSFColor(new Color(0, 0, 0)));
		}

		@Override
		public void setWritableCell(TableModel tableModel, XSSFCell cell, Object value, int row, int col) {
			XSSFWorkbook wb = cell.getSheet().getWorkbook();

			cell.setCellStyle(cellStyle);
			cell.setCellValue(wb.getCreationHelper().createRichTextString(value.toString()));
			cell.setCellType(XSSFCell.CELL_TYPE_STRING);
		}
	}

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		MySyTableModel model = new MySyTableModel();
		JSyTable table = new JSyTable(model, "TestSyTable2");
		table.setShowTableLines(true);
		table.setShowTableFooter(true);
		table.setShowToolTipsColumns(true);

		table.addDefaultPerspective("Inverser les colonnes", JSyTable.AUTO_RESIZE_ALL_COLUMNS, new int[] { 4, 3, 2, 1, 0, 5 }, new boolean[] { true, true, true, true, true });

		SyTableColumn tc0 = (SyTableColumn) table.getYColumnModel().getColumn(0, true);
		tc0.setComparator(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (o1 != null && o2 != null) {
					Integer i1 = Integer.decode(o1.substring("String_".length()));
					Integer i2 = Integer.decode(o2.substring("String_".length()));
					return i1.compareTo(i2);
				}
				return o1 != null ? 0 : 1;
			}
		});

		table.setDefaultRenderer(Date.class, new DateHourCellRenderer());

		SyTableColumn tc6 = (SyTableColumn) table.getYColumnModel().getColumn(6, true);
		tc6.setCellRenderer(new HourCellRenderer());
		tc6.setExcelColumnRenderer(new HourExcelColumnRenderer());
		tc6.setFilter(new HourFilterColumn());

		SyTableColumn tc7 = (SyTableColumn) table.getYColumnModel().getColumn(7, true);
		tc7.setExcelColumnRenderer(new MyExcelColumnRenderer());

		frame.getContentPane().add(new JSyScrollPane(table), BorderLayout.CENTER);

		frame.setTitle("TestSyTable");
		frame.pack();
		frame.setSize(800, 600);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}
