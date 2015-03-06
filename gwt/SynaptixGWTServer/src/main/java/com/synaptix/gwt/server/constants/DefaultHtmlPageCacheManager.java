package com.synaptix.gwt.server.constants;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;

public class DefaultHtmlPageCacheManager implements IHtmlPageCacheManager {

	private static final Log LOG = LogFactory.getLog(DefaultHtmlPageCacheManager.class);

	@Inject
	private IHtmlPageLocaleConverter htmlPageLocaleConverter;

	private Map<String, Integer> lockMap = new HashMap<String, Integer>();

	private long lastClear = System.currentTimeMillis();

	@Inject
	public DefaultHtmlPageCacheManager() {
		super();
	}

	@Override
	public File getLocaleFile(Locale locale, File srcFile, String servletPath) throws Exception {
		String dir = System.getProperty("java.io.tmpdir", "");
		int lastIndex = servletPath.lastIndexOf(".");
		String extension = lastIndex >= 0 ? servletPath.substring(lastIndex) : "";

		String name = new StringBuilder(URLEncoder.encode(servletPath, "UTF-8")).append("_").append(locale.toString()).append(extension).toString();
		String path = new StringBuilder(dir).append(name).toString();
		Integer lock;
		synchronized (lockMap) {
			lock = lockMap.get(path);
			if (lock == null) {
				lock = new Integer(0);
				lockMap.put(path, lock);
			}
		}
		File dstFile;
		synchronized (lock) {
			dstFile = new File(dir, name);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Get locale file=" + dstFile.getAbsolutePath());
			}
			boolean deleted = false;
			if (dstFile.lastModified() < lastClear) {
				dstFile.delete();
				deleted = true;
			}
			if (deleted || !dstFile.exists()) {
				LOG.debug("Create locale file=" + srcFile.getAbsolutePath() + " for " + locale + " where " + dstFile.getAbsolutePath());
				if (!dstFile.exists()) {
					dstFile.createNewFile();
				}

				htmlPageLocaleConverter.convert(locale, srcFile, dstFile);
			}
		}
		return dstFile;
	}

	@Override
	public void clearCache() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("clearCache");
		}
		synchronized (lockMap) {
			lastClear = System.currentTimeMillis();
		}
	}
}
