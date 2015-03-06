package com.synaptix.swing;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import com.synaptix.swing.utils.Toolkit;

public class JViewCropImage extends JComponent {

	private static final long serialVersionUID = 7454008562787110526L;

	public final static Color COLOR_DEFAULT_CROP;
	
	private Color colorCrop;
	
	private Image image;

	private int startX;

	private int startY;

	private int endX;

	private int endY;

	static {
		COLOR_DEFAULT_CROP = new Color(127,127,127);
	}
	
	public JViewCropImage() {
		image = null;
		colorCrop = COLOR_DEFAULT_CROP;
		
		startX = 0;
		startY = 0;
		endX = 0;
		endY = 0;

		this.setPreferredSize(new Dimension(512, 512));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (image != null) {
			Rectangle2D rect = Toolkit.getImageScaleForComponent(image, this,
					false);

			g.drawImage(image, (int) rect.getX(), (int) rect.getY(), (int) rect
					.getWidth(), (int) rect.getHeight(), null);

			double dw = rect.getWidth() / image.getWidth(null);
			double dh = rect.getHeight() / image.getHeight(null);

			g.setColor(colorCrop);

			// Left
			g.fillRect((int) rect.getX(), (int) rect.getY(),
					(int) ((double) startX * dw), (int) rect.getHeight());

			// Up
			g.fillRect((int) rect.getX(), (int) rect.getY(), (int) rect
					.getWidth(), (int) ((double) startY * dh));

			// Right
			g.fillRect((int) (rect.getX() + rect.getWidth())
					- (int) ((double) endX * dw), (int) rect.getY(),
					(int) ((double) endX * dw), (int) rect.getHeight());

			// Down
			g.fillRect((int) rect.getX(),
					(int) (rect.getY() + rect.getHeight())
							- (int) ((double) endY * dh),
					(int) rect.getWidth(), (int) ((double) endY * dh));
		}
	}

	public int getEndX() {
		return endX;
	}

	public void setEndX(int endX) {
		this.endX = endX;
		repaint();
	}

	public int getEndY() {
		return endY;
	}

	public void setEndY(int endY) {
		this.endY = endY;
		repaint();
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
		repaint();
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
		repaint();
	}

	public Color getColorCrop() {
		return colorCrop;
	}

	public void setColorCrop(Color colorCrop) {
		this.colorCrop = colorCrop;
		repaint();
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
		repaint();
	}
}
