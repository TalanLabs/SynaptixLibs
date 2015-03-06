import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JCircleWait;

public class MainCircleWait {

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JCircleWait c = new JCircleWait();
		c.setWaitHeight(24);
		c.setTraineColor(Color.red);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(c, BorderLayout.WEST);
		frame.getContentPane().add(new JLabel("Chargement en cours"), BorderLayout.CENTER);

		frame.pack();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);

				c.start();
			}
		});
	}
}
