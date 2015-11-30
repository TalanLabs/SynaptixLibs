import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JWaitGlassPane;

import helper.MainHelper;

public class MainWaitGlassPane {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				JFrame frame = MainHelper.createFrame();

				JWaitGlassPane waitGlassPane = new JWaitGlassPane();
				waitGlassPane.setText("Coucou");
				frame.setGlassPane(waitGlassPane);

				frame.getContentPane().add(MainHelper.createTable(), BorderLayout.CENTER);

				frame.setSize(800, 600);
				frame.setVisible(true);

				waitGlassPane.start();
			}
		});
	}
}
