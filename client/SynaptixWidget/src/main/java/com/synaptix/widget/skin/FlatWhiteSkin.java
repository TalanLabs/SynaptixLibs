package com.synaptix.widget.skin;

import java.awt.Insets;

import javax.swing.AbstractButton;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ColorSchemeTransform;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceColorSchemeBundle;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.colorscheme.LightGrayColorScheme;
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter;
import org.pushingpixels.substance.api.painter.border.CompositeBorderPainter;
import org.pushingpixels.substance.api.painter.border.DelegateBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.FlatDecorationPainter;
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
		SubstanceColorScheme activeScheme = new BlueColorScheme().named("FlatWhite Active");
		SubstanceColorScheme enabledScheme = new WhiteColorScheme().tint(0.3);
		SubstanceColorScheme disabledScheme = new LightGrayColorScheme().tint(0.35).named("FlatWhite Disabled");

		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(activeScheme, enabledScheme, disabledScheme);
		defaultSchemeBundle.registerColorScheme(new WhiteColorScheme(), ColorSchemeAssociationKind.TAB, ComponentState.SELECTED);

		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle, DecorationAreaType.NONE);

		this.registerAsDecorationArea(enabledScheme, DecorationAreaType.PRIMARY_TITLE_PANE, DecorationAreaType.SECONDARY_TITLE_PANE, DecorationAreaType.HEADER, DecorationAreaType.FOOTER,
				DecorationAreaType.GENERAL, DecorationAreaType.TOOLBAR);

		this.registerAsDecorationArea(disabledScheme, DecorationAreaType.PRIMARY_TITLE_PANE_INACTIVE, DecorationAreaType.SECONDARY_TITLE_PANE_INACTIVE);

		setSelectedTabFadeStart(0.0);
		setSelectedTabFadeEnd(0.0);

		this.buttonShaper = new ClassicButtonShaper() {
			@Override
			public float getCornerRadius(AbstractButton button, Insets insets) {
				return 0;
			}
		};
		this.fillPainter = new FlatFillPainter();
		this.decorationPainter = new FlatDecorationPainter();
		this.highlightPainter = new FlatHighlightPainter() {

		};
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
