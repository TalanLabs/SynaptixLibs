package com.synaptix.widget.skin;

import java.awt.Color;

import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.painter.fill.StandardFillPainter;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;

public class FlatFillPainter extends StandardFillPainter {

	@Override
	public Color getTopFillColor(SubstanceColorScheme fillScheme) {
		return SubstanceColorUtilities.getMidFillColor(fillScheme);
	}

	@Override
	public Color getTopShineColor(SubstanceColorScheme fillScheme) {
		return getTopFillColor(fillScheme);
	}

	@Override
	public Color getMidFillColorTop(SubstanceColorScheme fillScheme) {
		return getTopFillColor(fillScheme);
	}

	@Override
	public Color getMidFillColorBottom(SubstanceColorScheme fillScheme) {
		return getTopFillColor(fillScheme);
	}

	@Override
	public Color getBottomShineColor(SubstanceColorScheme fillScheme) {
		return getTopFillColor(fillScheme);
	}

	@Override
	public Color getBottomFillColor(SubstanceColorScheme fillScheme) {
		return getTopFillColor(fillScheme);
	}
}
