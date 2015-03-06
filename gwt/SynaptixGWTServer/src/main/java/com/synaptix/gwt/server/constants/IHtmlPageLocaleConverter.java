package com.synaptix.gwt.server.constants;

import java.io.File;
import java.util.Locale;

public interface IHtmlPageLocaleConverter {

	/**
	 * Convert a html page to locale html page
	 * 
	 * @param locale
	 * @param srcFile
	 * @param dstFile
	 * @throws Exception
	 */
	public void convert(Locale locale, File srcFile, File dstFile) throws Exception;

}
