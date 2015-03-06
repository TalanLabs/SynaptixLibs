import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JImageCropping;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.swing.utils.Manager;

public class TestImageCropping {

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		final JImageCropping imageCropping = new JImageCropping();
		imageCropping.setShowCropping(false);
		JButton fileButton = new JButton("Choose...");
		fileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser dialog = new JFileChooser();
				if (dialog.showOpenDialog(GUIWindow.getActiveWindow()) == JFileChooser.APPROVE_OPTION) {
					File file = dialog.getSelectedFile();
					
					try {
						imageCropping.setImage(ImageIO.read(file));
					} catch (IOException e2) {
					}
					//imageCropping.setImagePath(file.getAbsolutePath());
				}
			}
		});

		frame.getContentPane().add(fileButton, BorderLayout.NORTH);
		frame.getContentPane().add(imageCropping, BorderLayout.CENTER);

		frame.setTitle("TestPlanning");
		frame.pack();
		// frame.setSize(300, 400);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}
