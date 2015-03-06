package com.synaptix.swingx.mapviewer.info;

import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

public class PtvLoxaneGetMapTileFactoryInfo extends TileFactoryInfo {

	public PtvLoxaneGetMapTileFactoryInfo(String baseUrl) {
		super(2, 16, 20, 256, true, true, baseUrl, null, null, null, "png");

		setDefaultZoomLevel(16);
	}

	@Override
	public String getTileUrl(int x, int y, int zoom) {
		int z = getTotalMapZoom() - zoom;
		String t = new StringBuilder(getBaseURL()).append(x).append("/").append(y) //$NON-NLS-1$
				.append("/").append(z).append(".png").toString(); //$NON-NLS-1$ //$NON-NLS-2$
		return t;
	}
}
