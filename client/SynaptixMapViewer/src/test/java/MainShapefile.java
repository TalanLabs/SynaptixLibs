import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.util.GeoUtil;
import org.joda.time.DateTimeZone;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;

public class MainShapefile {

	/**
	 * @param args
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws Exception {
		int size = 512;

		ShpFiles shpFiles = new ShpFiles("D:/temp/world/tz_world.shp");

		GeometryFactory gf = new GeometryFactory();
		ShapefileReader shapefileReader = new ShapefileReader(shpFiles, true, true, gf);

		DbaseFileReader dbaseFileReader = new DbaseFileReader(shpFiles, true, Charset.forName("UTF-8"));

		MyTileFactoryInfo info = new MyTileFactoryInfo(size);

		Map<String, Area> map = new HashMap<String, Area>();

		while (shapefileReader.hasNext()) {
			ShapefileReader.Record record = shapefileReader.nextRecord();
			DbaseFileReader.Row row = dbaseFileReader.readRow();

			String tzid = (String) row.read(0);
			if (DateTimeZone.getAvailableIDs().contains(tzid)) {
				Path2D path = new Path2D.Double();
				boolean first = true;
				Point2D firstPoint = null;
				MultiPolygon mp = (MultiPolygon) record.shape();
				for (Coordinate c : mp.getCoordinates()) {
					Point2D p2d = GeoUtil.getBitmapCoordinate(c.y, c.x, 1, info);
					if (first) {
						firstPoint = p2d;
						path.moveTo(p2d.getX(), p2d.getY());
						first = false;
					} else {
						path.lineTo(p2d.getX(), p2d.getY());
					}
				}
				path.lineTo(firstPoint.getX(), firstPoint.getY());

				// Rectangle rect = path.getBounds();
				// if (rect.width > 1 && rect.height > 1) {
				Area gss = map.get(tzid);
				if (gss == null) {
					gss = new Area();
					map.put(tzid, gss);
				}

				gss.add(new Area(path));
				// }
			}

			System.out.println(tzid);
		}

		System.out.println("ici");

		File dir = new File("D:/temp/world/tz/");

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dir, "toto.tz")));

		Color[] backColors = { new Color(231, 232, 231), new Color(182, 207, 245), new Color(152, 215, 228), new Color(227, 215, 255), new Color(251, 211, 224), new Color(242, 178, 168),
				new Color(194, 194, 194), new Color(73, 134, 231), new Color(45, 162, 187), new Color(185, 154, 255), new Color(246, 145, 178), new Color(251, 76, 47), new Color(255, 200, 175),
				new Color(255, 222, 181), new Color(251, 233, 131), new Color(253, 237, 193), new Color(179, 239, 211), new Color(162, 220, 193), new Color(255, 117, 55), new Color(255, 173, 70),
				new Color(235, 219, 222), new Color(204, 166, 172), new Color(66, 214, 146), new Color(22, 167, 101) };

		BufferedImage image2 = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g22 = image2.createGraphics();

		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		int i = 0;
		for (Entry<String, Area> entry : map.entrySet()) {
			BufferedImage image3 = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g23 = image3.createGraphics();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g22.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g23.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Shape shape = entry.getValue();

			g2.setColor(new Color(i + 1));
			g2.fill(shape);

			g22.setColor(backColors[i % backColors.length]);
			g22.fill(shape);

			g23.setColor(Color.white);
			g23.fill(shape);

			g23.dispose();

			ImageIO.write(image, "png", new File(dir, URLEncoder.encode(entry.getKey(), "UTF-8") + ".png"));

			bw.write(entry.getKey() + "|" + (i + 1) + "\n");

			i++;
		}

		g2.dispose();

		bw.close();

		ImageIO.write(image, "png", new File(dir, "toto" + ".png"));

		g22.dispose();

		ImageIO.write(image2, "png", new File(dir, "final" + ".png"));

		shpFiles.dispose();
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
