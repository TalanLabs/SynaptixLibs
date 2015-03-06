package com.synaptix.widget.hierarchical.view.swing.component.helper;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.synaptix.swing.utils.Utils;

public class GraphicsHelper {

	private GraphicsHelper() {
	}

	private static Map<String, SoftReference<GradientPaintCache>> gradientPaintCacheMap = Collections.synchronizedMap(new HashMap<String, SoftReference<GradientPaintCache>>());

	/**
	 * Dessine le text en le centrant et coupant si besoin
	 * 
	 * @param g
	 * @param text
	 * @param font
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static final void paintCenterString(Graphics g, String text, Font font, int x, int y, int width, int height) {
		if (text != null && !text.isEmpty()) {
			Graphics2D g2 = (Graphics2D) g.create(x, y, width, height);
			g2.setFont(font);
			String t = Utils.getClippedText(text, g2.getFontMetrics(), width);
			Rectangle2D rect2 = font.getStringBounds(t, g2.getFontRenderContext());
			LineMetrics lm = font.getLineMetrics(t, g2.getFontRenderContext());
			final int x2 = (int) (width - rect2.getWidth()) / 2;
			final float y2 = height / 2 + lm.getAscent() / 2;
			g2.drawString(t, x2, y2);
			g2.dispose();
		}
	}

	/**
	 * Build a vertical gradient paint and cache
	 * 
	 * @param id
	 * @param w
	 * @param h
	 * @param color1
	 * @param color2
	 * @return
	 */
	public static final GradientPaint buildVerticalGradientPaint(String id, int h, Color color1, Color color2) {
		GradientPaintCache gpc = null;
		SoftReference<GradientPaintCache> sgpc = gradientPaintCacheMap.get(id);
		if (sgpc != null) {
			gpc = sgpc.get();
		}
		if (gpc == null || gpc.h != h) {
			gpc = new GradientPaintCache();
			gpc.h = h;
			gpc.gradientPaint = new GradientPaint(0, 0, color1, 0, h, color2);
			gradientPaintCacheMap.put(id, new SoftReference<GradientPaintCache>(gpc));
		}

		return gpc.gradientPaint;
	}

	private static class GradientPaintCache {

		GradientPaint gradientPaint;

		int h;

	}
}
