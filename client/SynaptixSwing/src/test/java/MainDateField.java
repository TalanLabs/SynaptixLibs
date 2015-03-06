import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.utils.DateTimeUtils;
import com.synaptix.swing.utils.Manager;
import com.synaptix.swing.utils.SwingComponentFactory;

public class MainDateField {

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		final JFormattedTextField dateField = SwingComponentFactory
				.createDateField(false, false);
		dateField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("ici "
						+ DateTimeUtils
								.formatShortDatabaseDate((Date) dateField
										.getValue()));
			}
		});

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(dateField, BorderLayout.CENTER);

		frame.setTitle("TestPlanning");
		frame.pack();
		// frame.setSize(300, 400);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}

}
