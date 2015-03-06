import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.synaptix.swing.AbstractGroupWeekModel;
import com.synaptix.swing.JGroupWeek;

public class MainGroupWeek {

	private static class MyGroupWeekModel extends AbstractGroupWeekModel {

		public int getGroupCount() {
			return 26;
		}

		public int getGroupRowCount(int group) {
			return 2;
		}

		public Object getValue(int group, int row, int day) {
			return "Gaby";
		}

		public boolean isSelected(int group, int row, int day) {
			return true;
		}
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JGroupWeek groupWeek = new JGroupWeek(new MyGroupWeekModel());

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JScrollPane(groupWeek), BorderLayout.CENTER);

		frame.pack();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}
