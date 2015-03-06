package com.synaptix.geotools.lambert;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.synaptix.geotools.GeoAlgo;
import com.synaptix.geotools.MathTools;

/**
 * @author philipakis
 * 
 *         To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates. To enable and disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class LambertProjections {

	double e;
	double n;
	double Xs;
	double Ys;
	double C;
	double lambdaC;

	LambertConstants constants;

	public LambertProjections(LambertConstants csts) {
		constants = csts;
		e = constants.getE();
		n = constants.getN();
		Xs = constants.getXs();
		Ys = constants.getYs();
		C = constants.getC();
		lambdaC = constants.getLambdaC();
	}

	public static void main(String[] args) {

		System.out.println("test rad<->degre");
		double rad = 0.5;
		double[] deg = MathTools.radianVersDegre(rad);
		System.out.println(" result :" + MathTools.degreVersRadian(deg[0], deg[1], deg[2]));

		System.out.println(" deg = " + MathTools.chaineVersDegre("485215", 2, 2, 2));
		System.out.println(" deg MapInfo = " + MathTools.degreVersEntier(MathTools.chaineVersDegre("485215", 2, 2, 2), 5));
		System.out.println(" deg = " + MathTools.chaineVersDegre("0022034", 3, 2, 2));
		System.out.println(" deg MapInfo = " + MathTools.degreVersEntier(MathTools.chaineVersDegre("0022034", 3, 2, 2), 5));

		LambertProjections proj = new LambertProjections(new LambertIIEtendueConstants());
		System.out.println("test calculeLatitudeIsometrique");
		System.out.println("result = " + GeoAlgo.calculeLatitudeIsometrique(proj.e, 0.87266462600));
		System.out.println("result = " + GeoAlgo.calculeLatitudeIsometrique(proj.e, -0.3));
		System.out.println("result = " + GeoAlgo.calculeLatitudeIsometrique(proj.e, 0.19998903370));

		System.out.println("test calculeLatitude");
		System.out.println("result = " + proj.calculeLatitude(1.00552653648, 1E-11));
		System.out.println("result = " + proj.calculeLatitude(-.30261690060, 1E-11));
		System.out.println("result = " + proj.calculeLatitude(.2, 1E-11));

		// LambertProjections proj2 = new LambertProjections(new LambertIConstants());
		LambertProjections proj2 = new LambertProjections(new LambertIVConstants());
		System.out.println("test calculeLambertXY");
		double[] lambda = MathTools.radianVersDegre(0.145512099);
		double[] phi = MathTools.radianVersDegre(0.872664626);
		System.out.println("Lambda = " + lambda[0] + "e" + lambda[1] + "'" + lambda[2] + "''");
		System.out.println("Phi    = " + phi[0] + "e" + phi[1] + "'" + phi[2] + "''");
		double[] xy = proj2.calculeWGS2Lambert(0.14551209900, 0.87266462600);
		System.out.println("result = " + xy[0] + "," + xy[1]);
		System.out.println("result = " + proj2.calculeLatitude(-.30261690060, 1.10E-11));
		System.out.println("result = " + proj2.calculeLatitude(.2, 1.10E-11));
		// proj2.calculeFastLambertXYInterne(xy,0.14551209900,0.87266462600);
		// System.out.println("result = "+xy[0]+","+xy[1]);

		long ts = System.currentTimeMillis();

		for (int i = 0; i < 1000000; i++) {
			proj2.calculeWGS2LambertInterne(xy, 0.14551209900, 0.87266462600);
		}
		System.out.println("calcul de 1000000 transformations internes : " + (System.currentTimeMillis() - ts));

		ts = System.currentTimeMillis();

		double e = proj2.e;
		double C = proj2.C;
		double lambdaC = proj2.lambdaC;
		double Xs = proj2.Xs;
		double Ys = proj2.Ys;
		double n = proj2.n;

		double params[] = new double[] { e, C, lambdaC, Xs, Ys, n, 0, 0 };

		for (int i = 0; i < 1000000; i++) {
			// GeoAlgo.calculeLambertXYInterne(e, C,lambdaC, Xs, Ys, n, 0.14551209900,0.87266462600, xy);
			params[6] = 0.14551209900;
			params[7] = 0.87266462600;
			GeoAlgo.calculeLambertXYInterne(params, xy);
		}
		System.out.println("calcul de 1000000 transformations externes : " + (System.currentTimeMillis() - ts));

		ts = System.currentTimeMillis();

		for (int i = 0; i < 1000000; i++) {

		}
		System.out.println("On ne fait rien : " + (System.currentTimeMillis() - ts));

		System.out.println("test calculeXYLambert");
		proj2.calculeLambert2WGSInterne(xy, 1029705.083, 272723.849, 1E-11);
		System.out.println("result = " + xy[0] + "," + xy[1]);

		System.out.println("pour nico Toulouse St Cyprien");
		proj.calculeLambert2WGSInterne(xy, 525831.219, 1844197.456, 1E-11);
		double[] lo = MathTools.radianVersDegre(xy[0]);
		double[] la = MathTools.radianVersDegre(xy[1]);
		System.out.println("result = " + lo[0] + "e" + lo[1] + "'" + lo[2] + "," + la[0] + "e" + la[1] + "'" + la[2]);
		System.out.println("result = " + MathTools.radianVersDegreDec(xy[0]) + "," + MathTools.radianVersDegreDec(xy[1]));
		System.out.println("pour nico le TOEC ");
		proj.calculeLambert2WGSInterne(xy, 524504.588, 1844388.466, 1E-11);
		lo = MathTools.radianVersDegre(xy[0]);
		la = MathTools.radianVersDegre(xy[1]);
		System.out.println("result = " + lo[0] + "e" + lo[1] + "'" + lo[2] + "," + la[0] + "e" + la[1] + "'" + la[2]);
		System.out.println("pour nico Lardenne ");
		proj.calculeLambert2WGSInterne(xy, 523115.795, 1844521.217, 1E-11);
		lo = MathTools.radianVersDegre(xy[0]);
		la = MathTools.radianVersDegre(xy[1]);
		System.out.println("result = " + lo[0] + "e" + lo[1] + "'" + lo[2] + "," + la[0] + "e" + la[1] + "'" + la[2]);
		System.out.println("pour nico St Martin du TOUCH  ");
		proj.calculeLambert2WGSInterne(xy, 522066.955, 1844899.827, 1E-11);
		lo = MathTools.radianVersDegre(xy[0]);
		la = MathTools.radianVersDegre(xy[1]);
		System.out.println("result = " + lo[0] + "e" + lo[1] + "'" + lo[2] + "," + la[0] + "e" + la[1] + "'" + la[2]);
		System.out.println("pour nico Colomiers   ");
		proj.calculeLambert2WGSInterne(xy, 519045.258, 1845342.644, 1E-11);
		lo = MathTools.radianVersDegre(xy[0]);
		la = MathTools.radianVersDegre(xy[1]);
		System.out.println("result = " + lo[0] + "e" + lo[1] + "'" + lo[2] + "," + la[0] + "e" + la[1] + "'" + la[2]);

		// Tests d'intersection...

		Rectangle2D r1 = new Rectangle2D.Double(0, 0, 100, 100);
		Rectangle2D r2 = new Rectangle2D.Double(30, 20, 100, 100);
		if (r1.intersects(r2.getMinX(), r2.getMinY(), r2.getWidth(), r2.getHeight())) {
			System.out.println("intersection r1.r2 " + r1.createIntersection(r2));
		}

		TreeSet rectSetV = new TreeSet(new Rectangle2DComparator(true));
		TreeSet rectSetH = new TreeSet(new Rectangle2DComparator(false));
		for (int i = 0; i < 10; i++) {
			Rectangle2D r = new Rectangle2D.Double(Math.random() * 1000, Math.random() * 1000, 100, 100);
			rectSetV.add(r);
			rectSetH.add(r);

		}

		Iterator it = rectSetV.subSet(new Point2D.Double(400, 100), new Point2D.Double(700, 700)).iterator();
		Set s = rectSetH.subSet(new Point2D.Double(400, 100), new Point2D.Double(700, 700));
		while (it.hasNext()) {
			Object o = it.next();
			if (s.contains(o)) {
				System.out.println(o);
			}
		}

	}

	public double calculeLatitude(double liso, double tolerance) {
		double e = constants.getE();
		double pi2 = Math.PI / 2;
		double expL = Math.exp(liso);
		double phi = 2 * Math.atan(expL) - pi2;
		double oldphi = phi;
		do {
			oldphi = phi;
			phi = 2 * Math.atan(Math.pow((1 + e * Math.sin(oldphi)) / (1 - e * Math.sin(oldphi)), (e / 2)) * expL) - pi2;
		} while (Math.abs(oldphi - phi) >= tolerance);

		return phi;
	}

	public double[] calculeWGS2Lambert(double lo, double la) {
		double liso = GeoAlgo.calculeLatitudeIsometrique(e, la);
		double c_e_n_liso = C * Math.exp(-n * liso);
		double nlola = n * (lo - lambdaC);
		double x = Xs + c_e_n_liso * Math.sin(nlola);
		double y = Ys - c_e_n_liso * Math.cos(nlola);
		return new double[] { x, y };
	}

	public void calculeWGS2LambertInterne(double[] xy, double lo, double la) {
		double liso = GeoAlgo.calculeLatitudeIsometrique(e, la);
		double c_e_n_liso = C * Math.exp(-n * liso);
		double nlola = n * (lo - lambdaC);

		xy[0] = Xs + c_e_n_liso * Math.sin(nlola);
		xy[1] = Ys - c_e_n_liso * Math.cos(nlola);
	}

	public void calculeLambert2WGSInterne(double[] lola, double x, double y, double tolerance) {
		double dX = x - Xs;
		double dY = Ys - y;
		double R = Math.sqrt(dX * dX + dY * dY);
		double gamma = Math.atan(dX / dY);
		lola[0] = lambdaC + gamma / n;
		double liso = -Math.log(Math.abs(R / C)) / n;
		lola[1] = calculeLatitude(liso, tolerance);
	}

	static class Rectangle2DComparator implements Comparator {

		boolean vertical;

		Rectangle2DComparator(boolean v) {
			vertical = v;
		}

		/**
		 * @see java.util.Comparator#compare(Object, Object)
		 */
		@Override
		public int compare(Object arg0, Object arg1) {
			// System.out.println(arg0.getClass().getName());
			// System.out.println(arg1.getClass().getName());

			double v0;
			double v1;

			if (arg0 instanceof Point2D) {
				v0 = (vertical ? ((Point2D) arg0).getY() : ((Point2D) arg0).getX());
			} else {
				v0 = (vertical ? ((Rectangle2D) arg0).getMinY() : ((Rectangle2D) arg0).getMinX());
			}
			if (arg1 instanceof Point2D) {
				v1 = (vertical ? ((Point2D) arg1).getY() : ((Point2D) arg1).getX());
			} else {
				v1 = (vertical ? ((Rectangle2D) arg1).getMinY() : ((Rectangle2D) arg1).getMinX());
			}

			return (int) Math.round(v0 - v1);
		}

	}

}
