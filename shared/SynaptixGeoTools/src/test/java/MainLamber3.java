import java.util.Arrays;

import com.synaptix.geotools.MathTools;

public class MainLamber3 {

	public static double getN() {
		return 0.725607765;
	}

	public static double getC() {
		return 11754255.426;
	}

	public static double getXs() {
		return 700000;
	}

	public static double getYs() {
		return 12655612.05;
	}

	public static double[] getXsYs() {
		return new double[] { getXs(), getYs() };
	}

	public static double getLambdaC() {
		return MathTools.degreVersRadian(3);
	}

	public static double getE() {
		return 0.08181919106;
	}

	private static double calculeLatitude(double liso, double tolerance) {
		double e = getE();
		double pi2 = Math.PI / 2;
		double expL = Math.exp(liso);
		double phi = 2 * Math.atan(expL) - pi2;
		double oldphi = phi;
		do {
			oldphi = phi;
			phi = 2
					* Math.atan(Math.pow((1 + e * Math.sin(oldphi))
							/ (1 - e * Math.sin(oldphi)), (e / 2))
							* expL) - pi2;
		} while (Math.abs(oldphi - phi) >= tolerance);

		return phi;
	}

	public static void main(String[] args) {
		double e = getE();
		double n = getN();
		double Xs = getXs();
		double Ys = getYs();
		double C = getC();
		double lambdaC = getLambdaC();

		double y = 652998.313; // Lambert X
		double x = 6864360.819; // Lambert Y

		double[] result = new double[2]; // Result

		double dX = x - Xs;
		double dY = Ys - y;
		double R = Math.sqrt(dX * dX + dY * dY);
		double gamma = Math.atan(dX / dY);
		result[0] = (lambdaC + gamma / n) / Math.PI * 180; // Longitude
		double liso = -Math.log(Math.abs(R / C)) / n;
		result[1] = calculeLatitude(liso, 1E-11) / Math.PI * 180; // Latitude

		System.out.println(Arrays.toString(result));
	}

}
