package com.synaptix.swing.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;

public class TextImageCacheFactory {

	private Map<Key, SoftReference<ImageRect>> rotateImageCacheMap;

	public TextImageCacheFactory() {
		rotateImageCacheMap = Collections
				.synchronizedMap(new HashMap<Key, SoftReference<ImageRect>>());
	}

	/**
	 * Dessine l'image à la position x et y
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param text
	 *            le text a générer, les saut de ligne sont pris en compte
	 * @param font
	 * @param color1
	 *            la couleur du fill, peut etre null
	 * @param color2
	 *            la couleur du contour, peut etre null
	 */
	public void paintRotateText(Graphics g, int x, int y, String text,
			Font font, Color color1, Color color2, double angle) {
		paintRotateText(g, x, y, text, font, color1, color2, angle,
				SwingConstants.LEFT);
	}

	/**
	 * Dessine l'image à la position x et y
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param text
	 *            le text a générer, les saut de ligne sont pris en compte
	 * @param font
	 * @param color1
	 *            la couleur du fill, peut etre null
	 * @param color2
	 *            la couleur du contour, peut etre null
	 * @param alignement
	 *            SwingConstants.LEFT, SwingConstants.CENTER,
	 *            SwingConstants.RIGHT
	 */
	public void paintRotateText(Graphics g, int x, int y, String text,
			Font font, Color color1, Color color2, double angle, int alignement) {
		ImageRect imageRect = getImageRect(text, font, color1, color2, angle,
				alignement);
		if (imageRect != null) {
			Rectangle rect = imageRect.rectangle;
			g.drawImage(imageRect.image, x + rect.x, y + rect.y, null);
		}
	}

	/**
	 * Renvoie l'image et la taille de l'image généré
	 * 
	 * @param text
	 *            le text a générer, les saut de ligne sont pris en compte
	 * @param font
	 * @param color1
	 *            la couleur du fill, peut etre null
	 * @param color2
	 *            la couleur du contour, peut etre null
	 * @param angle
	 *            en radian
	 * @return
	 */
	public ImageRect getImageRect(String text, Font font, Color color1,
			Color color2, double angle) {
		return getImageRect(text, font, color1, color2, angle,
				SwingConstants.LEFT);
	}

	/**
	 * Renvoie l'image et la taille de l'image généré
	 * 
	 * @param text
	 *            le text a générer, les saut de ligne sont pris en compte
	 * @param font
	 * @param color1
	 *            la couleur du fill, peut etre null
	 * @param color2
	 *            la couleur du contour, peut etre null
	 * @param angle
	 *            en radian
	 * @param alignement
	 *            SwingConstants.LEFT, SwingConstants.CENTER,
	 *            SwingConstants.RIGHT
	 * @return
	 */
	public ImageRect getImageRect(String text, Font font, Color color1,
			Color color2, double angle, int alignement) {
		ImageRect imageRect = null;
		if (text != null && !text.isEmpty()) {
			Key key = new Key(text, font, color1, color2, angle, alignement);
			SoftReference<ImageRect> sr = rotateImageCacheMap.get(key);
			if (sr == null || sr.get() == null) {
				imageRect = createRotateImage(key);
				rotateImageCacheMap.put(key, new SoftReference<ImageRect>(
						imageRect));
			} else {
				imageRect = sr.get();
			}
		}
		return imageRect;
	}

	private ImageRect createRotateImage(Key key) {
		String text = key.text;
		String[] lignes = text.split("\\n");

		double maxWidth = 0;

		FontRenderContext frc = new FontRenderContext(new AffineTransform(),
				true, true);

		List<Shape> sList = new ArrayList<Shape>(lignes.length);
		for (int i = 0; i < lignes.length; i++) {
			GlyphVector textGV = key.font.createGlyphVector(frc, lignes[i]);
			Shape textShape = textGV.getOutline();

			maxWidth = Math.max(maxWidth, textShape.getBounds().width);

			sList.add(textShape);
		}

		Rectangle2D charRect = key.font.getMaxCharBounds(frc);

		Area s = null;
		int x = 0;
		int y = 0;
		for (Shape textShape : sList) {
			if (key.angle == 0) {
				x = 0;
				switch (key.alignement) {
				case SwingConstants.CENTER:
					x = (int) (maxWidth - textShape.getBounds().width) / 2;
					break;
				case SwingConstants.RIGHT:
					x = (int) (maxWidth - textShape.getBounds().width);
					break;
				}
			}

			AffineTransform t = new AffineTransform();
			t.translate(x, y);
			t.rotate(key.angle);

			Area a = new Area(t.createTransformedShape(textShape));
			if (s != null) {
				s.add(a);
			} else {
				s = a;
			}

			if (key.angle != 0) {
				x += Math.cos(key.angle) * textShape.getBounds().height
						- Math.sin(key.angle) * textShape.getBounds().height;
				y += Math.sin(key.angle) * textShape.getBounds().height
						+ Math.cos(key.angle) * textShape.getBounds().height;
			} else {
				y += (int) charRect.getHeight();
			}
		}

		// AffineTransform t = new AffineTransform();
		// t.rotate(key.angle);
		// s = s.createTransformedArea(t);

		Rectangle rect = s.getBounds();
		BufferedImage img = new BufferedImage(rect.width, rect.height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g22 = (Graphics2D) img.createGraphics();
		g22.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g22.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g22.translate(-rect.x, -rect.y);
		if (key.color1 != null) {
			g22.setColor(key.color1);
			g22.fill(s);
		}
		if (key.color2 != null) {
			g22.setColor(key.color2);
			g22.draw(s);
		}
		g22.dispose();

		return new ImageRect(img, rect);
	}

	/**
	 * Permet de faire un flush sur la cache
	 */
	public void flushTextImageCache() {
		rotateImageCacheMap.clear();
	}

	private static final class Key {

		public String text;

		public Font font;

		public Color color1;

		public Color color2;

		public Double angle;

		public Integer alignement;

		public Key(String text, Font font, Color color1, Color color2,
				double angle, int alignement) {
			this.text = text;
			this.font = font;
			this.color1 = color1;
			this.color2 = color2;
			this.angle = angle;
			this.alignement = alignement;
		}

		public int hashCode() {
			if (text != null && font != null) {
				return text.hashCode() + font.hashCode()
						+ (color1 != null ? color1.hashCode() : 0)
						+ (color2 != null ? color2.hashCode() : 0)
						+ angle.hashCode() + alignement.hashCode();
			}
			return super.hashCode();
		}

		public boolean equals(Object obj) {
			if (obj instanceof Key) {
				Key o2 = (Key) obj;
				if (text != null && font != null && angle != null
						&& alignement != null) {
					return text.equals(o2.text)
							&& font.equals(o2.font)
							&& ((color1 != null && o2.color1 != null && color1
									.equals(o2.color1)) || (color1 == null && o2.color1 == null))
							&& ((color2 != null && o2.color2 != null && color2
									.equals(o2.color2)) || (color2 == null && o2.color2 == null))
							&& angle.equals(o2.angle)
							&& alignement.equals(o2.alignement);
				}
			}
			return super.equals(obj);
		}
	}

	public static final class ImageRect {

		private BufferedImage image;

		private Rectangle rectangle;

		public ImageRect(BufferedImage image, Rectangle rectangle) {
			super();
			this.image = image;
			this.rectangle = rectangle;
		}

		public BufferedImage getImage() {
			return image;
		}

		public Rectangle getRectangle() {
			return rectangle;
		}
	}

}
