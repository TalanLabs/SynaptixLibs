package com.synaptix.widget.skin;

import org.pushingpixels.substance.api.ColorSchemeTransform;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceColorSchemeBundle;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.colorscheme.LightAquaColorScheme;
import org.pushingpixels.substance.api.colorscheme.LightGrayColorScheme;
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter;
import org.pushingpixels.substance.api.painter.border.CompositeBorderPainter;
import org.pushingpixels.substance.api.painter.border.DelegateBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.FlatDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.ClassicFillPainter;
import org.pushingpixels.substance.api.painter.highlight.ClassicHighlightPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;

public class FlatWhiteSkin extends SubstanceSkin {
	/**
	 * Display name for <code>this</code> skin.
	 */
	public static final String NAME = "FlatWhite";

	/**
	 * Creates a new <code>FlatWhite</code> skin.
	 */
	public FlatWhiteSkin() {
		SubstanceColorScheme activeScheme = new LightAquaColorScheme().tint(0.3).named("FlatWhite Active");
		SubstanceColorScheme enabledScheme = new WhiteColorScheme();
		SubstanceColorScheme disabledScheme = new LightGrayColorScheme().tint(0.35).named("FlatWhite Disabled");

		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(activeScheme, enabledScheme, disabledScheme);
		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle, DecorationAreaType.NONE);

		this.registerAsDecorationArea(enabledScheme, DecorationAreaType.PRIMARY_TITLE_PANE, DecorationAreaType.SECONDARY_TITLE_PANE, DecorationAreaType.HEADER, DecorationAreaType.FOOTER,
				DecorationAreaType.GENERAL, DecorationAreaType.TOOLBAR);

		this.registerAsDecorationArea(disabledScheme, DecorationAreaType.PRIMARY_TITLE_PANE_INACTIVE, DecorationAreaType.SECONDARY_TITLE_PANE_INACTIVE);

		setSelectedTabFadeStart(0.2);
		setSelectedTabFadeEnd(0.4);

		this.buttonShaper = new ClassicButtonShaper();
		this.fillPainter = new ClassicFillPainter();
		this.decorationPainter = new FlatDecorationPainter();
		this.highlightPainter = new ClassicHighlightPainter();
		this.borderPainter = new CompositeBorderPainter("FlatWhite", new ClassicBorderPainter(), new DelegateBorderPainter("FlatWhite Inner", new ClassicBorderPainter(), new ColorSchemeTransform() {
			@Override
			public SubstanceColorScheme transform(SubstanceColorScheme scheme) {
				return scheme.tint(0.9f);
			}
		}));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.skin.SubstanceSkin#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return NAME;
	}
}
