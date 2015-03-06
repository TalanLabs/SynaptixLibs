import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;

import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.util.GeoUtil;
import org.joda.time.DateTimeZone;

import com.synaptix.swingx.mapviewer.util.Vec2;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;

public class MainShapefile3 {

	/**
	 * @param args
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws Exception {
		int size = 2048 * 4;
		double distanceMin = 1;

		ShpFiles shpFiles = new ShpFiles("D:/temp/world/tz_world.shp");
		GeometryFactory gf = new GeometryFactory();
		ShapefileReader shapefileReader = new ShapefileReader(shpFiles, true, true, gf);
		DbaseFileReader dbaseFileReader = new DbaseFileReader(shpFiles, true, Charset.forName("UTF-8"));

		MyTileFactoryInfo info = new MyTileFactoryInfo(size);

		Set<String> regions = new HashSet<String>();

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
				int i = tzid.indexOf("/");
				if (i >= 0) {
					regions.add(tzid.substring(0, i));
				}

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

		System.out.println(regions);

		for (String region : regions) {
			saveImage(region, shapesMap, size, 512);
		}

		shpFiles.dispose();
	}

	private static void saveImage(String region, Map<String, List<Shape>> shapesMap, int srcSize, int destSize) throws Exception {
		System.out.println("saveImage");

		File dir = new File("D:/temp/world/tz/");

		Color[] backColors = { new Color(231, 232, 231), new Color(182, 207, 245), new Color(152, 215, 228), new Color(227, 215, 255), new Color(251, 211, 224), new Color(242, 178, 168),
				new Color(194, 194, 194), new Color(73, 134, 231), new Color(45, 162, 187), new Color(185, 154, 255), new Color(246, 145, 178), new Color(251, 76, 47), new Color(255, 200, 175),
				new Color(255, 222, 181), new Color(251, 233, 131), new Color(253, 237, 193), new Color(179, 239, 211), new Color(162, 220, 193), new Color(255, 117, 55), new Color(255, 173, 70),
				new Color(235, 219, 222), new Color(204, 166, 172), new Color(66, 214, 146), new Color(22, 167, 101) };

		BufferedImage finalImage = new BufferedImage(destSize, destSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D finalG2 = finalImage.createGraphics();
		finalG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		finalG2.scale(destSize / srcSize, destSize / srcSize);

		int i = 0;
		for (Entry<String, List<Shape>> entry : shapesMap.entrySet()) {
			if (entry.getKey().startsWith(region)) {
				finalG2.setColor(backColors[i % backColors.length]);
				for (Shape shape : entry.getValue()) {
					finalG2.fill(shape);
				}
				i++;
			}
		}

		ImageIO.write(finalImage, "png", new File(dir, region + ".png"));
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
