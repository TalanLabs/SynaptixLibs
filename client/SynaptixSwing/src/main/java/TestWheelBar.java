import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JWheelBar;
import com.synaptix.swing.utils.Manager;

public class TestWheelBar {

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		frame.getContentPane().add(new JWheelBar(JWheelBar.HORIZONTAL), BorderLayout.EAST);
		frame.getContentPane().add(new JWheelBar(JWheelBar.VERTICAL), BorderLayout.SOUTH);

		frame.setTitle("TestWheelBar");
		// frame.pack();
		frame.setSize(600, 100);
			
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}
