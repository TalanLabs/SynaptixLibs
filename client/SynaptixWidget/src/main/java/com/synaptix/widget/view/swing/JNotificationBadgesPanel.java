package com.synaptix.widget.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.synaptix.swing.JCompositionPanel;

public class JNotificationBadgesPanel extends JPanel {

	private static final long serialVersionUID = -4494745983788831485L;

	private static final Image img = new ImageIcon(JNotificationBadgesPanel.class.getResource("/com/synaptix/widget/actions/view/swing/iconBadge.png")).getImage();

	private static final Font font = new Font(Font.DIALOG, Font.BOLD, 15);

	private Map<JComponent, String> map;

	private JComponent component;

	private MyBadges badges;

	public JNotificationBadgesPanel(JComponent component) {
		super(new BorderLayout());

		this.component = component;
		map = new HashMap<JComponent, String>();
		badges = new MyBadges();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private JComponent buildContents() {
		JCompositionPanel panel = new JCompositionPanel();
		panel.add(component, new Integer(0));
		panel.add(badges, new Integer(1));
		return panel;
	}

	/**
	 * Permet d'ajoute une notification à un composant
	 * 
	 * @param c
	 * @param value
	 */
	public void setNotifivationBadge(JComponent c, String value) {
		if (value == null) {
			map.remove(c);
		} else {
			map.put(c, value);
		}
		repaint();
	}

	public String getNotifivationBadge(JComponent c) {
		return map.get(c);
	}

	public void clear() {
		map.clear();
		repaint();
	}

	private final class MyBadges extends JComponent {

		private static final long serialVersionUID = 8914939794047985232L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			for (Entry<JComponent, String> entry : map.entrySet()) {
				paintBadge(g2, entry.getKey(), entry.getValue());
			}

			g2.dispose();
		}

		private void paintBadge(Graphics2D g2, JComponent c, String value) {
			Rectangle2D rect = font.getStringBounds(value, g2.getFontRenderContext());

			int tw = (int) rect.getWidth() + 10;
			tw = tw < 20 ? 20 : tw;

			Point p = SwingUtilities.convertPoint(c, 0, 0, this);

			int x = p.x + c.getWidth() - tw * 2 / 3;
			int y = p.y - 20 * 1 / 3;

			paintBadge(g2, x, y, tw);

			int m = (tw - (int) rect.getWidth()) / 2;

			g2.setColor(Color.white);
			g2.setFont(font);
			g2.drawString(value, x + m, y - (int) rect.getY());
		}

		private void paintBadge(Graphics2D g2, int x, int y, int w) {
			int dw = w < 20 ? 20 : w;
			int m = dw - 20;

			g2.drawImage(img, x, y, x + 10, y + 20, 0, 0, 10, 20, null);
			x += 10;
			if (m > 0) {
				g2.drawImage(img, x, y, x + m, y + 20, 10, 0, 11, 20, null);
				x += m;
			}
			g2.drawImage(img, x, y, x + 10, y + 20, 10, 0, 20, 20, null);
		}
	}
}
