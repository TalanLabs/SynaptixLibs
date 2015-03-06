package com.synaptix.widget.component.util;

import java.util.EventListener;
import java.util.Map;

/**
 * A listened to use to be notified when a new search is performed
 * 
 * @author Nicolas P
 * 
 */
public interface SearchListener extends EventListener {

	/**
	 * Invoked when a search has been done
	 * 
	 * @param valueFilterMap
	 */
	public void searchPerformed(Map<String, Object> valueFilterMap);

}
