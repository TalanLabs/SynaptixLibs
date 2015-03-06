package uk.ac.leeds.ccg.shapefile;

import java.io.IOException;
import java.io.Serializable;

import cmp.LEDataStream.LEDataInputStream;
import cmp.LEDataStream.LEDataOutputStream;

/**
 * Wrapper for a Shapefile point. This now extends GeoPoint from the GeoTools
 * package. To use this class without requiering any of geotools un-coment the
 * marked lines and remove extends
 */
public class ShapePoint implements ShapefileShape, Serializable {
	protected double x, y; // un coment to remove GeoTools dependence

	public ShapePoint(LEDataInputStream file) throws IOException {
		file.setLittleEndianMode(true);
		int shapeType = file.readInt();
		x = file.readDouble();
		y = file.readDouble();
	}

	public void write(LEDataOutputStream file) throws IOException {
		file.setLittleEndianMode(true);
		file.writeInt(Shapefile.POINT);
		file.writeDouble(x);
		file.writeDouble(y);
	}

	/**
	 * Create a new point from x,y values
	 */
	public ShapePoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a new point from an existing one
	 * 
	 * @param p
	 *            The existing point
	 */
	public ShapePoint(ShapePoint p) {
		this.x = p.x;
		this.y = p.y;
	}

	/**
	 * Return the x value of this point.
	 * 
	 * @return The x value
	 */
	public double getX() {
		return x;
	}

	/**
	 * Return the y value of this point.
	 * 
	 * @return The y value
	 */
	public double getY() {
		return y;
	}

	/**
	 * Return this point as an array.
	 * 
	 * @return double[2] in the form {x,y}
	 */
	public double[] getPoint() {
		double[] d = { x, y };
		return d;
	}

	public String toString() {
		return (x + "," + y); //$NON-NLS-1$
	}

	/**
	 * Returns the shapefile shape type value for a point
	 * 
	 * @return int Shapefile.POINT
	 */
	public int getShapeType() {
		return Shapefile.POINT;
	}

	public int getLength() {
		return 10;// the length of two doubles in 16bit words + the shapeType
	}

}
