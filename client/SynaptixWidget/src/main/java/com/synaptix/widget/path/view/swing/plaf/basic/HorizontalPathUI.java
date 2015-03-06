package com.synaptix.widget.path.view.swing.plaf.basic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.path.JPath;
import com.synaptix.swing.path.PathModel;
import com.synaptix.swing.path.PathRenderer;
import com.synaptix.swing.plaf.PathUI;
import com.synaptix.swing.plaf.basic.BasicPathUI;
import com.synaptix.swing.utils.TextImageCacheFactory;
import com.synaptix.swing.utils.TextImageCacheFactory.ImageRect;
import com.synaptix.swing.utils.Utils;

public class HorizontalPathUI extends PathUI {

	private TextImageCacheFactory textImageFactory = new TextImageCacheFactory();

	private JPath path;

	private MouseInputListener mouseInputListener;

	public static ComponentUI createUI(JComponent h) {
		return new BasicPathUI();
	}

	@Override
	public void installUI(JComponent c) {
		path = (JPath) c;

		installListeners();
	}

	private void installListeners() {
		mouseInputListener = new MouseInputHandler();

		path.addMouseListener(mouseInputListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		uninstallListeners();

		path = null;
	}

	private void uninstallListeners() {
		path.removeMouseListener(mouseInputListener);

		mouseInputListener = null;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Insets insets = path.getInsets();

		Graphics2D g2 = (Graphics2D) g.create(insets.top, insets.left, path.getWidth() - (insets.left + insets.right) + 1, path.getHeight() - (insets.top + insets.bottom) + 1);

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		paintLines(g2);

		g2.dispose();
	}

	private void paintLines(Graphics2D g2) {
		Insets insets = path.getInsets();

		PathModel model = path.getPathModel();
		if (model.getNodeCount() > 0) {
			Rectangle rect = getRectanglePath();
			PathRenderer renderer = path.getPathRenderer();

			int y = (path.getHeight() - (insets.top + insets.bottom)) / 2 - (int) rect.getCenterY();
			int x = -rect.x;
			for (int i = 0; i < model.getNodeCount(); i++) {
				Dimension size = path.prepareNodeDimension(renderer, i);

				int mw = size.width / 2;
				int mh = size.height / 2;

				if (i < model.getNodeCount() - 1) {
					Dimension lineSize = path.prepareLineDimension(renderer, i, i + 1);

					Dimension size2 = path.prepareNodeDimension(renderer, i + 1);

					int mw2 = size2.width / 2;

					Graphics g3 = g2.create(x + mw, y - lineSize.height / 2, mw + lineSize.width + mw2, lineSize.height);
					path.paintLineRenderer(g3, renderer, i, i + 1);
					g3.dispose();

					String text = model.getLineName(i, i + 1);
					if (text != null && !text.isEmpty()) {
						paintLineText(g2, x, y, text, path.prepareLineFontText(renderer, i, i + 1), path.prepareLineColorText(renderer, i, i + 1), size, lineSize);
					}
				}

				Graphics g3 = g2.create(x, y - mh, size.width, size.height);
				path.paintNodeRenderer(g3, renderer, i);
				g3.dispose();

				int lg = -1;
				if (i > 0) {
					Dimension lineSize = path.prepareLineDimension(renderer, i - 1, i);
					lg = lineSize.width / 2;
				}
				int ld = -1;
				if (i < model.getNodeCount() - 1) {
					Dimension lineSize = path.prepareLineDimension(renderer, i, i + 1);
					lg = lineSize.width / 2;
				}
				int widthMax = -1;
				if (lg != -1 && ld != -1) {
					widthMax = lg + ld + size.width;
				} else if (lg != -1) {
					widthMax = lg * 2 + size.width;
				} else if (ld != -1) {
					widthMax = ld * 2 + size.width;
				}

				paintNodeText(g2, model.getNodeName(i), path.prepareNodeFontText(renderer, i), path.prepareNodeColorText(renderer, i), x + mw, y, size.width, size.height, widthMax);

				x += size.width;
				if (i < model.getNodeCount() - 1) {
					Dimension lineSize = path.prepareLineDimension(renderer, i, i + 1);
					x += lineSize.width;
				}
			}
		}
	}

	protected void paintLineText(Graphics2D g2, int x, int y, String text, Font font, Color color, Dimension nodeSize, Dimension lineSize) {
		ImageRect ir = textImageFactory.getImageRect(text, font, color, null, 0, SwingUtilities.CENTER);
		g2.drawImage(ir.getImage(), x + nodeSize.width + (lineSize.width - ir.getRectangle().width) / 2, y + lineSize.height / 2 + 5, null);
	}

	private void paintNodeText(Graphics2D g2, String text, Font font, Color color, int x, int y, int height, int width, int widthMax) {
		String[] lignes = text.split("\\n");

		g2.setFont(font);
		g2.setColor(color);

		String text1 = widthMax != -1 ? Utils.getClippedText(lignes[0], g2.getFontMetrics(), widthMax) : lignes[0];
		Rectangle2D rectHaut = font.getStringBounds(text1, g2.getFontRenderContext());
		LineMetrics lmHaut = font.getLineMetrics(text1, g2.getFontRenderContext());

		g2.drawString(text1, (int) (x - rectHaut.getWidth() / 2), y - lmHaut.getDescent() - width / 2);

		if (lignes.length > 1) {
			int hh = -(font.getSize() + 3);
			for (int i = 1; i < lignes.length; i++) {
				String text2 = widthMax != -1 ? Utils.getClippedText(lignes[i], g2.getFontMetrics(), widthMax) : lignes[i];
				Rectangle2D rectBas = font.getStringBounds(text2, g2.getFontRenderContext());
				LineMetrics lmBas = font.getLineMetrics(text2, g2.getFontRenderContext());
				hh += font.getSize() + 3;
				g2.drawString(text2, (int) (x - rectBas.getWidth() / 2), y + lmBas.getAscent() + width / 2 + hh);
			}
		}
	}

	private Rectangle getRectanglePath() {
		PathModel model = path.getPathModel();
		int width = 0;
		int top = 0;
		int bottom = 0;
		int x = 0;
		if (model.getNodeCount() > 0) {
			PathRenderer renderer = path.getPathRenderer();

			for (int i = 0; i < model.getNodeCount(); i++) {
				Dimension nodeSize = path.prepareNodeDimension(renderer, i);

				int mw = nodeSize.width / 2;
				int mh = nodeSize.height / 2;

				width += nodeSize.width;
				if (i < model.getNodeCount() - 1) {
					Dimension lineSize = path.prepareLineDimension(renderer, i, i + 1);
					width += lineSize.width;

					top = Math.max(top, lineSize.height / 2);
					bottom = Math.max(bottom, lineSize.height / 2);

					String text = model.getLineName(i, i + 1);
					if (text != null && !text.isEmpty()) {
						ImageRect ir = textImageFactory.getImageRect(text, path.prepareLineFontText(renderer, i, i + 1), path.prepareLineColorText(renderer, i, i + 1), null, 0, SwingUtilities.CENTER);
						bottom = Math.max(bottom, lineSize.height / 2 + 5 + ir.getRectangle().height);
					}
				}

				bottom = Math.max(bottom, mh);

				String text = model.getNodeName(i);
				String[] lignes = text.split("\\n");

				FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
				Font font = path.prepareNodeFontText(renderer, i);
				Rectangle2D rectHaut = font.getStringBounds(lignes[0], frc);

				top = Math.max(top, (int) (rectHaut.getHeight() + mh));
				if (i == 0 && mw - rectHaut.getWidth() / 2 < 0) {
					x = Math.min(x, (int) (-rectHaut.getWidth() / 2));
				}
				if (i == model.getNodeCount() - 1) {
					width = width + (int) Math.max(0, rectHaut.getWidth() / 2 - mw);
				}

				if (lignes.length > 1) {
					int hh = 0;
					for (int j = 1; j < lignes.length; j++) {
						Rectangle2D rectBas = font.getStringBounds(lignes[j], frc);
						hh += font.getSize() + 3;

						if (i == 0) {
							x = Math.min(x, (int) (mw - rectBas.getWidth() / 2));
						}
						if (i == model.getNodeCount() - 1) {
							width = width + (int) Math.max(0, rectBas.getWidth() / 2 - mw);
						}
					}
					bottom = Math.max(bottom, hh + mh);
				}
			}
		}
		return new Rectangle(x, -top, width - x, top + bottom);
	}

	@Override
	public void clearRotateImageCache() {
		textImageFactory.flushTextImageCache();
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Insets insets = path.getInsets();
		Rectangle rect = getRectanglePath();
		return new Dimension(insets.left + insets.right + rect.width, insets.top + insets.bottom + rect.height);
	}

	private final class MouseInputHandler extends MouseInputAdapter {

		private boolean selectNode(Point p, Rectangle rect) {
			boolean find = false;
			PathModel model = path.getPathModel();
			if (model.getNodeCount() > 0) {
				PathRenderer renderer = path.getPathRenderer();
				Insets insets = path.getInsets();

				int x = insets.left - rect.x;
				int i = 0;
				Rectangle r = new Rectangle();
				while (i < model.getNodeCount() && !find) {
					Dimension size = path.prepareNodeDimension(renderer, i);
					if (model.isSelectedNode(i)) {
						r.setRect(x, insets.top, size.width, path.getHeight() - (insets.top + insets.bottom));
						find = r.contains(p);
					}

					x += size.width;
					if (i < model.getNodeCount() - 1) {
						Dimension lineSize = path.prepareLineDimension(renderer, i, i + 1);
						x += lineSize.width;
					}

					i++;
				}

				if (find) {
					path.getPathSelectionModel().setSelectedNode(i - 1);
				} else {
					path.getPathSelectionModel().clearSelection();
				}
			}
			return find;
		}

		private boolean selectLine(Point p, Rectangle rect) {
			PathModel model = path.getPathModel();
			if (model.getNodeCount() > 0) {

				int xi;
				int yi;
				int hi;
				int wi;

				PathRenderer renderer = path.getPathRenderer();
				Insets insets = path.getInsets();

				int i = 0;
				int j = model.getNodeCount() - 1;

				int x0 = insets.left - rect.x + path.prepareNodeDimension(renderer, 0).width / 2;
				int y0 = insets.top;
				int h0 = path.getHeight() - (insets.top + insets.bottom);
				int w0 = path.prepareLineDimension(renderer, 0, j).width + path.prepareNodeDimension(renderer, 0).width / 2 + path.prepareNodeDimension(renderer, j).width / 2;
				for (int k = 1; k < j; k++) {
					w0 += path.prepareNodeDimension(renderer, k).width;
				}

				Rectangle r = new Rectangle(x0, y0, w0, h0);

				if (!r.contains(p)) {
					path.getPathSelectionModel().clearSelection();
					return false;
				}

				xi = x0 + path.prepareNodeDimension(renderer, i).width / 2 + path.prepareLineDimension(renderer, 0, i).width;
				yi = y0;
				hi = h0;
				wi = path.prepareNodeDimension(renderer, i).width / 2 + path.prepareNodeDimension(renderer, j).width / 2 + path.prepareLineDimension(renderer, i, j).width;
				for (int k = i + 1; k < j; k++) {
					wi += path.prepareNodeDimension(renderer, k).width;
				}
				r.setRect(xi, yi, wi, hi);
				while (r.contains(p) && i < j) {
					i++;
					xi = x0 + path.prepareNodeDimension(renderer, 0).width / 2 + path.prepareNodeDimension(renderer, i).width / 2 + path.prepareLineDimension(renderer, 0, i).width;
					for (int k = 1; k < i; k++) {
						xi += path.prepareNodeDimension(renderer, k).width;
					}
					wi = path.prepareNodeDimension(renderer, i).width / 2 + path.prepareNodeDimension(renderer, j).width / 2 + path.prepareLineDimension(renderer, i, j).width;
					for (int k = i + 1; k < j; k++) {
						wi += path.prepareNodeDimension(renderer, k).width;
					}
					r.setRect(xi, yi, wi, hi);
				}
				if (i > 0) {
					i--;
				}
				xi = x0 + path.prepareNodeDimension(renderer, 0).width / 2 + path.prepareNodeDimension(renderer, i).width / 2 + path.prepareLineDimension(renderer, 0, i).width;
				for (int k = 1; k < i; k++) {
					xi += path.prepareNodeDimension(renderer, k).width;
				}
				wi = path.prepareNodeDimension(renderer, i).width / 2 + path.prepareNodeDimension(renderer, j).width / 2 + path.prepareLineDimension(renderer, i, j).width;
				for (int k = i + 1; k < j; k++) {
					wi += path.prepareNodeDimension(renderer, k).width;
				}
				r.setRect(xi, yi, wi, hi);
				while (r.contains(p) && j > 0) {
					j--;
					xi = x0 + path.prepareNodeDimension(renderer, i).width / 2 + path.prepareLineDimension(renderer, 0, i).width;
					for (int k = 1; k < i; k++) {
						xi += path.prepareNodeDimension(renderer, k).width;
					}
					wi = path.prepareNodeDimension(renderer, i).width + path.prepareNodeDimension(renderer, j).width / 2 + path.prepareLineDimension(renderer, i, j).width;
					for (int k = i + 1; k < j; k++) {
						wi += path.prepareNodeDimension(renderer, k).width;
					}
					r.setRect(xi, yi, wi, hi);
				}
				j++;
				if (j - i > 1) {
					i = j - 1;
				}
				path.getPathSelectionModel().setSelectedNode(i);
				path.getPathSelectionModel().setSelectedNode(j);
				return true;
			}
			return false;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			path.requestFocus();
			if (path.isEnabled()) {
				Rectangle rect = getRectanglePath();
				if (!selectNode(e.getPoint(), rect)) {
					selectLine(e.getPoint(), rect);
					path.selectionChanged();
				}
			}
		}
	}
}
