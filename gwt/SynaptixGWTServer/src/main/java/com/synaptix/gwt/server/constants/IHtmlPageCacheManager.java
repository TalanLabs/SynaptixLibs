package com.synaptix.gwt.server.constants;

import java.io.File;
import java.util.Locale;

public interface IHtmlPageCacheManager {

	/**
	 * Create locale file with scrFile
	 * 
	 * @param locale
	 * @param srcFile
	 * @param servletPath
	 * @return
	 * @throws Exception
	 */
	public File getLocaleFile(Locale locale, File srcFile, String servletPath) throws Exception;

	/**
	 * Clear cache
	 */
	public void clearCache();
}
