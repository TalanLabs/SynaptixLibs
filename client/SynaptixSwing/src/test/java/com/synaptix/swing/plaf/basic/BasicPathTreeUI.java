package com.synaptix.swing.plaf.basic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.plaf.PathTreeUI;

import pathTree.JPathTree;
import pathTree.PathTreeModel;

public class BasicPathTreeUI extends PathTreeUI {

	private Map<Key, SoftReference<ImageRect>> rotateImageCacheMap = Collections
			.synchronizedMap(new HashMap<Key, SoftReference<ImageRect>>());

	private JPathTree pathTree;

	private MouseInputListener mouseInputListener;

	public static ComponentUI createUI(JComponent h) {
		return new BasicPathTreeUI();
	}

	public void installUI(JComponent c) {
		pathTree = (JPathTree) c;

		installListeners();
	}

	private void installListeners() {
		mouseInputListener = new MouseInputHandler();

		pathTree.addMouseListener(mouseInputListener);
		pathTree.addMouseMotionListener(mouseInputListener);
	}

	public void uninstallUI(JComponent c) {
		uninstallListeners();

		pathTree = null;
	}

	private void uninstallListeners() {
		pathTree.removeMouseListener(mouseInputListener);
		pathTree.removeMouseMotionListener(mouseInputListener);

		mouseInputListener = null;
	}

	public void paint(Graphics g, JComponent c) {
		Insets insets = pathTree.getInsets();

		Graphics2D g2 = (Graphics2D) g.create(insets.top, insets.left, pathTree
				.getWidth()
				- (insets.left + insets.right) + 1, pathTree.getHeight()
				- (insets.top + insets.bottom) + 1);

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		paintLines(g2);

		g2.dispose();
	}

	private void paintLines(Graphics2D g2) {
		PathTreeModel model = pathTree.getPathTreeModel();

		Map<Object, Integer> map = new HashMap<Object, Integer>();
		Object root = model.getRoot();
		map.put(root, 0);
		int max = calculePoids(model, map, root);
		int[] total = new int[max + 1];
		Arrays.fill(total, 0);
		for (Integer poids : map.values()) {
			total[poids]++;
		}

		int m = -1;
		for (int i = 0; i < total.length; i++) {
			if (total[i] > m) {
				m = total[i];
			}
		}

		int width = (max + 1) * 20 + max * 50;
		int height = m * 20 + (m - 1) * 100;

		int[] pos = new int[max + 1];
		Arrays.fill(pos, 0);

		for (Entry<Object, Integer> entry : map.entrySet()) {
			int col = entry.getValue();
			int x = col * 20 + col * 50;

			int nb = total[col];
			int c = pos[col];
			int y = c * (20 + 100) + (m - nb) * (50 + 10);
			
			g2.setColor(Color.black);
			g2.drawOval(x, y, 20, 20);

			pos[col]++;
		}
	}

	private int calculePoids(PathTreeModel model, Map<Object, Integer> map,
			Object parent) {
		int max = 0;
		int count = model.getNodeCount(parent);
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				int poids = map.get(parent) + 1;

				Object node = model.getNode(parent, i);
				if (!map.containsKey(node) || map.get(node) < poids) {
					map.put(node, poids);
				}

				max = Math.max(max, Math.max(poids, calculePoids(model, map,
						node)));
			}
		}
		return max;
	}

	private Rectangle getRectanglePath() {
		PathTreeModel model = pathTree.getPathTreeModel();

		Map<Object, Integer> map = new HashMap<Object, Integer>();
		Object root = model.getRoot();
		map.put(root, 0);
		int max = calculePoids(model, map, root);
		int[] total = new int[max + 1];
		Arrays.fill(total, 0);
		for (Integer poids : map.values()) {
			total[poids]++;
		}

		int m = -1;
		for (int i = 0; i < total.length; i++) {
			if (total[i] > m) {
				m = total[i];
			}
		}

		int width = (max + 1) * 20 + max * 50;
		int height = m * 20 + (m - 1) * 100;
		return new Rectangle(-width / 2, -height / 2, width, height);
	}

	public Dimension getPreferredSize(JComponent c) {
		Insets insets = pathTree.getInsets();
		Rectangle rect = getRectanglePath();

		System.out.println(rect);

		return new Dimension(insets.left + insets.right + rect.width,
				insets.top + insets.bottom + rect.height);
	}

	private ImageRect getImageRect(String text, Font font, Color color) {
		ImageRect imageRect = null;
		if (text != null && !text.isEmpty()) {
			Key key = new Key(text, font, color, color, -Math.PI / 4.0);
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

		Area s = null;
		int x = 0;
		for (int i = 0; i < lignes.length; i++) {
			GlyphVector textGV = key.font.createGlyphVector(
					new FontRenderContext(new AffineTransform(), true, true),
					lignes[i]);
			Shape textShape = textGV.getOutline();

			AffineTransform t = new AffineTransform();
			t.translate(x, 0);
			t.rotate(key.angle);

			Area a = new Area(t.createTransformedShape(textShape));
			if (s != null) {
				s.add(a);
			} else {
				s = a;
			}

			x += textShape.getBounds().height * 3.0 / 2.0;
		}

		Rectangle rect = s.getBounds();
		BufferedImage img = new BufferedImage(rect.width, rect.height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g22 = (Graphics2D) img.createGraphics();
		g22.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g22.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g22.translate(-rect.x, -rect.y);
		g22.setColor(key.color1);
		g22.fill(s);
		g22.dispose();

		return new ImageRect(img, rect);
	}

	public void clearRotateImageCache() {
		rotateImageCacheMap.clear();
	}

	private final class MouseInputHandler extends MouseInputAdapter {

		public void mousePressed(MouseEvent e) {
		}
	}

	private static final class Key {

		public String text;

		public Font font;

		public Color color1;

		public Color color2;

		public Double angle;

		public Key(String text, Font font, Color color1, Color color2,
				Double angle) {
			this.text = text;
			this.font = font;
			this.color1 = color1;
			this.color2 = color2;
			this.angle = angle;
		}

		public int hashCode() {
			if (text != null && font != null && color1 != null
					&& color2 != null && angle != null) {
				return text.hashCode() + font.hashCode() + color1.hashCode()
						+ color2.hashCode() + angle.hashCode();
			}
			return super.hashCode();
		}

		public boolean equals(Object obj) {
			if (obj instanceof Key) {
				Key o2 = (Key) obj;
				if (text != null && font != null && color1 != null
						&& color2 != null && angle != null) {
					return text.equals(o2.text) && font.equals(o2.font)
							&& color1.equals(o2.color1)
							&& angle.equals(o2.angle);
				}
			}
			return super.equals(obj);
		}
	}

	private static final class ImageRect {

		public BufferedImage image;

		public Rectangle rectangle;

		public ImageRect(BufferedImage image, Rectangle rectangle) {
			super();
			this.image = image;
			this.rectangle = rectangle;
		}
	}
}
