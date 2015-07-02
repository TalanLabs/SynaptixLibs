package com.synaptix.widget.skin;

import java.awt.Color;

import org.pushingpixels.substance.api.colorscheme.BaseDarkColorScheme;

public class WhiteColorScheme extends BaseDarkColorScheme {

	/**
	 * The main ultra-light color.
	 */
	private static final Color mainUltraDarkColor = new Color(100, 100, 100);

	/**
	 * The main extra light color.
	 */
	private static final Color mainDarkColor = new Color(180, 180, 180);

	/**
	 * The main light color.
	 */
	private static final Color mainMidColor = new Color(210, 210, 210);

	/**
	 * The main medium color.
	 */
	private static final Color mainLightColor = new Color(225, 225, 225);

	/**
	 * The main dark color.
	 */
	private static final Color mainExtraLightColor = new Color(240, 240, 240);

	/**
	 * The main ultra-dark color.
	 */
	private static final Color mainUltraLightColor = new Color(250, 250, 250);

	/**
	 * The foreground color.
	 */
	private static final Color foregroundColor = Color.black;

	/**
	 * Creates a new <code></code> color scheme.
	 */
	public WhiteColorScheme() {
		super("White");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getForegroundColor()
	 */
	@Override
	public Color getForegroundColor() {
		return WhiteColorScheme.foregroundColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getUltraLightColor()
	 */
	@Override
	public Color getUltraLightColor() {
		return WhiteColorScheme.mainUltraLightColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getExtraLightColor()
	 */
	@Override
	public Color getExtraLightColor() {
		return WhiteColorScheme.mainExtraLightColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getLightColor()
	 */
	@Override
	public Color getLightColor() {
		return WhiteColorScheme.mainLightColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getMidColor()
	 */
	@Override
	public Color getMidColor() {
		return WhiteColorScheme.mainMidColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getDarkColor()
	 */
	@Override
	public Color getDarkColor() {
		return WhiteColorScheme.mainDarkColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getUltraDarkColor()
	 */
	@Override
	public Color getUltraDarkColor() {
		return WhiteColorScheme.mainUltraDarkColor;
	}
}
