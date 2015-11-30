import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JTriangleWait;

import helper.MainHelper;

public class MainTriangleWait {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				JFrame frame = MainHelper.createFrame();

				JTriangleWait triangleWait = new JTriangleWait(JTriangleWait.FPS, true);
				triangleWait.setNbTriangle(20);
				triangleWait.setTraineLenght(20);
				triangleWait.setTraineColor(new Color(0, 0, 127));
				triangleWait.start();
				frame.getContentPane().add(triangleWait, BorderLayout.CENTER);

				frame.setSize(800, 600);
				frame.setVisible(true);

				triangleWait.start();
			}
		});
	}
}
