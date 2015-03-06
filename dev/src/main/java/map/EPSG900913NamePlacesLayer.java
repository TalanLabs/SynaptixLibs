package map;

import java.awt.geom.Point2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

public class EPSG900913NamePlacesLayer extends AbstractNamePlacesLayer {

	public EPSG900913NamePlacesLayer(JXMapViewer mapViewer, String baseUrl, String layer) {
		super(mapViewer, baseUrl, layer);
	}

	@Override
	protected Point2D convertGeoPositiontoPoint2D(GeoPosition gp) {
		double x = 6378137.0 * Math.PI / 180 * gp.getLongitude();
		double y = 6378137.0 * Math.log(Math.tan(Math.PI / 180 * (45 + gp.getLatitude() / 2.0)));
		return new Point2D.Double(x, y);
	}

	@Override
	protected String getSRS() {
		return "EPSG:900913";
	}
}
