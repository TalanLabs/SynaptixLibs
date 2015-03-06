package com.synaptix.swingx.mapviewer.info;

import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

public class MicrosoftVirtualEarthTileFactoryInfo extends TileFactoryInfo {

	public enum Type {
		Map, Satellite, MapAndSatellite
	};

	private final static int TOP_ZOOM_LEVEL = 19;

	public final static int MAX_ZOOM_LEVEL = 17;

	public final static int MIN_ZOOM_LEVEL = 2;

	private final static int TILE_SIZE = 256;

	private final static MVEMode MAP = new MVEMode("map", "map", "r", "png");

	private final static MVEMode SATELLITE = new MVEMode("satellite",
			"satellite", "a", "jpeg");

	private final static MVEMode SATELLITE_AND_MAP = new MVEMode(
			"satellite+map", "satellite+map", "h", "jpeg");

	private MVEMode mode = SATELLITE;

	public MicrosoftVirtualEarthTileFactoryInfo() {
		this(Type.Satellite);
	}

	public MicrosoftVirtualEarthTileFactoryInfo(Type type) {
		this(MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL, type);
	}

	public MicrosoftVirtualEarthTileFactoryInfo(int minimumZoomLevel,
			int maximumZoomLevel, Type type) {
		super(minimumZoomLevel, maximumZoomLevel, TOP_ZOOM_LEVEL, TILE_SIZE,
				true, true, null, null, null, null, null);

		switch (type) {
		case Map:
			mode = MAP;
			break;
		case Satellite:
			mode = SATELLITE;
			break;
		case MapAndSatellite:
			mode = SATELLITE_AND_MAP;
			break;
		default:
			mode = SATELLITE;
			break;
		}

		setDefaultZoomLevel(maximumZoomLevel);
	}

	@Override
	public String getBaseURL() {
		return "MicrosoftVirtualEarth/" + mode.name;
	}

	@Override
	public String getFormatImage() {
		return mode.ext;
	}

	@Override
	public String getTileUrl(int x, int y, int zoom) {
		final String quad = tileToQuadKey(x, y, getTotalMapZoom() - 0 - zoom);
		return "http://" + mode.type + quad.charAt(quad.length() - 1)
				+ ".ortho.tiles.virtualearth.net/tiles/" + mode.type + quad
				+ "." + mode.ext + "?g=1";
	}

	private String tileToQuadKey(final int tx, final int ty, final int zl) {
		String quad = "";

		for (int i = zl; i > 0; i--) {
			int mask = 1 << (i - 1);
			int cell = 0;

			if ((tx & mask) != 0) {
				cell++;
			}

			if ((ty & mask) != 0) {
				cell += 2;
			}

			quad += cell;
		}

		return quad;
	}

	private static class MVEMode {

		protected String name;

		protected String type;

		protected String ext;

		MVEMode(final String name, final String displayName, final String type,
				final String ext) {
			super();
			this.name = name;
			this.type = type;
			this.ext = ext;
		}
	}
}
