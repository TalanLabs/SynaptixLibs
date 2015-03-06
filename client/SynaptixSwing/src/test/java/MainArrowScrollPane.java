import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import com.synaptix.swing.JArrowScrollPane;

public class MainArrowScrollPane {

	private static final class MyTableModel extends AbstractTableModel {

		public int getColumnCount() {
			return 100;
		}

		public int getRowCount() {
			return 100;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return rowIndex + columnIndex;
		}
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTable table = new JTable(new MyTableModel());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setBorder(BorderFactory.createLineBorder(Color.red, 10));

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JLabel("Choisir un parcours"), BorderLayout.NORTH);
		frame.getContentPane().add(new JArrowScrollPane(table), BorderLayout.CENTER);

		frame.pack();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}