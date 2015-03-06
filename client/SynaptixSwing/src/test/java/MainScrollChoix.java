import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JArrowScrollPane;

public class MainScrollChoix {

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JLabel("Choisir un parcours"), BorderLayout.NORTH);
		frame.getContentPane().add(new JArrowScrollPane(new MainChoix()), BorderLayout.CENTER);

		// frame.pack();
		frame.setSize(400, 400);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}