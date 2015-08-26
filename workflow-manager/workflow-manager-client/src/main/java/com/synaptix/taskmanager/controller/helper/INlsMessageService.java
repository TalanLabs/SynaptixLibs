package com.synaptix.taskmanager.controller.helper;

import java.util.List;
import java.util.Locale;

import com.synaptix.component.IComponent;


public interface INlsMessageService {

	/**
	 * Export a nls message for ComponentClass
	 * 
	 * @param componentClass
	 * @param locale
	 * @return
	 */
	public <E extends IComponent> List<INlsMessageData> exportNlsMessages(Class<E> componentClass, Locale locale);

	/**
	 * Import a nls message for component class
	 * 
	 * @param componentClass
	 * @param locale
	 * @param nlsMessageDatas
	 * @return
	 */
	public <E extends IComponent> int importNlsMessages(Class<E> componentClass, Locale locale, List<INlsMessageData> nlsMessageDatas);

}
