import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.shp.ShapeType;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileWriter;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.util.GeoUtil;
import org.joda.time.DateTimeZone;

import com.synaptix.swingx.mapviewer.util.Vec2;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class MainShapefile2 {

	/**
	 * @param args
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws Exception {
		int size = 2048;
		double distanceMin = 1;

		ShpFiles shpFiles = new ShpFiles("D:/temp/world/tz_world.shp");
		GeometryFactory gf = new GeometryFactory();
		ShapefileReader shapefileReader = new ShapefileReader(shpFiles, true, true, gf);
		DbaseFileReader dbaseFileReader = new DbaseFileReader(shpFiles, true, Charset.forName("UTF-8"));

		MyTileFactoryInfo info = new MyTileFactoryInfo(size);

		Map<String, List<Shape>> shapesMap = new HashMap<String, List<Shape>>();
		Map<String, List<List<Coordinate>>> map2 = new HashMap<String, List<List<Coordinate>>>();

		int totalPoint = 0;
		int totalPoly = 0;
		int totalNewPoly = 0;
		int totalNewPoint = 0;

		while (shapefileReader.hasNext()) {
			ShapefileReader.Record record = shapefileReader.nextRecord();
			DbaseFileReader.Row row = dbaseFileReader.readRow();

			String tzid = (String) row.read(0);
			if (DateTimeZone.getAvailableIDs().contains(tzid)) {
				Path2D path = new Path2D.Double();
				List<Coordinate> gps = new ArrayList<Coordinate>();
				boolean first = true;
				Point2D firstPoint = null;
				Vec2 lastVec = null;
				Vec2 vec = null;
				double distance = 0;
				int nbNewPoint = 0;
				MultiPolygon mp = (MultiPolygon) record.shape();
				int num = 0;
				int len = mp.getCoordinates().length;
				for (Coordinate c : mp.getCoordinates()) {
					Point2D p2d = GeoUtil.getBitmapCoordinate(c.y, c.x, 1, info);
					if (first) {
						firstPoint = p2d;
						lastVec = new Vec2(p2d.getX(), p2d.getY());
						path.moveTo(p2d.getX(), p2d.getY());
						first = false;
						nbNewPoint++;
						gps.add(c);
					} else {
						vec = new Vec2(p2d.getX(), p2d.getY());

						distance += vec.distanceTo2(lastVec);
						if (distance > distanceMin) {
							path.lineTo(p2d.getX(), p2d.getY());
							distance = 0;
							nbNewPoint++;
							gps.add(c);
						} else if (num == len - 1) {
							gps.add(c);
						}
						lastVec = vec;

						gps.add(c);
					}
					num++;
				}
				path.lineTo(firstPoint.getX(), firstPoint.getY());

				totalPoly++;
				totalPoint += mp.getCoordinates().length;

				// Rectangle rect = path.getBounds();
				// if (rect.width > 1 && rect.height > 1) {
				totalNewPoly++;
				totalNewPoint += nbNewPoint;
				List<Shape> shapes = shapesMap.get(tzid);
				if (shapes == null) {
					shapes = new ArrayList<Shape>();
					shapesMap.put(tzid, shapes);
				}
				shapes.add(path);

				List<List<Coordinate>> gss = map2.get(tzid);
				if (gss == null) {
					gss = new ArrayList<List<Coordinate>>();
					map2.put(tzid, gss);
				}
				gss.add(gps);
				// }
			}
		}

		System.out.println(totalPoly + " " + totalPoint + " => " + totalNewPoly + " " + totalNewPoint);

		shpFiles.dispose();

		// saveImage(size, shapesMap);

		// saveShapeFile(map2);

		saveShapes(shapesMap);
	}

	private static void saveShapeFile(Map<String, List<List<Coordinate>>> map2) throws Exception {
		System.out.println("saveShapeFile");

		GeometryFactory gf = new GeometryFactory();

		List<Geometry> ps = new ArrayList<Geometry>();
		for (Entry<String, List<List<Coordinate>>> entry : map2.entrySet()) {
			for (List<Coordinate> gs : entry.getValue()) {
				// if (gs.size() >= 4) {
				System.out.println(gs.size());
				LinearRing lr = gf.createLinearRing(gs.toArray(new Coordinate[gs.size()]));
				Polygon p = gf.createPolygon(lr, null);
				MultiPolygon mp = gf.createMultiPolygon(new Polygon[] { p });
				ps.add(mp);
				// }
			}
		}

		GeometryCollection gc = new GeometryCollection(ps.toArray(new Geometry[ps.size()]), gf);

		File dirFile = new File("D:/temp/world/tz/");
		FileOutputStream shp = new FileOutputStream(new File(dirFile, "myshape.shp"));
		FileOutputStream shx = new FileOutputStream(new File(dirFile, "myshape.shx"));
		ShapefileWriter sfw = new ShapefileWriter(shp.getChannel(), shx.getChannel());
		sfw.write(gc, ShapeType.POLYGON);
		sfw.close();
	}

	private static void saveShapes(Map<String, List<Shape>> shapesMap) throws Exception {
		File dir = new File("D:/temp/world/tz/");

		OutputStream out = new GZIPOutputStream(new FileOutputStream(new File(dir, "toto.gab")));

		double[] coords = new double[2];

		for (Entry<String, List<Shape>> entry : shapesMap.entrySet()) {
			byte[] bytes = entry.getKey().getBytes("UTF-8");
			out.write(shortInBytes((short) bytes.length));
			out.write(bytes);
			out.write(shortInBytes((short) entry.getValue().size()));
			for (Shape shape : entry.getValue()) {
				out.write(shortInBytes((short) nb(shape)));

				PathIterator pi = shape.getPathIterator(new AffineTransform());
				while (!pi.isDone()) {
					pi.currentSegment(coords);

					out.write(shortInBytes((short) coords[0]));
					out.write(shortInBytes((short) coords[1]));

					pi.next();
				}
			}
		}

		out.close();
	}

	private static void saveShapes2(Map<String, List<Shape>> shapesMap) throws Exception {
		File dir = new File("D:/temp/world/tz/");

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(new File(dir, "toto.gab")))));

		double[] coords = new double[2];

		for (Entry<String, List<Shape>> entry : shapesMap.entrySet()) {
			out.write(entry.getKey());
			out.write(" ");
			out.write(String.valueOf(entry.getValue().size()));
			out.write(" ");
			for (Shape shape : entry.getValue()) {
				out.write(String.valueOf(nb(shape)));
				out.write(" ");
				PathIterator pi = shape.getPathIterator(new AffineTransform());
				while (!pi.isDone()) {
					pi.currentSegment(coords);

					out.write(String.valueOf((int) coords[0]));
					out.write(" ");
					out.write(String.valueOf((int) coords[1]));
					out.write(" ");

					pi.next();
				}
			}
			out.write("\n");
		}

		out.close();
	}

	private static final int nb(Shape shape) {
		int res = 0;
		PathIterator pi = shape.getPathIterator(new AffineTransform());
		while (!pi.isDone()) {
			res++;
			pi.next();
		}
		return res;
	}

	private static final byte[] doubleInBytes(double value) {
		byte[] bytes = new byte[4];
		int i = (int) Math.floor(value * Math.pow(10, 5));
		bytes[0] = (byte) (i >>> 24);
		bytes[1] = (byte) (i >> 16 & 0xff);
		bytes[2] = (byte) (i >> 8 & 0xff);
		bytes[3] = (byte) (i & 0xff);
		return bytes;
	}

	private static final byte[] intInBytes(int value) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (value >>> 24);
		bytes[1] = (byte) (value >> 16 & 0xff);
		bytes[2] = (byte) (value >> 8 & 0xff);
		bytes[3] = (byte) (value & 0xff);
		return bytes;
	}

	private static final byte[] shortInBytes(short value) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (value >> 8 & 0xff);
		bytes[1] = (byte) (value & 0xff);
		return bytes;
	}

	private static void saveImage(int size, Map<String, List<Shape>> shapesMap) throws Exception {
		System.out.println("saveImage");

		File dir = new File("D:/temp/world/tz/");

		Color[] backColors = { new Color(231, 232, 231), new Color(182, 207, 245), new Color(152, 215, 228), new Color(227, 215, 255), new Color(251, 211, 224), new Color(242, 178, 168),
				new Color(194, 194, 194), new Color(73, 134, 231), new Color(45, 162, 187), new Color(185, 154, 255), new Color(246, 145, 178), new Color(251, 76, 47), new Color(255, 200, 175),
				new Color(255, 222, 181), new Color(251, 233, 131), new Color(253, 237, 193), new Color(179, 239, 211), new Color(162, 220, 193), new Color(255, 117, 55), new Color(255, 173, 70),
				new Color(235, 219, 222), new Color(204, 166, 172), new Color(66, 214, 146), new Color(22, 167, 101) };

		BufferedImage finalImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D finalG2 = finalImage.createGraphics();

		int i = 0;
		for (Entry<String, List<Shape>> entry : shapesMap.entrySet()) {
			finalG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			finalG2.setColor(backColors[i % backColors.length]);
			for (Shape shape : entry.getValue()) {
				finalG2.fill(shape);
			}

			i++;
		}

		ImageIO.write(finalImage, "png", new File(dir, "final" + ".png"));
	}

	private static class MyTileFactoryInfo extends TileFactoryInfo {

		public MyTileFactoryInfo(int size) {
			super(1, 1, 1, size, true, true, "", null, null, null, "png");
		}

		@Override
		public String getTileUrl(int x, int y, int zoom) {
			int z = getTotalMapZoom() - zoom;
			int p = (int) Math.pow(2, z);
			String t = new StringBuilder(baseURL).append(z).append("/").append(x).append("/").append(p - y - 1).append(".png").toString();
			return t;
		}
	}
}
