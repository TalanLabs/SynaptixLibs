import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.WaitFullComponentSwingWorker;
import com.synaptix.swing.utils.Manager;

public class TestFullWaitComponentFeedback {

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		final WaitComponentFeedbackPanel w = new WaitComponentFeedbackPanel();
		w.addContent(new JScrollPane(new JTable(10, 10)));

		frame.getContentPane().add(w, BorderLayout.CENTER);
		frame.getContentPane().add(new JButton("Rien"), BorderLayout.WEST);

		frame.setTitle("TestWaitComponent");
		// frame.pack();
		frame.setSize(500, 300);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);

				new WaitFullComponentSwingWorker<Void>(w) {

					protected Void doInBackground() throws Exception {
						publish("J'attent 1...");
						System.out.println("attends 1");
						Thread.sleep(10000);
						return null;
					}

					protected void done() {
						try {
							get();

							System.out.println("fini 1");
						} catch (Exception e) {

						}
					}
				}.execute();
			}
		});
	}
}
