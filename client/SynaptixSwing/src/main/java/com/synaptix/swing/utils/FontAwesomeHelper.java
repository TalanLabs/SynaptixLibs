package com.synaptix.swing.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class FontAwesomeHelper {

	private static final Font ttfBase;

	private static final Map<String, Character> icons;

	private static final Map<String, Font> fonts;

	static {
		ttfBase = initFont();
		icons = initIcons();

		fonts = new HashMap<String, Font>();
	}

	public static Font getTtfBase() {
		return ttfBase;
	}

	public static Character getCharacter(String name) {
		return icons.get(name);
	}

	public static final Font deriveFont(float size) {
		return deriveFont(ttfBase.getStyle(), size);
	}

	public static final Font deriveFont(int style, float size) {
		Font f;
		String key = new StringBuilder().append(style).append("_").append(size).toString();
		synchronized (fonts) {
			f = fonts.get(key);
			if (f == null) {
				f = ttfBase.deriveFont(style, size);
				fonts.put(key, f);
			}
		}
		return f;
	}

	private static Font initFont() {
		Font font = null;
		try {
			InputStream in = FontAwesomeHelper.class.getResourceAsStream("font/fontawesome-webfont.ttf");
			font = Font.createFont(Font.TRUETYPE_FONT, in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return font;
	}

	private static Map<String, Character> initIcons() {
		Map<String, Character> icons = new HashMap<String, Character>(1000);
		try {
			List<String> lines = Resources.readLines(FontAwesomeHelper.class.getResource("css/font-awesome.css"), Charsets.UTF_8);
			Pattern pvalue = Pattern.compile("(?<=\\\\).*(?=\")"); // (?<=\).*(?=")
			Pattern pkey = Pattern.compile("(?<=.).*(?=:before)"); // (?<=.fa-).*(?=:before)
			for (int i = 0; i < lines.size(); i++) { // Check each line if it has a unicode value
				Matcher mvalue = pvalue.matcher(lines.get(i));
				if (mvalue.find()) {
					Character value = (char) Integer.parseInt(mvalue.group(), 16);
					for (int j = i - 1; j >= 0; j--) { // Check previous lines for the keys to this unicode value
						Matcher mkey = pkey.matcher(lines.get(j));
						if (mkey.find()) {
							icons.put(mkey.group(), value);
						} else {
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			System.err.print(e.getMessage() + "\n");
			System.exit(1);
		}
		return icons;
	}

	public static Icon getIcon(String name, int size, Color color) {
		return new ImageIcon(buildImage(size, ttfBase, icons.get(name), color));
	}

	public static Image getImage(String name, int size, Color color) {
		return buildImage(size, ttfBase, icons.get(name), color);
	}

	private static BufferedImage buildImage(int size, Font font, Character letter, Color textColor) {
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		GraphicsHelper.activeAntiAliasing(graphics);
		paintCharacter(graphics, font, letter, textColor, 0, 0, size, size);
		graphics.dispose();
		return image;
	}

	private static void paintCharacter(Graphics2D g2, Font font, Character letter, Color textColor, int x, int y, int w, int h) {
		GlyphVector firstLetterGV = font.createGlyphVector(g2.getFontRenderContext(), letter.toString());
		Shape firstLetterShape = firstLetterGV.getOutline();

		Rectangle2D rect = firstLetterShape.getBounds2D();
		double sx = (w - 1) / rect.getWidth();
		double sy = (h - 1) / rect.getHeight();

		AffineTransform t = new AffineTransform();
		t.translate(x, y);
		t.scale(sx, sy);
		t.translate(-rect.getX(), -rect.getY());

		Shape s = t.createTransformedShape(firstLetterShape);

		g2.setColor(textColor);
		g2.fill(s);
	}
}
