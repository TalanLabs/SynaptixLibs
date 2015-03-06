package com.synaptix.swingx.mapviewer.layers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.mapviewer.AVKey;
import org.jdesktop.swingx.mapviewer.DrawContext;

import com.synaptix.swingx.mapviewer.layers.render.AnnotationAttributes;
import com.synaptix.swingx.mapviewer.layers.render.ScreenAnnotation;

/**
 * Calque qui permet d'afficher une legende
 * 
 * @author Gaby
 * 
 */
public class LegendLayer extends RenderableLayer implements AVKey {

	private String title;

	private String position;

	private ScreenAnnotation legendSa;

	private int imageWidth = 32;

	private int imageHeight = 32;

	private int space = 5;

	private List<Line> lines = new ArrayList<Line>();

	private Point2D locationOffset = null;

	private Point2D locationCenter = null;

	private AnnotationAttributes titleAnnotationAttributes;

	private AnnotationAttributes imageAnnotationAttributes;

	private AnnotationAttributes textAnnotationAttributes;

	public LegendLayer(String title, String position) {
		super();

		this.setPickEnabled(false);
		this.setAntialiasing(true);
		this.setCacheable(true);

		this.title = title;
		this.position = position;

		initialize();

		this.legendSa = new ScreenAnnotation(title, new Point(0, 0));
		legendSa.getAnnotationAttributes().setDefaults(
				titleAnnotationAttributes);
		legendSa.setHighlightable(false);
		this.addRenderable(legendSa);
	}

	private void initialize() {
		titleAnnotationAttributes = new AnnotationAttributes();
		titleAnnotationAttributes.setCornerRadius(0);
		titleAnnotationAttributes.setBackgroundColor(new Color(0, 0, 0, 0.5f));
		titleAnnotationAttributes.setTextColor(Color.white);
		titleAnnotationAttributes.setScale(1);
		titleAnnotationAttributes.setHighlightScale(1.2);
		titleAnnotationAttributes.setOpacity(1.0);
		titleAnnotationAttributes.setDrawOffset(new Point(0, 0));
		titleAnnotationAttributes.setAdjustWidthToText(AVKey.SIZE_FIXED);
		titleAnnotationAttributes.setSize(new Dimension(300, 200));
		titleAnnotationAttributes.setTextAlign(AVKey.ALIGN_CENTER);
		titleAnnotationAttributes.setFont(Font.decode("Arial-BOLD-20"));
		titleAnnotationAttributes.setInsets(new Insets(5, 5, 5, 5));
		titleAnnotationAttributes.setFrameHorizontalAlign(AVKey.ALIGN_LEFT);
		titleAnnotationAttributes.setFrameVerticalAlign(AVKey.ALIGN_TOP);

		imageAnnotationAttributes = new AnnotationAttributes();
		imageAnnotationAttributes.setCornerRadius(0);
		imageAnnotationAttributes.setBackgroundColor(new Color(0, 0, 0, 0));
		imageAnnotationAttributes.setBorderColor(new Color(0, 0, 0, 0));
		imageAnnotationAttributes.setScale(1);
		imageAnnotationAttributes.setOpacity(1.0);
		imageAnnotationAttributes.setDrawOffset(new Point(0, 0));
		imageAnnotationAttributes.setInsets(new Insets(0, 0, 0, 0));
		imageAnnotationAttributes.setAdjustWidthToText(AVKey.SIZE_FIXED);
		imageAnnotationAttributes.setFrameHorizontalAlign(AVKey.ALIGN_LEFT);
		imageAnnotationAttributes.setFrameVerticalAlign(AVKey.ALIGN_TOP);

		textAnnotationAttributes = new AnnotationAttributes();
		textAnnotationAttributes.setCornerRadius(0);
		textAnnotationAttributes.setBackgroundColor(new Color(0, 0, 0, 0));
		textAnnotationAttributes.setTextColor(Color.white);
		textAnnotationAttributes.setBorderColor(new Color(0, 0, 0, 0));
		textAnnotationAttributes.setScale(1);
		textAnnotationAttributes.setOpacity(1.0);
		textAnnotationAttributes.setDrawOffset(new Point(0, 0));
		textAnnotationAttributes.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);
		textAnnotationAttributes.setTextAlign(AVKey.ALIGN_LEFT);
		textAnnotationAttributes.setInsets(new Insets(0, 0, 0, 0));
		textAnnotationAttributes.setFrameHorizontalAlign(AVKey.ALIGN_LEFT);
		textAnnotationAttributes.setFrameVerticalAlign(AVKey.ALIGN_TOP);
	}

	/**
	 * Ajoute une ligne à la légende
	 * 
	 * @param image
	 * @param text
	 */
	public void addLine(Image image, String text) {
		Line line = new Line();

		line.imageSa = new ScreenAnnotation(null, new Point(0, 0));
		line.imageSa.getAnnotationAttributes().setDefaults(
				imageAnnotationAttributes);
		line.imageSa.getAnnotationAttributes().setBackgroundImage(image);
		line.imageSa.setHighlightable(false);
		this.addRenderable(line.imageSa);

		line.textSa = new ScreenAnnotation(text, new Point(0, 0));
		line.textSa.getAnnotationAttributes().setDefaults(
				textAnnotationAttributes);
		line.textSa.setHighlightable(false);
		this.addRenderable(line.textSa);

		lines.add(line);

		clearCache();
	}

	/**
	 * Efface toutes les lignes de la légende
	 */
	public void removeAllLines() {
		lines.clear();
		clearCache();
	}

	public void setLocationOffset(Point2D offsetPoint) {
		this.locationOffset = offsetPoint;
		clearCache();
	}

	public Point2D getLocationOffset() {
		return locationOffset;
	}

	public Point2D getLocationCenter() {
		return locationCenter;
	}

	public void setLocationCenter(Point2D locationCenter) {
		this.locationCenter = locationCenter;
		clearCache();
	}

	public void setPosition(String position) {
		this.position = position;
		clearCache();
	}

	public String getPosition() {
		return position;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		clearCache();
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
		clearCache();
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
		clearCache();
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public AnnotationAttributes getImageAnnotationAttributes() {
		return imageAnnotationAttributes;
	}

	public AnnotationAttributes getTitleAnnotationAttributes() {
		return titleAnnotationAttributes;
	}

	public AnnotationAttributes getTextAnnotationAttributes() {
		return textAnnotationAttributes;
	}

	private void compute(DrawContext dc) {
		int legendWidth = titleAnnotationAttributes.getSize().width;

		Insets insets = titleAnnotationAttributes.getInsets();

		// On calcule la hauteur max
		int h = insets.top + 25;
		for (int i = 0; i < lines.size(); i++) {
			if (i > 0) {
				h += space;
			}
			Line line = lines.get(i);
			line.imageSa.getAnnotationAttributes().setSize(
					new Dimension(imageWidth, imageHeight));
			line.textSa.getAnnotationAttributes().setSize(
					new Dimension(legendWidth - imageWidth - space
							- insets.left - insets.right, 0));

			Dimension imageSize = line.imageSa.getPreferredSize(dc);
			Dimension textSize = line.textSa.getPreferredSize(dc);

			h += Math.max(imageSize.height, textSize.height);
		}
		h += insets.bottom;

		int x = 0;
		int y = 0;
		if (locationCenter != null) {
			x = (int) (locationCenter.getX() - legendWidth / 2.0);
			y = (int) (locationCenter.getY() - h / 2.0);
		} else if (AVKey.NORTHWEST.equals(position)) {
			x = 10;
			y = 10;
		} else if (AVKey.NORTHEAST.equals(position)) {
			x = dc.getDrawableWidth() - legendWidth - 10;
			y = 10;
		} else if (AVKey.SOUTHWEST.equals(position)) {
			x = 10;
			y = dc.getDrawableHeight() - h - 10;
		} else if (AVKey.SOUTHEAST.equals(position)) {
			x = dc.getDrawableWidth() - legendWidth - 10;
			y = dc.getDrawableHeight() - h - 10;
		}

		if (locationOffset != null) {
			x += locationOffset.getX();
			y += locationOffset.getY();
		}

		this.legendSa.setScreenPoint(new Point(x, y));

		x += insets.left;
		y += insets.top + 25;

		for (int i = 0; i < lines.size(); i++) {
			if (i > 0) {
				y += space;
			}
			Line line = lines.get(i);
			line.imageSa.setScreenPoint(new Point(x, y));
			line.textSa.setScreenPoint(new Point(x + imageWidth + space, y));

			Dimension imageSize = line.imageSa.getPreferredSize(dc);
			Dimension textSize = line.textSa.getPreferredSize(dc);

			int hLine = Math.max(imageSize.height, textSize.height);
			y += hLine;
		}

		legendSa.getAnnotationAttributes().setSize(
				new Dimension(legendWidth, h));
	}

	@Override
	protected void doPick(DrawContext dc, Point point) {
		compute(dc);
		super.doPick(dc, point);
	}

	@Override
	protected void doPaint(Graphics2D g, DrawContext dc) {
		compute(dc);
		super.doPaint(g, dc);
	}

	private final class Line {

		ScreenAnnotation imageSa;

		ScreenAnnotation textSa;

	}
}
