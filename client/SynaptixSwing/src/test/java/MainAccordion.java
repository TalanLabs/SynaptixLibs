import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.synaptix.swing.JAccordion;

public class MainAccordion {

	/**
	 * Debug, dummy method
	 */
	private static JPanel getDummyPanel(String name) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(name, JLabel.CENTER));
		return panel;
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JAccordion outlookBar = new JAccordion(JAccordion.Direction.VERTICAL_UP);
		outlookBar.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				System.out.println(outlookBar.getVisibleBar());
			}
		});

		outlookBar.addBar("One", getDummyPanel("<html>J<br>b<br>u<br>t<br>t<br>o<br>n<br>1</html>"));
		outlookBar.addBar("Two", getDummyPanel("Two"));
		outlookBar.addBar("Three", getDummyPanel("Three"));
		outlookBar.addBar("Four", getDummyPanel("Four"));
		outlookBar.addBar("Five", getDummyPanel("Five"));

		// outlookBar.setVisibleBar(2);
		// outlookBar.setPreferredSize(new Dimension(400, 400));

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(outlookBar, BorderLayout.CENTER);

		frame.setSize(400, 100);
		// frame.pack();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}

}
