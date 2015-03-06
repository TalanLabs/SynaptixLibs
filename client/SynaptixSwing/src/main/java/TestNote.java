import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JNote;
import com.synaptix.swing.utils.Manager;

public class TestNote {

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		JNote note = new JNote();
		note.setType(JNote.Type.warning);
		note.setText("Bonjour, comment allez vous ? Voici un text un peu long pour tester la longueur.\nMaintenant un saut à la ligne");

		frame.getContentPane().add(note, BorderLayout.NORTH);

		frame.setTitle("TestNote");
		frame.pack();
		frame.setSize(800, 600);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}
