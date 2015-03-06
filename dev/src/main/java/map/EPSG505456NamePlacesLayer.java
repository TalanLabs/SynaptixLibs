package map;

import java.awt.geom.Point2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

public class EPSG505456NamePlacesLayer extends AbstractNamePlacesLayer {

	public EPSG505456NamePlacesLayer(JXMapViewer mapViewer, String baseUrl, String layer) {
		super(mapViewer, baseUrl, layer);
	}

	@Override
	protected Point2D convertGeoPositiontoPoint2D(GeoPosition gp) {
		double RPI = Math.PI * 6371000.0;
		double x = gp.getLongitude() * RPI / 180;
		double y = Math.log(Math.tan((90 + gp.getLatitude()) * Math.PI / 360)) / (Math.PI / 180);
		y = y * RPI / 180;
		return new Point2D.Double(x, y);
	}

	@Override
	protected String getSRS() {
		return "EPSG:505456";
	}
}