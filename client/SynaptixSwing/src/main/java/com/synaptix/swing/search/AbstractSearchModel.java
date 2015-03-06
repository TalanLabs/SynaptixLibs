package com.synaptix.swing.search;

import java.util.Map;

import javax.swing.event.EventListenerList;

import com.synaptix.prefs.SyPreferences;
import com.synaptix.swing.SearchMessages;
import com.synaptix.swing.event.SearchModelEvent;
import com.synaptix.swing.event.SearchModelListener;

public abstract class AbstractSearchModel implements SearchModel {

	protected EventListenerList listenerList;

	public AbstractSearchModel() {
		listenerList = new EventListenerList();
	}

	public boolean isUseCount() {
		return true;
	}

	public int getMaxCount() {
		SyPreferences prefs = SyPreferences.getPreferences();
		String count = prefs.get("maxCountAbstractSearchModel", "500"); //$NON-NLS-1$ //$NON-NLS-2$
		return Integer.parseInt(count);
	}

	public void addSearchModelListener(SearchModelListener l) {
		listenerList.add(SearchModelListener.class, l);
	}

	public void removeSearchModelListener(SearchModelListener l) {
		listenerList.remove(SearchModelListener.class, l);
	}

	public SearchModelListener[] getSearchDataListeners() {
		return (SearchModelListener[]) listenerList
				.getListeners(SearchModelListener.class);
	}

	public boolean validate(Map<String, Object> filters) {
		return true;
	}
	
	protected void fireFiltersChanged(Object source) {
		SearchModelListener[] listeners = listenerList
				.getListeners(SearchModelListener.class);
		SearchModelEvent e = new SearchModelEvent(this,
				SearchModelEvent.Type.FILTER);

		for (SearchModelListener listener : listeners) {
			listener.searchChanged(e);
		}
	}
}
