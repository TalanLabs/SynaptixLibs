package com.synaptix.geotools.lambert;

import com.synaptix.geotools.MathTools;

/**
 * @author philipakis
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LambertIIIConstants implements LambertConstants {

	/**
	 * @see LambertConstants#getN()
	 */
	public double getN() {
		return 0.6959127966;
	}

	/**
	 * @see LambertConstants#getC()
	 */
	public double getC() {
		return 11947992.52;
	}

	/**
	 * @see LambertConstants#getXs()
	 */
	public double getXs() {
		return 600000;
	}

	/**
	 * @see LambertConstants#getYx()
	 */
	public double getYs() {
		return 6791905.085;
	}

	/**
	 * @see LambertConstants#getXsYs()
	 */
	public double[] getXsYs() {
		return new double[]{600000,6791905.085};
	}

	/**
	 * @see LambertConstants#getLambda0()
	 */
	public double getLambdaC() {
		return MathTools.degreVersRadian(2,20,14.025);
		//return 0;
	}

	/**
	 * @see LambertConstants#getE()
	 */
	public double getE() {
		return 0.08248325676;
	}

}
