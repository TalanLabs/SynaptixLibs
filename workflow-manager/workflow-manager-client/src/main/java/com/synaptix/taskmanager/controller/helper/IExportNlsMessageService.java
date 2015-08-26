package com.synaptix.taskmanager.controller.helper;

import java.util.List;
import java.util.Locale;

public interface IExportNlsMessageService {

	/**
	 * Export a nls message for ComponentClass
	 * 
	 * @param locale
	 * @return
	 */
	public List<INlsMessageData> exportNlsMessages(Locale locale);

}
