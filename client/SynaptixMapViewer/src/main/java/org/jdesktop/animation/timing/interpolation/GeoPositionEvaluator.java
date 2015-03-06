package org.jdesktop.animation.timing.interpolation;

import org.jdesktop.swingx.mapviewer.GeoPosition;

public class GeoPositionEvaluator extends Evaluator<GeoPosition> {

	public GeoPosition evaluate(GeoPosition gp1, GeoPosition gp2, float fraction) {
		return new GeoPosition(evaluate(gp1.getLatitude(), gp2.getLatitude(),
				fraction), evaluate(gp1.getLongitude(), gp2.getLongitude(),
				fraction));
	}

	private double evaluate(double a, double b, float fraction) {
		return a + (b - a) * fraction;
	}
}
