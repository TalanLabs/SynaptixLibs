package com.synaptix.swing.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;

public class OldTextImageCacheFactory {

	private Map<Key, SoftReference<ImageRect>> rotateImageCacheMap;

	public OldTextImageCacheFactory() {
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

		Rectangle2D charRect = key.font.getMaxCharBounds(frc);
		List<Shape> sList = new ArrayList<Shape>(lignes.length);
		for (int i = 0; i < lignes.length; i++) {
			GlyphVector textGV = key.font.createGlyphVector(frc, lignes[i]);
			Shape textShape = textGV.getOutline();

			maxWidth = Math.max(maxWidth, textShape.getBounds().width);

			sList.add(textShape);
		}

		Area area = null;
		int y = 0;
		int i = 0;
		for (Shape textShape : sList) {
			int x = 0;
			switch (key.alignement) {
			case SwingConstants.CENTER:
				x = (int) (maxWidth - textShape.getBounds().width) / 2;
				break;
			case SwingConstants.RIGHT:
				x = (int) (maxWidth - textShape.getBounds().width);
				break;
			}
			AffineTransform at = new AffineTransform();
			at.translate(x, y);

			Area a = new Area(at.createTransformedShape(textShape));
			if (area != null) {
				area.add(a);
			} else {
				area = a;
			}

			y += (int) charRect.getHeight();

			i++;
		}

		AffineTransform t = new AffineTransform();
		t.rotate(key.angle);

		Area bounds = new Area(area.getBounds2D());
		PathIterator pi = bounds.getPathIterator(t);
		List<Point2D> pList = new ArrayList<Point2D>();
		while (!pi.isDone()) {
			double[] coords = new double[6];
			int seg = pi.currentSegment(coords);
			switch (seg) {
			case PathIterator.SEG_MOVETO:
			case PathIterator.SEG_LINETO:
				pList.add(new Point2D.Double(coords[0], coords[1]));
				break;
			default:
				break;
			}
			pi.next();
		}

		area = area.createTransformedArea(t);

		Rectangle rect = area.getBounds();

		BufferedImage img = new BufferedImage(rect.width, rect.height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g22 = (Graphics2D) img.createGraphics();
		g22.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g22.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g22.getFontMetrics(key.font);

		g22.translate(-rect.x, -rect.y);
		if (key.color1 != null) {
			g22.setColor(key.color1);
			g22.fill(area);
		}
		if (key.color2 != null) {
			g22.setColor(key.color2);
			g22.draw(area);
		}
		g22.dispose();

		return new ImageRect(img, rect, area.getBounds2D(), pList
				.toArray(new Point2D[pList.size()]), charRect.getHeight());
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

		private Image image;

		private Rectangle rectangle;

		private Rectangle2D rectangle2D;

		private Point2D[] point2Ds;

		private double lineHeight;

		public ImageRect(BufferedImage image, Rectangle rectangle,
				Rectangle2D rectangle2D, Point2D[] point2Ds, double lineHeight) {
			super();
			this.image = image;
			this.rectangle = rectangle;
			this.rectangle2D = rectangle2D;
			this.point2Ds = point2Ds;
			this.lineHeight = lineHeight;
		}

		public Image getImage() {
			return image;
		}

		public Rectangle getRectangle() {
			return rectangle;
		}

		/**
		 * Renvoie le rectangle de la boite englobante
		 * 
		 * @return
		 */
		public Rectangle2D getRectangle2D() {
			return rectangle2D;
		}

		/**
		 * Renvoie la liste des points qui forme la boite englobante minimum
		 * 
		 * @return
		 */
		public Point2D[] getPoint2Ds() {
			return point2Ds;
		}

		/**
		 * Renvoie la hauteur d'une ligne max pour la font
		 * 
		 * @return
		 */
		public double getLineHeight() {
			return lineHeight;
		}
	}
}
