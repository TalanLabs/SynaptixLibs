package com.synaptix.swing.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class GraphicsHelper {

	private static boolean activeAntiAliasing = true;

	public GraphicsHelper() {
	}

	public static boolean isActiveAntiAliasing() {
		return activeAntiAliasing;
	}

	public static void setActiveAntiAliasing(boolean activeAntiAliasing) {
		GraphicsHelper.activeAntiAliasing = activeAntiAliasing;
	}

	/**
	 * Cette fonction active l'anti-aliasing sur le graphics2D qui lui est passé
	 * en paramètre.
	 */
	public static void activeAntiAliasing(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	}

	/**
	 * Cette fonction désactive l'anti-aliasing sur le graphics2D qui lui est
	 * passé en paramètre.
	 */
	public static void desactiveAntiAliasing(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	/**
	 * Cette méthode met à jour l'anti-aliasing en fonction du paramètre
	 * 'activeAntiAliasing'.
	 */
	public static void useParamAntiAliasing(Graphics2D g2) {
		if (activeAntiAliasing) {
			activeAntiAliasing(g2);
		} else {
			desactiveAntiAliasing(g2);
		}
	}
}
