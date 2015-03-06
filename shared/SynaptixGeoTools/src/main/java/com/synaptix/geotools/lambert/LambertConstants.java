package com.synaptix.geotools.lambert;

/**
 * @author philipakis
 * 
 *         To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates. To enable and disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public interface LambertConstants {
	// exposant de la projection
	public double getN();

	// constante de la projection
	public double getC();

	// Coordonnees en projection du pole
	public double getXs();

	public double getYs();

	public double[] getXsYs();

	// longitude origine par rapport au meridien origine.
	public double getLambdaC();

	// 1 ere excentricite de l'ellipsoede.
	public double getE();
}
