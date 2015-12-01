import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.tuple.Pair;
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
import com.synaptix.swing.widget.HourExcelColumnRenderer;

import helper.MainHelper;

public class MainSyTable {

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

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 7;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			super.setValueAt(aValue, rowIndex, columnIndex);
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

	public static class MyDragMeMouseAdapter extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			JComponent c = (JComponent) e.getSource();
			TransferHandler handler = c.getTransferHandler();
			handler.exportAsDrag(c, e, TransferHandler.COPY);
		}
	}

	public static class GridCellTransferable implements Transferable {

		public final static DataFlavor TEST_FLAVOR;

		private final static DataFlavor[] FLAVORS;

		private Pair<String, Dimension> pair;

		static {
			DataFlavor flavor = null;
			FLAVORS = new DataFlavor[1];
			try {
				flavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + String.class.getName());
				FLAVORS[0] = flavor;
			} catch (ClassNotFoundException e) {
				FLAVORS[0] = null;
			}
			TEST_FLAVOR = flavor;
		}

		public GridCellTransferable(String value, Dimension dim) {
			super();

			this.pair = Pair.of(value, dim);
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (!flavor.equals(TEST_FLAVOR)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return pair;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return FLAVORS;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor.equals(TEST_FLAVOR);
		}
	}

	private static class MyDragMeTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 5111950707141828591L;

		private Random rand = new Random(0);

		private int v = 0;

		@Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY;
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			return new GridCellTransferable("DragMe" + String.valueOf(v++), new Dimension(rand.nextInt(6) + 1, rand.nextInt(6) + 1));
		}
	}

	private static class MyDropMeTransferHandler extends TransferHandler {

		@Override
		public boolean canImport(TransferSupport support) {
			return true;
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				final JFrame frame = MainHelper.createFrame();
				frame.getContentPane().setLayout(new BorderLayout());

				JLabel label = new JLabel("DRAG ME", JLabel.CENTER);
				label.setOpaque(true);
				label.setBackground(Color.LIGHT_GRAY);
				label.setPreferredSize(new Dimension(100, 50));
				label.setTransferHandler(new MyDragMeTransferHandler());
				label.addMouseListener(new MyDragMeMouseAdapter());
				frame.getContentPane().add(label, BorderLayout.NORTH);

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

				SyTableColumn tc6 = (SyTableColumn) table.getYColumnModel().getColumn(6, true);
				tc6.setCellRenderer(new HourCellRenderer());
				tc6.setExcelColumnRenderer(new HourExcelColumnRenderer());
				tc6.setFilter(new HourFilterColumn());

				SyTableColumn tc7 = (SyTableColumn) table.getYColumnModel().getColumn(7, true);
				tc7.setExcelColumnRenderer(new MyExcelColumnRenderer());

				tc7.setCellEditor(new MyEditor());

				table.setTransferHandler(new MyDropMeTransferHandler());

				frame.getContentPane().add(new JSyScrollPane(table), BorderLayout.CENTER);

				frame.pack();
				frame.setSize(800, 600);

				frame.setVisible(true);
			}
		});
	}

	private static class MyEditor extends DefaultCellEditor {

		private DefaultComboBoxModel model;

		public MyEditor() {
			super(new JComboBox());

			model = new DefaultComboBoxModel();
			((JComboBox) getComponent()).setModel(model);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			model.removeAllElements();
			for (int i = 0; i < 5; i++) {
				int j = ((Integer) value).intValue();
				model.addElement(j + i);
			}

			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}
	}
}
