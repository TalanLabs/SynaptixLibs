package com.synaptix.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class JClip extends JComponent {

	private static final long serialVersionUID = -6003983543364156620L;

	private static final RenderingHints hints;

	private Image image;

	private float alpha;

	private int z;

	static {
		hints = new RenderingHints(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	}

	public JClip() {
		this(null, 1.0f);
	}

	public JClip(Image image) {
		this(image, 1.0f);
	}

	public JClip(Image image, float alpha) {
		super();

		this.image = image;
		this.alpha = alpha;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		Image oldValue = getImage();
		this.image = image;
		firePropertyChange("image", oldValue, image); //$NON-NLS-1$
		repaint();
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		float oldValue = getAlpha();
		this.alpha = alpha;
		firePropertyChange("alpha", oldValue, alpha); //$NON-NLS-1$
		repaint();
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		int oldValue = getZ();
		this.z = z;
		firePropertyChange("z", oldValue, z); //$NON-NLS-1$
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (image != null && alpha > 0.0f) {
			g2.setRenderingHints(hints);
			if (alpha < 1.0f) {
				AlphaComposite composite = AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, alpha);
				g2.setComposite(composite);
			}

			g2.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
		} else {
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
		}
	}
}
