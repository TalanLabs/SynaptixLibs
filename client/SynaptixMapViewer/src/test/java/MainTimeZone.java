import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MainTimeZone {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		final int size = 512;

		File dir = new File("D:/temp/world/tz/");

		final BufferedImage bi = ImageIO.read(new File(dir, "toto.png"));

		final Map<Integer, String> tzidMap = new HashMap<Integer, String>();

		BufferedReader br = new BufferedReader(new FileReader(new File(dir, "toto.tz")));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] ls = line.split("\\|");
			tzidMap.put(Integer.parseInt(ls[1]), ls[0]);
		}

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout());

		final JLabel label = new JLabel(new ImageIcon(new File(dir, "final.png").getAbsolutePath()));
		label.setBorder(BorderFactory.createLineBorder(Color.red));

		label.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int w = label.getWidth();
				int h = label.getHeight();

				int xd = (w - size) / 2;
				int yd = (h - size) / 2;

				int x = e.getX() - xd;
				int y = e.getY() - yd;

				if (x >= 0 && y >= 0 && x < size && y < size) {
					int rgb = bi.getRGB(x, y) - 0xFF000000;
					System.out.println(tzidMap.get(Integer.valueOf(rgb)));
				}
			}
		});

		frame.getContentPane().add(label, BorderLayout.NORTH);

		frame.setSize(600, 600);
		frame.setVisible(true);
	}
}
