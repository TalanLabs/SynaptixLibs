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
public class LambertIVConstants implements LambertConstants {

	/**
	 * @see LambertConstants#getN()
	 */
	public double getN() {
		return 0.6712679322;
	}

	/**
	 * @see LambertConstants#getC()
	 */
	public double getC() {
		return 12136281.99;
	}

	/**
	 * @see LambertConstants#getXs()
	 */
	public double getXs() {
		return 234.358;
	}

	/**
	 * @see LambertConstants#getYx()
	 */
	public double getYs() {
		return 7239161.542;
	}

	/**
	 * @see LambertConstants#getXsYs()
	 */
	public double[] getXsYs() {
		return new double[]{234.358,7239161.542};
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
