import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JSelectionCalendar;
import com.synaptix.swing.utils.Manager;

public class TestSelectionCalendar {

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		final JSelectionCalendar sc = new JSelectionCalendar();
		sc.addHolidayDate(new Date());
		
		JButton button = new JButton("GET");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(sc.getTextSelectedDates());
				System.out.println(sc.getExpression());
			}
		});

		frame.getContentPane().add(sc, BorderLayout.CENTER);
		frame.getContentPane().add(button, BorderLayout.SOUTH);

		frame.setTitle("TestCalendar");
		frame.pack();
		frame.setSize(800, 600);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}

}
