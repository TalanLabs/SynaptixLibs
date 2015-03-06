package com.synaptix.geotools;
/**
 * @author philipakis
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MathTools {
	public static double degreVersRadian(double d, double m, double s){
		double d2 = (d+m/60+s/3600)/180;
		return d2*Math.PI;
	} 
	public static double degreVersRadian(double d){
		double d2 = (d)/180;
		return d2*Math.PI;
	} 
	
	public static double[] radianVersDegre(double r){
		double d = r/Math.PI*180;
		double [] deg = new double[3];
		deg[0] = Math.floor(d);
		double m = d-deg[0];
		deg[1] = Math.floor(m*60);
		deg[2] = m*60-deg[1];
		return deg;
	}
	public static double radianVersDegreDec(double r){
		return r/Math.PI*180;
	}
	
	public static double chaineVersDegre(String str, int d, int m, int s){
		double deg = Double.parseDouble(str.substring(0,d));
		double min = Double.parseDouble(str.substring(d,d+m));
		double sec = Double.parseDouble(str.substring(d+m,d+m+s));
		
		return deg+min/60+sec/3600;
	}

	public static int degreVersEntier(double d, int dec){
		return (int) Math.round(d*(Math.pow(10.0,dec)));
	}
	
}
