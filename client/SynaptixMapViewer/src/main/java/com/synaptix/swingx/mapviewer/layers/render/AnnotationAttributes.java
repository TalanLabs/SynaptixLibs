package com.synaptix.swingx.mapviewer.layers.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;

import org.jdesktop.swingx.mapviewer.AVKey;

public class AnnotationAttributes implements Cloneable {

	private static final AnnotationAttributes defaults = new AnnotationAttributes();

	static {
		defaults.setFont(Font.decode("Arial-PLAIN-12"));
		defaults.setBackgroundColor(Color.WHITE);
		defaults.setBorderColor(new Color(171, 171, 171));
		defaults.setTextColor(Color.black);
		defaults.setInsets(new Insets(20, 15, 15, 15));
		defaults.setCornerRadius(0);
		defaults.setSize(new Dimension(160, 0));
		defaults.setTextAlign(AVKey.ALIGN_LEFT);
		defaults.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);
		defaults.setEffect(AVKey.TEXT_EFFECT_NONE);
		defaults.setScale(1.0);
		defaults.setHighlightScale(1.2);
		defaults.setDrawOffset(new Point(0, 0));
		defaults.setOpacity(1);
		defaults.setFrameHorizontalAlign(AVKey.ALIGN_CENTER);
		defaults.setFrameVerticalAlign(AVKey.ALIGN_CENTER);
		defaults.setBorderStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
	}

	private Font font = null;

	private Color backgroundColor = null;

	private Color borderColor = null;

	private Color textColor = null;

	private Insets insets = null;

	private int cornerRadius = -1;

	private Dimension size = null;

	private String textAlign = null;

	private String adjustWidthToText = null;

	private String effect = null;

	private double scale = -1;

	private double highlightScale = -1;

	private Point drawOffset = null;

	private double opacity = -1;

	private String frameVerticalAlign = null;

	private String frameHorizontalAlign = null;

	private BasicStroke borderStroke = null;

	private Image backgroundImage = null;

	private AnnotationAttributes defaultAttributes = defaults;

	public AnnotationAttributes() {
		super();
	}

	public void setDefaults(AnnotationAttributes attr) {
		this.defaultAttributes = attr;
	}

	public Image getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(Image backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public String getFrameHorizontalAlign() {
		return frameHorizontalAlign != null ? frameHorizontalAlign
				: defaultAttributes.getFrameHorizontalAlign();
	}

	public void setFrameHorizontalAlign(String frameHorizontalAlign) {
		this.frameHorizontalAlign = frameHorizontalAlign;
	}

	public String getFrameVerticalAlign() {
		return frameVerticalAlign != null ? frameVerticalAlign
				: defaultAttributes.getFrameVerticalAlign();
	}

	public void setFrameVerticalAlign(String frameVerticalAlign) {
		this.frameVerticalAlign = frameVerticalAlign;
	}

	public String getAdjustWidthToText() {
		return adjustWidthToText != null ? adjustWidthToText
				: defaultAttributes.getAdjustWidthToText();
	}

	public void setAdjustWidthToText(String adjustWidthToText) {
		this.adjustWidthToText = adjustWidthToText;
	}

	public String getTextAlign() {
		return textAlign != null ? textAlign : defaultAttributes.getTextAlign();
	}

	public void setTextAlign(String textAlign) {
		this.textAlign = textAlign;
	}

	public Insets getInsets() {
		return insets != null ? insets : defaultAttributes.getInsets();
	}

	public void setInsets(Insets insets) {
		this.insets = insets;
	}

	public int getCornerRadius() {
		return cornerRadius != -1 ? cornerRadius : defaultAttributes
				.getCornerRadius();
	}

	public void setCornerRadius(int corner) {
		this.cornerRadius = corner;
	}

	public Color getBorderColor() {
		return borderColor != null ? borderColor : defaultAttributes
				.getBorderColor();
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor != null ? backgroundColor : defaultAttributes
				.getBackgroundColor();
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Font getFont() {
		return font != null ? font : defaultAttributes.getFont();
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Color getTextColor() {
		return textColor != null ? textColor : defaultAttributes.getTextColor();
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public Dimension getSize() {
		return size != null ? size : defaultAttributes.getSize();
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public String getEffect() {
		return effect != null ? effect : defaultAttributes.getEffect();
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public BasicStroke getBorderStroke() {
		return borderStroke != null ? borderStroke : defaultAttributes
				.getBorderStroke();
	}

	public void setBorderStroke(BasicStroke borderStroke) {
		this.borderStroke = borderStroke;
	}

	public double getScale() {
		return scale != -1 ? scale : defaultAttributes.getScale();
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public double getHighlightScale() {
		return highlightScale != -1 ? highlightScale : defaultAttributes
				.getHighlightScale();
	}

	public void setHighlightScale(double highlightScale) {
		this.highlightScale = highlightScale;
	}

	public Point getDrawOffset() {
		return drawOffset != null ? drawOffset : defaultAttributes
				.getDrawOffset();
	}

	public void setDrawOffset(Point drawOffset) {
		this.drawOffset = drawOffset;
	}

	public double getOpacity() {
		return opacity != -1 ? opacity : defaultAttributes.getOpacity();
	}

	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}

	public AnnotationAttributes clone() {
		try {
			AnnotationAttributes clone = (AnnotationAttributes) super.clone();
			clone.setAdjustWidthToText(adjustWidthToText);
			clone.setBackgroundColor(backgroundColor);
			clone.setBackgroundImage(backgroundImage);
			clone.setBorderColor(borderColor);
			clone.setBorderStroke(borderStroke);
			clone.setCornerRadius(cornerRadius);
			clone.setDefaults(defaultAttributes);
			clone.setDrawOffset(drawOffset);
			clone.setEffect(effect);
			clone.setFont(font);
			clone.setFrameHorizontalAlign(frameHorizontalAlign);
			clone.setFrameVerticalAlign(frameVerticalAlign);
			clone.setHighlightScale(highlightScale);
			clone.setInsets(insets);
			clone.setOpacity(opacity);
			clone.setScale(scale);
			clone.setSize(size);
			clone.setTextAlign(textAlign);
			clone.setTextColor(textColor);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}
}
