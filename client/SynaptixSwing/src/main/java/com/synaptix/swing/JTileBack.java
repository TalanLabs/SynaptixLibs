package com.synaptix.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.Timer;

public class JTileBack extends JComponent {

	private static final long serialVersionUID = 4869284051700533568L;

	private static final RenderingHints hints;

	private Image tile;

	private BufferedImage bufferedImage;

	private boolean playAnimation;

	private Timer animationTimer;

	private double directionX;

	private double directionY;

	private double decalageX;

	private double decalageY;

	static {
		hints = new RenderingHints(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);
		hints.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	}

	public JTileBack(Image tile) {
		this(tile, 25);
	}

	public JTileBack(Image tile, float fps) {
		super();

		this.tile = tile;

		this.playAnimation = true;
		this.directionX = 1;
		this.directionY = 1;
		this.decalageX = 0;
		this.decalageY = 0;

		buildBufferedImage();

		this.animationTimer = new Timer((int) (1000.0f / fps),
				new TimerActionListener());
	}

	public void setTile(Image tile) {
		this.tile = tile;
		buildBufferedImage();
		repaint();
	}

	public Image getTile() {
		return tile;
	}

	public void setDirectionX(double directionX) {
		this.directionX = directionX;
	}

	public double getDirectionX() {
		return directionX;
	}

	public void setDirectionY(double directionY) {
		this.directionY = directionY;
	}

	public double getDirectionY() {
		return directionY;
	}

	private void buildBufferedImage() {
		bufferedImage = new BufferedImage(tile.getWidth(null), tile
				.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(tile, 0, 0, null);
		g2.dispose();
	}

	public void setPlayAnimation(boolean playAnimation) {
		this.playAnimation = playAnimation;
		if (animationTimer.isRunning()) {
			animationTimer.stop();
		}
		repaint();
	}

	public void startAnimation() {
		if (playAnimation) {
			animationTimer.start();
		}
	}

	public void stopAnimation() {
		if (playAnimation) {
			animationTimer.stop();
		}
	}

	public boolean isAnimation() {
		return animationTimer.isRunning();
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();

		g2.setRenderingHints(hints);

		int w = this.getWidth();
		int h = this.getHeight();

		int tw = bufferedImage.getWidth();
		int th = bufferedImage.getHeight();

		int nbW = w / tw + 2;
		int nbH = h / th + 2;

		int x = (int) decalageX;
		int y = (int) decalageY;

		for (int j = 0; j < nbH; j++) {
			for (int i = 0; i < nbW; i++) {
				g2.drawImage(bufferedImage, x + i * tw, y + j * th, null);
			}
		}

		g2.dispose();
	}

	private final class TimerActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			decalageX += directionX >= 0 ? -bufferedImage.getWidth()
					+ directionX : directionX;
			if (decalageX <= -bufferedImage.getWidth()) {
				decalageX = bufferedImage.getWidth() + decalageX;
			}

			decalageY += directionY >= 0 ? -bufferedImage.getHeight()
					+ directionY : directionY;
			if (decalageY <= -bufferedImage.getHeight()) {
				decalageY = bufferedImage.getHeight() + decalageY;
			}

			repaint();
		}
	}
}
