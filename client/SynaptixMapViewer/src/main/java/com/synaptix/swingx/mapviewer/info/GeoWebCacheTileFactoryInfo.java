package com.synaptix.swingx.mapviewer.info;

import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

public class GeoWebCacheTileFactoryInfo extends TileFactoryInfo {

	public GeoWebCacheTileFactoryInfo(int minimumZoomLevel, int maximumZoomLevel, int totalMapZoom, int tileSize, String baseUrl) {
		super(minimumZoomLevel, maximumZoomLevel, totalMapZoom, tileSize, true, true, baseUrl, null, null, null, "png");
	}

	@Override
	public String getTileUrl(int x, int y, int zoom) {
		int z = getTotalMapZoom() - zoom;
		int p = (int) Math.pow(2, z);
		String t = new StringBuilder(baseURL).append(z).append("/").append(x).append("/").append(p - y - 1).append(".png").toString();
		return t;
	}
}
