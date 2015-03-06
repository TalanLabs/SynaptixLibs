package com.synaptix.geotools;

/**
 * @author administrateur
 * 
 *         To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates. To enable and disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class GeoAlgo {

	/**
	 * Method calculeLatitudeIsometrique.
	 * 
	 * Ref : IGN, Projection cartographique conique conforme de Lambert ALGO : ALG0001
	 * 
	 * @param e
	 *            1ere excentricite de l'ellipsoide.
	 * @param lat
	 *            latitude.
	 * @return double latitude isometrique.
	 */
	public static double calculeLatitudeIsometrique(double e, double lat) {
		double slat = Math.sin(lat);
		double eslat = e * slat;
		double a = (1 + slat) / (1 - slat);
		double b = (1 + eslat) / (1 - eslat);
		return (Math.log(a) - e * Math.log(b)) / 2;
	}

	/**
	 * Method calculeLatitudeIsometrique.
	 * 
	 * Ref : IGN, Projection cartographique conique conforme de Lambert ALGO : ALG0001
	 * 
	 * @param e
	 *            1ere excentricite de l'ellipsoide.
	 * @param lat
	 *            latitude.
	 * @return double latitude isometrique.
	 */
	public static double calculeLatitudeIsometrique(double params[]) {
		double slat = Math.sin(params[1]);
		double eslat = params[0] * slat;
		double a = (1 + slat) / (1 - slat);
		double b = (1 + eslat) / (1 - eslat);
		return (Math.log(a) - params[0] * Math.log(b)) / 2;
	}

	/**
	 * Method calculeLambertXYInterne.
	 * 
	 * Ref : IGN, Projection cartographique conique conforme de Lambert ALGO : ALG0003
	 * 
	 * @param e
	 *            premiere excentricite de l'ellipsoide.
	 * @param C
	 *            constante de la projection.
	 * @param lambdaC
	 *            longitude de l'origine par rapport au meridien origine.
	 * @param Xs
	 *            coordonnee en projection du pole
	 * @param Ys
	 *            coordonnee en projection du pole
	 * @param n
	 *            exposant de la projection
	 * @param lo
	 *            longitude du point
	 * @param la
	 *            latitude du point
	 * @param xy
	 *            coordonnee en projection du point
	 */
	public static void calculeLambertXYInterne(double e, double C, double lambdaC, double Xs, double Ys, double n, double lo, double la, double[] xy) {
		double liso = GeoAlgo.calculeLatitudeIsometrique(e, la);
		double c_e_n_liso = C * Math.exp(-n * liso);
		double nlola = n * (lo - lambdaC);

		xy[0] = Xs + c_e_n_liso * Math.sin(nlola);
		xy[1] = Ys - c_e_n_liso * Math.cos(nlola);
	}

	/**
	 * Method calculeLambertXYInterne.
	 * 
	 * Ref : IGN, Projection cartographique conique conforme de Lambert ALGO : ALG0003
	 * 
	 * @param params
	 *            [0] premiere excentricite de l'ellipsoide.
	 * @param params
	 *            [1] constante de la projection.
	 * @param params
	 *            [2] longitude de l'origine par rapport au meridien origine.
	 * @param params
	 *            [3] coordonnee en projection du pole
	 * @param params
	 *            [4] coordonnee en projection du pole
	 * @param params
	 *            [5] exposant de la projection
	 * @param params
	 *            [6] longitude du point
	 * @param params
	 *            [7] latitude du point
	 * @param xy
	 *            coordonnee en projection du point
	 */
	public static void calculeLambertXYInterne(double params[], double[] xy) {
		double liso = GeoAlgo.calculeLatitudeIsometrique(params[0], params[7]);
		double c_e_n_liso = params[1] * Math.exp(-params[5] * liso);
		double nlola = params[5] * (params[6] - params[2]);

		xy[0] = params[3] + c_e_n_liso * Math.sin(nlola);
		xy[1] = params[4] - c_e_n_liso * Math.cos(nlola);
	}
}
