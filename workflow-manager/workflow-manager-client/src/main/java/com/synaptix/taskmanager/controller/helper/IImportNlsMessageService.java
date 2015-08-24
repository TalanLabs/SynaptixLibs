package com.synaptix.taskmanager.controller.helper;

import java.util.List;
import java.util.Locale;

public interface IImportNlsMessageService {

	/**
	 * Import a nls message for component class
	 * 
	 * @param locale
	 * @param nlsMessageDatas
	 * @return
	 */
	public int importNlsMessages(Locale locale, List<INlsMessageData> nlsMessageDatas);

}
