package com.synaptix.widget.skin;

import java.awt.Color;

import org.pushingpixels.substance.api.colorscheme.BaseDarkColorScheme;

public class BlueColorScheme extends BaseDarkColorScheme {
	/**
	 * The main ultra-light color.
	 */
	private static final Color mainUltraDarkColor = new Color(0, 61, 107);

	/**
	 * The main extra light color.
	 */
	private static final Color mainDarkColor = new Color(0, 79, 139);

	/**
	 * The main light color.
	 */
	private static final Color mainMidColor = new Color(16, 151, 255);

	/**
	 * The main medium color.
	 */
	private static final Color mainLightColor = new Color(68, 174, 225);

	/**
	 * The main dark color.
	 */
	private static final Color mainExtraLightColor = new Color(121, 197, 255);

	/**
	 * The main ultra-dark color.
	 */
	private static final Color mainUltraLightColor = new Color(169, 218, 255);

	/**
	 * The foreground color.
	 */
	private static final Color foregroundColor = Color.white;

	/**
	 * Creates a new <code></code> color scheme.
	 */
	public BlueColorScheme() {
		super("Dark Gray");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getForegroundColor()
	 */
	@Override
	public Color getForegroundColor() {
		return BlueColorScheme.foregroundColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getUltraLightColor()
	 */
	@Override
	public Color getUltraLightColor() {
		return BlueColorScheme.mainUltraLightColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getExtraLightColor()
	 */
	@Override
	public Color getExtraLightColor() {
		return BlueColorScheme.mainExtraLightColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getLightColor()
	 */
	@Override
	public Color getLightColor() {
		return BlueColorScheme.mainLightColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getMidColor()
	 */
	@Override
	public Color getMidColor() {
		return BlueColorScheme.mainMidColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getDarkColor()
	 */
	@Override
	public Color getDarkColor() {
		return BlueColorScheme.mainDarkColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.substance.color.ColorScheme#getUltraDarkColor()
	 */
	@Override
	public Color getUltraDarkColor() {
		return BlueColorScheme.mainUltraDarkColor;
	}
}
