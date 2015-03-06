import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import path.ExamplePathModel;

import com.synaptix.swing.JArrowScrollPane;
import com.synaptix.swing.path.JPath;

public class MainPath {

	private static final JPath createPathPanel(String[] lieux, int[] importantNodes) {
		JPath path = new JPath(new ExamplePathModel(lieux, importantNodes));
		path.setBackground(Color.WHITE);
		path.setBorder(BorderFactory.createEtchedBorder());
		return path;
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(
				new JArrowScrollPane(createPathPanel(new String[] { "Paris\nToto", "Bruxelles\nToto", "Grand", "Bruges", "Ostende\nblablbalbalablababab\nhljhjkhjlh" }, new int[] { 0, 1, 4 })),
				BorderLayout.CENTER);

		frame.pack();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}