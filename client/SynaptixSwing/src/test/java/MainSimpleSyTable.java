import java.awt.BorderLayout;
import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.TableCellRenderer;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.combo.WidestComboPopupPrototype;
import org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel;

import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.table.AbstractSimpleSpecialTableModel;
import com.synaptix.swing.table.Column;
import com.synaptix.swing.table.SimpleColumn;
import com.synaptix.swing.utils.DateTimeUtils;
import com.synaptix.swing.utils.Manager;

public class MainSimpleSyTable {

	private static class MySyTableModel extends AbstractSimpleSpecialTableModel {

		private static final long serialVersionUID = -878825691309640954L;

		private List<Column> columns;

		public MySyTableModel() {
			columns = new ArrayList<Column>();
			for (int i = 0; i < 5; i++) {
				Class<?> clazz = Object.class;
				switch (i) {
				case 0:
					clazz = String.class;
					break;
				case 1:
					clazz = Date.class;
					break;
				case 2:
					clazz = Integer.class;
					break;
				case 3:
					clazz = Double.class;
					break;
				case 4:
					clazz = BigDecimal.class;
					break;
				case 5:
					clazz = Boolean.class;
					break;
				}
				columns.add(new SimpleColumn(String.valueOf(i), "Column "
						+ String.valueOf(i), clazz, i % 2 == 0));
			}
		}

		public int getColumnCount() {
			return columns.size();
		}

		public Column getColumn(int index) {
			return columns.get(index);
		}

		public int getRowCount() {
			return 40;
		}

		public Object getValueAt(int rowIndex, String columnId) {
			Object res = null;
			switch (Integer.valueOf(columnId)) {
			case 0:
				if (rowIndex % 5 == 0)
					res = null;
				else
					res = "String_" + rowIndex;
				break;
			case 1:
				Calendar c = Calendar.getInstance();
				c.add(Calendar.HOUR_OF_DAY, rowIndex * 3);
				if (rowIndex % 5 == 2)
					res = null;
				else
					res = c.getTime();
				break;
			case 2:
				if (rowIndex % 5 == 3)
					res = null;
				else
					res = rowIndex;
				break;
			case 3:
				res = Math.exp(rowIndex) * 3.4;
				break;
			case 4:
				res = rowIndex;
				break;
			case 5:
				if (rowIndex % 5 == 4)
					res = true;
				else
					res = false;
				break;
			}
			return res;
		}
	}

	private static class DateHourCellRenderer extends JLabel implements
			TableCellRenderer {

		private static final long serialVersionUID = 7388584552867300961L;

		public DateHourCellRenderer() {
			super("", JLabel.LEFT);
			this.setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					// Properties props = new Properties();
					//			props.put("logoString", "Opti"); //$NON-NLS-1$ //$NON-NLS-2$
					// GabyLookAndFeel.setCurrentTheme(props);
					// UIManager.setLookAndFeel(new GabyLookAndFeel());
					//
					UIManager
							.setLookAndFeel(new SubstanceBusinessLookAndFeel());
					UIManager.put(SubstanceLookAndFeel.COMBO_POPUP_PROTOTYPE,
							new WidestComboPopupPrototype());
					JFrame.setDefaultLookAndFeelDecorated(true);
				} catch (UnsupportedLookAndFeelException e) {
					Manager.installLookAndFeelWindows();
				}

				final JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.getContentPane().setLayout(new BorderLayout());

				MySyTableModel model = new MySyTableModel();
				JSyTable table = new JSyTable(model, "TestSimpleSyTable");
				table.setShowTableLines(true);
				table.setShowTableFooter(true);
				table.setShowToolTipsColumns(true);

				table.addDefaultPerspective("Inverser les colonnes",
						JSyTable.AUTO_RESIZE_ALL_COLUMNS, new int[] { 4, 3, 2,
								1, 0 }, new boolean[] { true, true, true, true,
								true });

				// ((SyTableColumnModel)table.getColumnModel()).getColumn(0,
				// true).setCellEditor(cellEditor);

				table.setDefaultRenderer(Date.class, new DateHourCellRenderer());

				frame.getContentPane().add(new JSyScrollPane(table),
						BorderLayout.CENTER);

				frame.setTitle("TestSimpleSyTable");
				frame.pack();
				frame.setSize(800, 600);

				frame.setVisible(true);
			}
		});
	}
}
