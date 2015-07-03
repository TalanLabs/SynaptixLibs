package com.synaptix.widget.skin;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.painter.fill.SubstanceFillPainter;
import org.pushingpixels.substance.api.painter.highlight.SubstanceHighlightPainter;

public class FlatHighlightPainter implements SubstanceHighlightPainter {

	/**
	 * The display name for the highlight painters of this class.
	 */
	public static final String DISPLAY_NAME = "Flat";

	/**
	 * Single gradient painter instance.
	 */
	protected SubstanceFillPainter painter;

	/**
	 * Creates new classic title painter.
	 */
	public FlatHighlightPainter() {
		this.painter = new FlatFillPainter();
	}

	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	@Override
	public void paintHighlight(Graphics2D graphics, Component comp, int width, int height, SubstanceColorScheme colorScheme) {
		Graphics2D g2d = (Graphics2D) graphics.create();
		g2d.translate(-3, -3);
		this.painter.paintContourBackground(g2d, comp, width + 6, height + 6, new Rectangle(width + 6, height + 6), false, colorScheme, false);
		g2d.dispose();
	}
}
