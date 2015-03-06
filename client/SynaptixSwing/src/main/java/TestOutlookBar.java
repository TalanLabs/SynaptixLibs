import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JOutlookBar;
import com.synaptix.swing.utils.Manager;

public class TestOutlookBar {

	private static JPanel getDummyPanel(String name) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(name, JLabel.CENTER));
		return panel;
	}

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();
		
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		JOutlookBar outlookBar = new JOutlookBar();
		outlookBar.addBar("One", getDummyPanel("One"));
		outlookBar.addBar("Two", getDummyPanel("Two"));
		outlookBar.addBar("Three", getDummyPanel("Three"));
		outlookBar.addBar("Four", getDummyPanel("Four"));
		outlookBar.addBar("Five", getDummyPanel("Five"));
		outlookBar.setVisibleBar(2);
		frame.getContentPane().add(outlookBar, BorderLayout.CENTER);

		frame.setTitle("TestOutlookBar");
		frame.setSize(800, 600);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}

}
