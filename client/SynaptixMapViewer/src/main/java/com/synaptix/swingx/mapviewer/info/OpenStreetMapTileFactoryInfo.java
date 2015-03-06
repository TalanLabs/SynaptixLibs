package com.synaptix.swingx.mapviewer.info;

import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

public class OpenStreetMapTileFactoryInfo extends TileFactoryInfo {

	public final static int MAX_ZOOM_LEVEL = 16;

	public final static int MIN_ZOOM_LEVEL = 2;

	public final static String[] DEFAULT_PATHS = new String[] { "http://a.tile.openstreetmap.org/", "http://b.tile.openstreetmap.org/", "http://c.tile.openstreetmap.org/" };

	private String[] paths = new String[] { "http://a.tile.openstreetmap.org/", "http://b.tile.openstreetmap.org/", "http://c.tile.openstreetmap.org/" };

	public OpenStreetMapTileFactoryInfo() {
		this(MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL);
	}

	public OpenStreetMapTileFactoryInfo(int minimumZoomLevel, int maximumZoomLevel) {
		this(MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL, DEFAULT_PATHS);
	}

	public OpenStreetMapTileFactoryInfo(int minimumZoomLevel, int maximumZoomLevel, String... paths) {
		super(minimumZoomLevel, maximumZoomLevel, 20, 256, true, true, null, null, null, null, "png");

		setDefaultZoomLevel(maximumZoomLevel);
		this.paths = paths;
	}

	@Override
	public String getBaseURL() {
		return paths[0];
	}

	@Override
	public String getTileUrl(int x, int y, int zoom) {
		int z = getTotalMapZoom() - zoom;
		String t = new StringBuilder(paths[(x + y) % paths.length]).append(z).append("/").append(x) //$NON-NLS-1$
				.append("/").append(y).append(".png").toString(); //$NON-NLS-1$ //$NON-NLS-2$
		return t;
	}
}