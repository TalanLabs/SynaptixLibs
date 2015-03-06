package com.synaptix.swingx.mapviewer.layers.render;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.LineMetrics;
import java.util.HashMap;
import java.util.Map;

import org.jdesktop.swingx.mapviewer.AVKey;
import org.jdesktop.swingx.mapviewer.DrawContext;

import com.synaptix.swingx.mapviewer.layers.Highlightable;
import com.synaptix.swingx.mapviewer.layers.ToolTipable;
import com.synaptix.swingx.mapviewer.util.MultiLineTextRenderer;
import com.synaptix.swingx.mapviewer.util.TextRenderer;

public abstract class AbstractAnnotation implements Renderable, AVKey,
		Highlightable, ToolTipable {

	protected String text;

	protected AnnotationAttributes annotationAttributes;

	protected Map<Object, String> wrappedTextMap;

	protected Map<Object, Rectangle> textBoundsMap;

	protected Map<Object, LineMetrics> lineMetricsMap;

	protected boolean highlightable = true;

	protected boolean highlighted = false;

	protected boolean visible = true;

	protected String toolTip;
	
	private TextRenderer tr;

	private MultiLineTextRenderer mltr;

	public AbstractAnnotation() {
		super();

		this.annotationAttributes = new AnnotationAttributes();
		this.wrappedTextMap = new HashMap<Object, String>();
		this.textBoundsMap = new HashMap<Object, Rectangle>();
		this.lineMetricsMap = new HashMap<Object, LineMetrics>();
	}

	public AnnotationAttributes getAnnotationAttributes() {
		return annotationAttributes;
	}

	public void setAnnotationAttributes(
			AnnotationAttributes annotationAttributes) {
		this.annotationAttributes = annotationAttributes;
	}

	@Override
	public String getToolTip() {
		return toolTip;
	}

	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public boolean isHighlightable() {
		return highlightable;
	}

	public void setHighlightable(boolean highlightable) {
		this.highlightable = highlightable;
	}

	@Override
	public boolean isHighlighted() {
		return highlighted;
	}

	@Override
	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void render(Graphics2D g, DrawContext dc) {
		if (!this.isVisible())
			return;

		doRenderNow(g, dc);
	}

	protected abstract void doRenderNow(Graphics2D g, DrawContext dc);

	@Override
	public boolean isPick(DrawContext dc, Point point) {
		Rectangle rect = getBounds(dc);
		return dc.isPickingMode() && rect.contains(point);
	}

	public Dimension getPreferredSize(DrawContext dc) {
		Dimension size = new Dimension(getAnnotationAttributes().getSize());
		if (size.width < 1) {
			size.width = 1;
		}
		if (size.height < 0) {
			size.height = 0;
		}

		Insets insets = getAnnotationAttributes().getInsets();

		// Compute the size of this annotation's inset region.
		Rectangle insetBounds = this
				.computeInsetBounds(size.width, size.height);
		Dimension insetSize = new Dimension(insetBounds.width,
				insetBounds.height);

		// Wrap the text to fit inside the annotation's inset bounds. Then
		// adjust the inset bounds to the wrapped
		// text, depending on the annotation's attributes.
		insetSize = this
				.adjustSizeToText(dc, insetSize.width, insetSize.height);

		return new java.awt.Dimension(insetSize.width
				+ (insets.left + insets.right), insetSize.height
				+ (insets.top + insets.bottom));
	}

	protected Dimension adjustSizeToText(DrawContext dc, int width, int height) {
		String text = this.getWrappedText(dc, width, height, this.getText(),
				getAnnotationAttributes().getFont(), getAnnotationAttributes()
						.getTextAlign());
		Rectangle textBounds = this.getTextBounds(dc, text,
				getAnnotationAttributes().getFont(), getAnnotationAttributes()
						.getTextAlign());

		// If the attributes specify to fit the annotation to the wrapped text
		// width, then set the inset width to
		// the wrapped text width.
		if (getAnnotationAttributes().getAdjustWidthToText().equals(
				AVKey.SIZE_FIT_TEXT)
				&& text.length() > 0) {
			width = textBounds.width;
		}

		// If the inset height is less than or equal to zero, then override the
		// inset height with the the wrapped
		// text height.
		if (height <= 0) {
			height = textBounds.height;
		}

		return new Dimension(width, height);
	}

	protected Rectangle computeInsetBounds(int width, int height) {
		// TODO: factor in border width?
		Insets insets = getAnnotationAttributes().getInsets();

		int insetWidth = width - (insets.left + insets.right);
		int insetHeight = height - (insets.bottom + insets.top);

		if (insetWidth < 0) {
			insetWidth = 0;
		}

		if (insetHeight < 0 && height > 0) {
			insetHeight = 1;
		} else if (insetHeight < 0) {
			insetHeight = 0;
		}

		return new Rectangle(insets.left, insets.top, insetWidth, insetHeight);
	}

	protected Rectangle getTextBounds(DrawContext dc, String text, Font font,
			String align) {
		Object key = new TextCacheKey(0, 0, text, font, align);
		Rectangle bounds = this.textBoundsMap.get(key);
		if (bounds == null) {
			bounds = this.computeTextBounds(dc, text, font, align);
			this.textBoundsMap.put(key, bounds);
		}

		return new Rectangle(bounds);
	}

	protected Rectangle computeTextBounds(DrawContext dc, String text,
			Font font, String align) {
		if (text != null && text.length() > 0) {
			MultiLineTextRenderer mltr = this.getMultiLineTextRenderer(dc,
					font, align);
			return mltr.getBounds(text);
		} else {
			return new Rectangle();
		}
	}

	protected LineMetrics getLineMetrics(DrawContext dc, String text,
			Font font, String align) {
		Object key = new TextCacheKey(0, 0, text, font, align);
		LineMetrics lm = this.lineMetricsMap.get(key);
		if (lm == null) {
			lm = this.computeLineMetrics(dc, text, font, align);
			this.lineMetricsMap.put(key, lm);
		}

		return lm;
	}

	protected LineMetrics computeLineMetrics(DrawContext dc, String text,
			Font font, String align) {
		if (text != null && text.length() > 0) {
			MultiLineTextRenderer mltr = this.getMultiLineTextRenderer(dc,
					font, align);
			return mltr.getLineMetrics(text);
		} else {
			return null;
		}
	}

	protected String getWrappedText(DrawContext dc, int width, int height,
			String text, Font font, String align) {
		Object key = new TextCacheKey(width, height, text, font, align);
		String wrappedText = this.wrappedTextMap.get(key);
		if (wrappedText == null) {
			wrappedText = this.wrapText(dc, width, height, text, font, align);
			this.wrappedTextMap.put(key, wrappedText);
		}

		return wrappedText;
	}

	protected String wrapText(DrawContext dc, int width, int height,
			String text, java.awt.Font font, String align) {
		if (text != null && text.length() > 0) {
			MultiLineTextRenderer mltr = this.getMultiLineTextRenderer(dc,
					font, align);
			text = mltr.wrap(text, width, height);
		}

		return text;
	}

	protected MultiLineTextRenderer getMultiLineTextRenderer(DrawContext dc,
			Font font, String align) {
		if (tr == null || !tr.getFont().equals(font)) {
			tr = new TextRenderer(font);
			mltr = new MultiLineTextRenderer(tr);
		}
		mltr.setTextAlign(align);

		return mltr;
	}

	public Rectangle getBounds(DrawContext dc) {
		return this.computeBounds(dc);
	}

	protected abstract Rectangle computeBounds(DrawContext dc);

	protected Rectangle computeBoundingRectangle(Rectangle rect, int px, int py) {
		if (rect.contains(px, py))
			return rect;

		int dx = 0, dy = 0, dw = 0, dh = 0;
		if (px < rect.x) {
			dx = px - rect.x;
			dw = -dx;
		} else if (px > rect.x + rect.width - 1) {
			dw = px - (rect.x + rect.width - 1);
		}

		if (py < rect.y) {
			dy = py - rect.y;
			dh = -dy;
		} else if (py > rect.y + rect.height - 1) {
			dh = py - (rect.y + rect.height - 1);
		}

		rect.setBounds(rect.x + dx, rect.y + dy, rect.width + dw, rect.height
				+ dh);
		return rect;
	}

	protected void doDraw(Graphics2D g, DrawContext dc, int width, int height) {
		if (!isVisible()) {
			return;
		}

		this.drawContent(g, dc, width, height);
	}

	protected void drawTopLevelAnnotation(Graphics2D g, DrawContext dc, int x,
			int y, int width, int height, double scale, double opacity) {
		Graphics2D g2 = (Graphics2D) g.create();
		double finalOpacity = opacity * this.computeOpacity(dc);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				(float) finalOpacity));
		this.applyScreenTransform(g2, dc, x, y, width, height, scale);
		this.draw(g2, dc, width, height);
		g2.dispose();
	}

	public void draw(Graphics2D g, DrawContext dc, int width, int height) {
		this.doDraw(g, dc, width, height);
	}

	protected double computeOpacity(DrawContext dc) {
		double opacity = getAnnotationAttributes().getOpacity();

		// Remove transparency if highlighted.
		if (this.isHighlighted()) {
			opacity = 1;
		}

		return opacity;
	}

	protected void applyScreenTransform(Graphics2D g, DrawContext dc, int x,
			int y, int width, int height, double scale) {
		double finalScale = scale * this.computeScale(dc);
		Point offset = this.getAnnotationAttributes().getDrawOffset();

		g.translate(x, y);
		g.scale(finalScale, finalScale);
		g.translate(offset.x, offset.y);
		double dx = 0;
		if (AVKey.ALIGN_CENTER.equals(getAnnotationAttributes()
				.getFrameHorizontalAlign())) {
			dx = -width / 2;
		} else if (AVKey.ALIGN_RIGHT.equals(getAnnotationAttributes()
				.getFrameHorizontalAlign())) {
			dx = -width;
		}
		double dy = 0;
		if (AVKey.ALIGN_CENTER.equals(getAnnotationAttributes()
				.getFrameVerticalAlign())) {
			dy = -height / 2;
		} else if (AVKey.ALIGN_BOTTOM.equals(getAnnotationAttributes()
				.getFrameVerticalAlign())) {
			dy = -height;
		}
		g.translate(dx, dy);
	}

	protected double computeScale(DrawContext dc) {
		double scale = getAnnotationAttributes().getScale();

		// Factor in highlight scale.
		if (this.isHighlighted()) {
			scale *= getAnnotationAttributes().getHighlightScale();
		}

		return scale;
	}

	protected void drawContent(Graphics2D g, DrawContext dc, int width,
			int height) {
		this.drawBackground(g, dc, width, height);
		this.drawBackgroundImage(g, dc, width, height);
		this.drawBorder(g, dc, width, height);
		this.drawText(g, dc, width, height);
	}

	protected void drawBackground(Graphics2D g, DrawContext dc, int width,
			int height) {
		if (getAnnotationAttributes().getBackgroundColor() != null) {
			g.setColor(getAnnotationAttributes().getBackgroundColor());
			if (getAnnotationAttributes().getCornerRadius() == 0) {
				g.fillRect(0, 0, width, height);
			} else {
				g.fillRoundRect(0, 0, width, height, getAnnotationAttributes()
						.getCornerRadius(), getAnnotationAttributes()
						.getCornerRadius());
			}
		}
	}

	protected void drawBackgroundImage(Graphics2D g, DrawContext dc, int width,
			int height) {
		if (getAnnotationAttributes().getBackgroundImage() != null) {
			g.drawImage(getAnnotationAttributes().getBackgroundImage(), 0, 0,
					width, height, null);
		}
	}

	protected void drawBorder(Graphics2D g, DrawContext dc, int width,
			int height) {
		if (getAnnotationAttributes().getBorderColor() != null) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(getAnnotationAttributes().getBorderColor());
			g2.setStroke(getAnnotationAttributes().getBorderStroke());
			if (getAnnotationAttributes().getCornerRadius() == 0) {
				g2.drawRect(0, 0, width - 1, height - 1);
			} else {
				g2.drawRoundRect(0, 0, width - 1, height - 1,
						getAnnotationAttributes().getCornerRadius(),
						getAnnotationAttributes().getCornerRadius());
			}
			g2.dispose();
		}
	}

	protected void drawText(Graphics2D g, DrawContext dc, int width, int height) {
		String text = this.getText();
		if (text == null || text.length() == 0) {
			return;
		}

		Rectangle insetBounds = this.computeInsetBounds(width, height);

		// Wrap the text to the annotation's inset bounds.
		String wrappedText = this.getWrappedText(dc, insetBounds.width,
				insetBounds.height, text, getAnnotationAttributes().getFont(),
				getAnnotationAttributes().getTextAlign());
		Rectangle wrappedTextBounds = this.getTextBounds(dc, wrappedText,
				getAnnotationAttributes().getFont(), getAnnotationAttributes()
						.getTextAlign());

		LineMetrics lm = this.getLineMetrics(dc, wrappedText,
				getAnnotationAttributes().getFont(), getAnnotationAttributes()
						.getTextAlign());

		int x = insetBounds.x;
		int y = insetBounds.y - wrappedTextBounds.y + (int) lm.getAscent();

		// Adjust the text x-coordinate according to the text alignment
		// property.
		if (getAnnotationAttributes().getTextAlign().equals(AVKey.ALIGN_CENTER)) {
			x = (int) insetBounds.getCenterX();
		} else if (getAnnotationAttributes().getTextAlign().equals(
				AVKey.ALIGN_RIGHT)) {
			x = (int) insetBounds.getMaxX();
		}

		int lineHeight = (int) wrappedTextBounds.getMinY();

		this.drawText(g, dc, x, y, lineHeight, wrappedText);
	}

	protected void drawText(Graphics2D g, DrawContext dc, int x, int y,
			int lineHeight, String text) {
		this.drawPlainText(g, dc, x, y, lineHeight, text);
	}

	protected void drawPlainText(Graphics2D g, DrawContext dc, int x, int y,
			int lineHeight, String text) {
		MultiLineTextRenderer mltr = this.getMultiLineTextRenderer(dc,
				getAnnotationAttributes().getFont(), getAnnotationAttributes()
						.getTextAlign());

		mltr.setTextColor(getAnnotationAttributes().getTextColor());
		mltr.setBackColor(Color.pink);// getAnnotationAttributes().getBackgroundColor());

		mltr.draw(g, text, x, y, lineHeight, getAnnotationAttributes()
				.getEffect());
	}

	protected static class TextCacheKey {

		private final int width;
		private final int height;
		private final String text;
		private final java.awt.Font font;
		private final String align;

		public TextCacheKey(int width, int height, String text,
				java.awt.Font font, String align) {
			this.width = width;
			this.height = height;
			this.text = text;
			this.font = font;
			this.align = align;
		}

		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || this.getClass() != o.getClass())
				return false;

			TextCacheKey that = (TextCacheKey) o;
			return (this.width == that.width)
					&& (this.height == that.height)
					&& (this.align.equals(that.align))
					&& (this.text != null ? this.text.equals(that.text)
							: that.text == null)
					&& (this.font != null ? this.font.equals(that.font)
							: that.font == null);
		}

		public int hashCode() {
			int result = this.width;
			result = 31 * result + this.height;
			result = 31 * result
					+ (this.text != null ? this.text.hashCode() : 0);
			result = 31 * result
					+ (this.font != null ? this.font.hashCode() : 0);
			result = 31 * result
					+ (this.align != null ? this.align.hashCode() : 0);
			return result;
		}
	}
}
