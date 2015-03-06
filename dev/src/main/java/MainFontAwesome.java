import helper.MainHelper;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.utils.FontAwesomeHelper;

public class MainFontAwesome {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				JFrame frame = MainHelper.createFrame();
				JButton button = new JButton("Coucou", FontAwesomeHelper.getIcon("fa-arrow-left", 50, Color.DARK_GRAY));
				// button.setFont(FontAwesomeHelper.deriveFont(50.0f));
				frame.getContentPane().add(button);
				frame.setVisible(true);
			}
		});

	}

}
