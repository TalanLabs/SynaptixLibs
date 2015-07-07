import helper.MainHelper;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JTitle;

public class MainTitle {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				JFrame frame = MainHelper.createFrame();

				JTitle title = new JTitle("C'est moi gabriel");
				title.startAnimation();
				frame.getContentPane().add(title, BorderLayout.NORTH);
				frame.getContentPane().add(MainHelper.createTable(), BorderLayout.CENTER);

				frame.setSize(800, 600);
				frame.setVisible(true);
			}
		});
	}
}
