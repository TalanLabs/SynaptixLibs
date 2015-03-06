import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JSyTabbedPane;
import com.synaptix.swing.utils.Manager;

public class TestSyTabbedPane {

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame("Tests");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		JSyTabbedPane tabbed = new JSyTabbedPane();
		tabbed.addTab("Tabbed1", new JPanel());
		tabbed.addTab("Total", new JButton("Total"));
		tabbed.addTab("Wen Juan", new JPanel());
		tabbed.addTab("Encore et toujours", new JButton(
				"Encore et toujour"));
		
		frame.getContentPane().add(tabbed,BorderLayout.CENTER);
		
		frame.pack();
		// frame.setSize(300, 400);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}
