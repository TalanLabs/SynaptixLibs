package com.synaptix.geotools;

import com.synaptix.geotools.GeoHelper;

public class ConversionMain {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out
					.println("Usage : java -jar GeoTools-0.0.1-SNAPSHOT.jar longitude latitude (en degre)");
		} else {
			double[] ds = GeoHelper.WGS84DegToLambertIIE(Double
					.parseDouble(args[0]), Double.parseDouble(args[1]));
			System.out.println(String.valueOf(ds[0]) + " "
					+ String.valueOf(ds[1]));
		}
	}

}
