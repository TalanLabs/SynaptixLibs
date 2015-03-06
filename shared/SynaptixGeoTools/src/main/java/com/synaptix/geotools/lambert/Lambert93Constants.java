package com.synaptix.geotools.lambert;

import com.synaptix.geotools.MathTools;

/**
 * @author philipakis
 * 
 *         To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class Lambert93Constants implements LambertConstants {

	/**
	 * @see LambertConstants#getN()
	 */
	public double getN() {
		return 0.725607765;
	}

	/**
	 * @see LambertConstants#getC()
	 */
	public double getC() {
		return 11754255.426;
	}

	/**
	 * @see LambertConstants#getXs()
	 */
	public double getXs() {
		return 700000;
	}

	/**
	 * @see LambertConstants#getYx()
	 */
	public double getYs() {
		return 12655612.05;
	}

	/**
	 * @see LambertConstants#getXsYs()
	 */
	public double[] getXsYs() {
		return new double[] { getXs(), getYs() };
	}

	/**
	 * @see LambertConstants#getLambda0()
	 */
	public double getLambdaC() {
		return MathTools.degreVersRadian(3);
	}

	/**
	 * @see LambertConstants#getE()
	 */
	public double getE() {
		return 0.08181919106;
	}

}
