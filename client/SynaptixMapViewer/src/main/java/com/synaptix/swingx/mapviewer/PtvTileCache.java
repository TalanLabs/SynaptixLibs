package com.synaptix.swingx.mapviewer;

import java.net.URI;
import java.net.URLEncoder;

import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

public class PtvTileCache extends TileCache {

	private TileFactoryInfo tileFactoryInfo;

	public PtvTileCache(TileFactoryInfo tileFactoryInfo) {
		super(tileFactoryInfo);

		this.tileFactoryInfo = tileFactoryInfo;
	}

	@Override
	protected String getFilename(URI uri) throws Exception {
		String s = uri.toString();
		s = s.substring(tileFactoryInfo.getBaseURL().length());
		int i = s.indexOf("bbox");
		s = s.substring(i);
		return URLEncoder.encode(s, "UTF-8") + ".png";
	}
}
