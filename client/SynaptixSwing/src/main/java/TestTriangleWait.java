import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JTriangleWait;
import com.synaptix.swing.utils.Manager;

public class TestTriangleWait {

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		final JTriangleWait t = new JTriangleWait(10, true);
		t.setNbTriangle(15);
		t.setTraineLenght(10);
		t.setTraineColor(Color.gray);
		frame.getContentPane().add(t, BorderLayout.CENTER);

		frame.setTitle("TestWaitComponent");
		// frame.pack();
		frame.setSize(500, 300);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
				
				t.start();
			}
		});
	}
}
