import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JClip;
import com.synaptix.swing.utils.Manager;

public class TestClip {

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame("Tests");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JClip clip = new JClip();
		clip.setAlpha(0.5f);
		clip.setSize(256, 256);
		
		clip.setBounds(0, 0, 256, 256);
		
		frame.getContentPane().add(clip);
		
		frame.pack();
		frame.setSize(600, 600);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}
