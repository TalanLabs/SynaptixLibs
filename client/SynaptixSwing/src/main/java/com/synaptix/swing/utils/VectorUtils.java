package com.synaptix.swing.utils;

import java.awt.geom.Point2D;

public final class VectorUtils {

	public static Point2D computeVector(Point2D a, Point2D b) {
		double xAB = b.getX() - a.getX();
		double yAB = b.getY() - a.getY();

		double norme = Math.sqrt(xAB * xAB + yAB * yAB);
		if (norme > 0) {
			return new Point2D.Double(xAB / norme, yAB / norme);
		}
		return null;
	}

	public static double computeNorme(Point2D a, Point2D b) {
		double xAB = b.getX() - a.getX();
		double yAB = b.getY() - a.getY();

		return Math.sqrt(xAB * xAB + yAB * yAB);
	}
	
	public static Point2D computeNormalVector(Point2D a, Point2D b) {
		Point2D p = computeVector(a, b);
		if (p != null) {
			return new Point2D.Double(-p.getY(), p.getX());
		}
		return null;
	}

	public static Point2D inverseVector(Point2D a) {
		return new Point2D.Double(-a.getX(), -a.getY());
	}

	public static Point2D sommeVector(Point2D a, Point2D b) {
		return new Point2D.Double(a.getX() + b.getX(), a.getY() + b.getY());
	}

	public static Point2D computePerpendiculaireVector(Point2D a) {
		return new Point2D.Double(-a.getY(), a.getX());
	}
}
