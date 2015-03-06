import helper.MainHelper;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class MainTabbedPane {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				JFrame frame = MainHelper.createFrame();
				frame.getContentPane().setLayout(new BorderLayout());

				JTabbedPane tp = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
				for (int i = 0; i < 3; i++) {
					tp.addTab("Test_" + i, new JScrollPane(new JTable(new DefaultTableModel(100, 10))));

					// tp.setTabComponentAt(i, component);
				}
				frame.getContentPane().add(tp);

				frame.setVisible(true);
			}
		});
	}

}
