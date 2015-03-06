import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.UUID;

import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.shp.ShapefileReader;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;

public class MainShapefile4 {

	/**
	 * @param args
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws Exception {
		ShpFiles shpFiles = new ShpFiles("D:/Apps/geoserver-2.1.4/data_dir/synaptix/monde/gadm1_lev0.shp");
		GeometryFactory gf = new GeometryFactory();
		ShapefileReader shapefileReader = new ShapefileReader(shpFiles, true, true, gf);
		DbaseFileReader dbaseFileReader = new DbaseFileReader(shpFiles, true, Charset.forName("UTF-8"));

		// DbaseFileHeader dbaseFileHeader = dbaseFileReader.getHeader();
		// for (int i = 0; i < dbaseFileHeader.getNumFields(); i++) {
		// System.out.println(i + " -> " + dbaseFileHeader.getFieldName(i));
		// }

		while (shapefileReader.hasNext()) {
			ShapefileReader.Record record = shapefileReader.nextRecord();
			DbaseFileReader.Row row = dbaseFileReader.readRow();

			String iso = (String) row.read(2);

			Geometry max = null;

			MultiPolygon mp = (MultiPolygon) record.shape();
			for (int i = 0; i < mp.getNumGeometries(); i++) {
				Geometry g = mp.getGeometryN(i);
				if (max == null || g.getArea() > max.getArea()) {
					max = g;
				}
			}

			System.out.println("insert into T_LOCAL_COUNTRY (ID, VERSION, COUNTRY, GEOPOINT_LONGITUDE, GEOPOINT_LATITUDE, CREATEDBY, CREATEDTIMESTAMP  ) values (hextoraw('" + randomGUID()
					+ "'), 0, '" + iso + "', " + max.getCentroid().getX() + ", " + max.getCentroid().getY() + ", 'GALLAIGRE', sysdate);");

		}

		shpFiles.dispose();
	}

	public static Serializable randomGUID() {
		UUID uuid = UUID.randomUUID();
		String rep = uuid.toString();
		return rep.replaceAll("-", "").toUpperCase();
	}
}
