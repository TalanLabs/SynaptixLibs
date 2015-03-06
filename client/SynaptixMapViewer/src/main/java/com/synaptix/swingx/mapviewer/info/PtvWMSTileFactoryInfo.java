package com.synaptix.swingx.mapviewer.info;

import java.awt.geom.Point2D;

import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.util.MercatorUtils;

public class PtvWMSTileFactoryInfo extends TileFactoryInfo {

	private String layer;

	private String realBaseUrl;

	public PtvWMSTileFactoryInfo(String baseUrl, String layer) {
		super(5, 16, 20, 256, true, true, baseUrl, null, null, null, "png");

		this.layer = layer;
		this.realBaseUrl = baseUrl;
	}

	@Override
	public String getTileUrl(int x, int y, int zoom) {
		int zz = getTotalMapZoom() - zoom;
		int z = (int) Math.pow(2, (double) zz - 1);
		String res = toWMSURL(x - z, z - 1 - y, zz, getTileSize(zoom));
		res = new StringBuilder(baseURL).append(res).toString();
		return res;
	}

	@Override
	public String getBaseURL() {
		return realBaseUrl;
	}

	private String toWMSURL(int x, int y, int zoom, int tileSize) {
		int ts = tileSize;
		int circumference = widthOfWorldInPixels(zoom, tileSize);
		double radius = circumference / (2 * Math.PI);
		double ulx = MercatorUtils.xToLong(x * ts, radius);
		double uly = MercatorUtils.yToLat(y * ts, radius);
		double lrx = MercatorUtils.xToLong((x + 1) * ts, radius);
		double lry = MercatorUtils.yToLat((y + 1) * ts, radius);

		Point2D p1 = convertESPG4326ToESPG505456(uly, ulx);
		Point2D p2 = convertESPG4326ToESPG505456(lry, lrx);

		StringBuilder sb = new StringBuilder();
		sb.append("?LAYERS=").append(layer);
		sb.append("&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&EXCEPTIONS=application/vnd.ogc.se_inimage&TRANSPARENT=true&FORMAT=image/png&SRS=EPSG:505456");
		sb.append("&width=").append(tileSize);
		sb.append("&height=").append(tileSize);
		sb.append("&bbox=").append(p1.getX()).append(",").append(p1.getY()).append(",").append(p2.getX()).append(",").append(p2.getY());
		return sb.toString();
	}

	private Point2D convertESPG4326ToESPG505456(double lat, double lon) {
		double RPI = Math.PI * 6371000.0;
		double x = lon * RPI / 180;
		double y = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
		y = y * RPI / 180;
		return new Point2D.Double(x, y);
	}

	private int widthOfWorldInPixels(int zoom, int TILE_SIZE) {
		// int TILE_SIZE = 256;
		int tiles = (int) Math.pow(2, zoom);
		int circumference = TILE_SIZE * tiles;
		return circumference;
	}
}
