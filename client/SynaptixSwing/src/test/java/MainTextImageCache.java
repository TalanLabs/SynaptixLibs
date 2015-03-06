import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.synaptix.swing.utils.OldTextImageCacheFactory;
import com.synaptix.swing.utils.OldTextImageCacheFactory.ImageRect;

public class MainTextImageCache extends JComponent {

	private static final long serialVersionUID = 8773548445231104852L;

	private OldTextImageCacheFactory textImageFactory = new OldTextImageCacheFactory();

	private String text = "Grand Boulevard\nSaint Lazare";

	private Font font = new Font("Arial", Font.BOLD, 50);

	private double angle = 0;

	private int alignement = SwingConstants.LEFT;

	public MainTextImageCache(double angle) {
		super();

		this.angle = angle;
	}

	public Dimension getPreferredSize() {
		return textImageFactory.getImageRect(text, font, getForeground(), null, angle, alignement).getRectangle().getSize();
	}

	protected void paintComponent(Graphics g) {
		ImageRect ir = textImageFactory.getImageRect(text, font, getForeground(), null, angle, alignement);
		textImageFactory.paintRotateText(g, -ir.getRectangle().x, -ir.getRectangle().y, text, font, getForeground(), null, angle, alignement);
	}

	private static JComponent createText(double angle) {
		MainTextImageCache c = new MainTextImageCache(angle);
		c.setBorder(BorderFactory.createLineBorder(Color.red, 2));
		return c;
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout());
		// frame.getContentPane().add(createText(0), BorderLayout.NORTH);
		frame.getContentPane().add(createText(-Math.PI / 4), BorderLayout.SOUTH);

		frame.pack();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}