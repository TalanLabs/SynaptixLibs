import helper.MainHelper;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JCircleWait;

public class MainCircleWait {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				JFrame frame = MainHelper.createFrame();

				JCircleWait circleWait = new JCircleWait();
				circleWait.start();
				frame.getContentPane().add(circleWait, BorderLayout.CENTER);

				frame.setSize(800, 600);
				frame.setVisible(true);

				circleWait.start();
			}
		});
	}
}
