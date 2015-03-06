import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JCompositionPanel;
import com.synaptix.swing.JTileBack;
import com.synaptix.swing.utils.Toolkit;

public class MainTileBack {

	private static JTileBack tileBack;

	private static JLabel label;

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tileBack = new JTileBack(Toolkit.getImageWithAlpha(new ImageIcon(MainTileBack.class.getResource("/com/synaptix/swing/important.png")).getImage(), 0.25f), 12);
		tileBack.setDirectionX(1);
		tileBack.setDirectionY(1);

		label = new JLabel("COUCOU VOILAAAAAAAAA");
		label.setFont(new Font("Arial", Font.BOLD, 25));
		label.setForeground(Color.black);

		JCompositionPanel c = new JCompositionPanel();
		c.add(tileBack, 1);
		c.add(label, 2);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(c, BorderLayout.CENTER);

		frame.setSize(300, 300);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);

				tileBack.startAnimation();
			}
		});
	}
}
