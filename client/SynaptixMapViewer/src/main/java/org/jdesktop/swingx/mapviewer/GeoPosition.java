/*
 * GeoPosition.java
 *
 * Created on March 31, 2006, 9:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

/**
 * An immutable coordinate in the real (geographic) world, composed of a
 * latitude and a longitude.
 * 
 * @author rbair
 */
public class GeoPosition {

	public static final double WGS84_EQUATORIAL_RADIUS = 6378137.0;

	private final double latitude;

	private final double longitude;

	/**
	 * Creates a new instance of GeoPosition from the specified latitude and
	 * longitude. These are double values in decimal degrees, not degrees,
	 * minutes, and seconds. Use the other constructor for those.
	 * 
	 * @param latitude
	 *            a latitude value in decmial degrees
	 * @param longitude
	 *            a longitude value in decimal degrees
	 */
	public GeoPosition(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	// must be an array of length two containing lat then long in that order.
	/**
	 * Creates a new instance of GeoPosition from the specified latitude and
	 * longitude as an array of two doubles, with the latitude first. These are
	 * double values in decimal degrees, not degrees, minutes, and seconds. Use
	 * the other constructor for those.
	 * 
	 * @param coords
	 *            latitude and longitude as a double array of length two
	 */
	public GeoPosition(double[] coords) {
		this.latitude = coords[0];
		this.longitude = coords[1];
	}

	/**
	 * Creates a new instance of GeoPosition from the specified latitude and
	 * longitude. Each are specified as degrees, minutes, and seconds; not as
	 * decimal degrees. Use the other constructor for those.
	 * 
	 * @param latDegrees
	 *            the degrees part of the current latitude
	 * @param latMinutes
	 *            the minutes part of the current latitude
	 * @param latSeconds
	 *            the seconds part of the current latitude
	 * @param lonDegrees
	 *            the degrees part of the current longitude
	 * @param lonMinutes
	 *            the minutes part of the current longitude
	 * @param lonSeconds
	 *            the seconds part of the current longitude
	 */
	public GeoPosition(double latDegrees, double latMinutes, double latSeconds,
			double lonDegrees, double lonMinutes, double lonSeconds) {
		this(latDegrees + (latMinutes + latSeconds / 60.0) / 60.0, lonDegrees
				+ (lonMinutes + lonSeconds / 60.0) / 60.0);
	}

	/**
	 * Get the latitude as decimal degrees
	 * 
	 * @return the latitude as decimal degrees
	 */
	public final double getLatitude() {
		return latitude;
	}

	/**
	 * Get the longitude as decimal degrees
	 * 
	 * @return the longitude as decimal degrees
	 */
	public final double getLongitude() {
		return longitude;
	}

	/**
	 * Calcul la distance en mètre
	 * 
	 * @param gp
	 * @return
	 */
	public double distance(GeoPosition gp) {
		double dLat = Math.toRadians(gp.getLatitude() - latitude);
		double dLon = Math.toRadians(gp.getLongitude() - longitude);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(latitude))
				* Math.cos(Math.toRadians(gp.getLatitude()))
				* Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return WGS84_EQUATORIAL_RADIUS * c;
	}

	/**
	 * Calcul la position en ajouter une distance en latMeter et lonMeter
	 * 
	 * @param latMeter
	 *            en mètre
	 * @param lonMeter
	 *            en mètre
	 * @return
	 */
	public GeoPosition add(double latMeter, double lonMeter) {
		double dLat = latMeter / WGS84_EQUATORIAL_RADIUS;
		double dLon = lonMeter
				/ (WGS84_EQUATORIAL_RADIUS * Math.cos(Math.PI * latitude / 180));
		return new GeoPosition(latitude + Math.toDegrees(dLat), longitude
				+ Math.toDegrees(dLon));
	}

	/**
	 * Calcul la position milieu entre les 2 points
	 * 
	 * @param gp
	 * @return
	 */
	public GeoPosition middle(GeoPosition gp) {
		return new GeoPosition((latitude + gp.latitude) / 2.0,
				(longitude + gp.longitude) / 2.0);
	}

	/**
	 * Returns true the specified GeoPosition and this GeoPosition represent the
	 * exact same latitude and longitude coordinates.
	 * 
	 * @param obj
	 *            a GeoPosition to compare this GeoPosition to
	 * @return returns true if the specified GeoPosition is equal to this one
	 */
	public boolean equals(Object obj) {
		if (obj instanceof GeoPosition) {
			GeoPosition coord = (GeoPosition) obj;
			return coord.latitude == latitude && coord.longitude == longitude;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	public String toString() {
		return "[" + latitude + ", " + longitude + "]";
	}
}