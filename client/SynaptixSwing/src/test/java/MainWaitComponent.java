import java.awt.BorderLayout;
import java.awt.Color;
import java.util.concurrent.CancellationException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import com.synaptix.client.view.AbstractViewWorker;
import com.synaptix.client.view.IWaitWorker;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.WaitComponentSwingWorker;
import com.synaptix.view.swing.WaitSilentSwingViewWorker;

public class MainWaitComponent {

	static JLabel label1;

	static JLabel label2;

	private static JLabel createLabel(String text) {
		JLabel label = new JLabel(text);
		label.setOpaque(true);
		label.setBackground(Color.red);
		label.setForeground(Color.white);
		// label.setSize(label.getPreferredSize());
		return label;
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		final WaitComponentFeedbackPanel w = new WaitComponentFeedbackPanel();
		w.addContent(new JScrollPane(new JTable(10, 10)));

		frame.getContentPane().add(w, BorderLayout.CENTER);

		frame.setTitle("TestWaitComponent");
		// frame.pack();
		frame.setSize(500, 300);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);

				final IWaitWorker ww = WaitSilentSwingViewWorker.waitSilentSwingViewWorker(new AbstractViewWorker<Void, String>() {
					public Void doBackground() throws Exception {
						publish("Chargement en cours...");
						Thread.sleep(5000);
						publish("Sauvegarde en cours...");
						Thread.sleep(5000);
						publish("Terminé");
						return null;
					}

					public void success(Void e) {
						System.out.println("ici");
					}

					public void fail(Throwable t) {
						if (t instanceof CancellationException) {
							System.out.println("cancel");
						} else {
							t.printStackTrace();
						}
					}

				});

				new WaitComponentSwingWorker<Void>(w) {

					protected Void doInBackground() throws Exception {
						Thread.sleep(2000);
						return null;
					}

					protected void done() {
						try {
							get();

							System.out.println(ww.isCancelled() + " " + ww.isDone());
							ww.cancel(true);
							System.out.println(ww.isCancelled() + " " + ww.isDone());
						} catch (Exception e) {

						}
					}
				}.execute();
			}
		});
	}
}
