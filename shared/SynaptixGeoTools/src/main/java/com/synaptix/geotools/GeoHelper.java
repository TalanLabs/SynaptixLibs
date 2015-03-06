/*
 * Fichier : GeoHelper.java
 * Projet  : GPSTrains
 * Date    : 27 aout 2004
 * Auteur  : sps
 *
 */
package com.synaptix.geotools;

import com.synaptix.geotools.lambert.LambertIIEtendueConstants;
import com.synaptix.geotools.lambert.LambertProjections;

/**
 * Projet GPSTrains
 * 
 * @author sps
 */
public final class GeoHelper {
	private static LambertProjections projLambertIIE = new LambertProjections(new LambertIIEtendueConstants());

	/**
	 * Converts LambertIIEtendu Coordinates to WGS84 coordinates <strong>in degrees</strong>.
	 * 
	 * @param x
	 *            X coordinate in LambertIIEtendu System
	 * @param y
	 *            Y coordinate in LambertIIEtendu System
	 * @return array of double where first element is Longitude and second is Latitude.
	 */
	public static double[] lambertIIEToWGS84Deg(double x, double y) {
		double lola[] = new double[2];
		projLambertIIE.calculeLambert2WGSInterne(lola, x, y, 1E-11);
		lola[0] = MathTools.radianVersDegreDec(lola[0]);
		lola[1] = MathTools.radianVersDegreDec(lola[1]);
		return lola;
	}

	/**
	 * Converts LambertIIEtendu Coordinates to WGS84 coordinates <strong>in radians</strong>.
	 * 
	 * @param x
	 *            X coordinate in LambertIIEtendu System
	 * @param y
	 *            Y coordinate in LambertIIEtendu System
	 * @return array of double where first element is Longitude and second is Latitude.
	 */
	public static double[] lambertIIEToWGS84Rad(double x, double y) {
		double lola[] = new double[2];
		projLambertIIE.calculeLambert2WGSInterne(lola, x, y, 1E-11);
		return lola;
	}

	/**
	 * Converts WGS84 coordinates <strong>in degrees</strong> to LambertIIEtendu Coordinates.
	 * 
	 * @param lo
	 *            Longitude in <strong>in degrees</strong>
	 * @param la
	 *            Latitude in <strong>in degrees</strong>
	 * @return array of double {X,Y}.
	 */
	public static double[] WGS84DegToLambertIIE(double lo, double la) {
		double xy[] = new double[2];
		lo = MathTools.degreVersRadian(lo);
		la = MathTools.degreVersRadian(la);
		projLambertIIE.calculeWGS2LambertInterne(xy, lo, la);
		return xy;
	}

	/**
	 * Converts WGS84 coordinates <strong>in radians</strong> to LambertIIEtendu Coordinates.
	 * 
	 * @param lo
	 *            Longitude in <strong>in radians</strong>
	 * @param la
	 *            Latitude in <strong>in radians</strong>
	 * @return array of double {X,Y}.
	 */
	public static double[] WGS84RadToLambertIIE(double lo, double la) {
		double xy[] = new double[2];
		projLambertIIE.calculeWGS2LambertInterne(xy, lo, la);
		return xy;
	}
}
