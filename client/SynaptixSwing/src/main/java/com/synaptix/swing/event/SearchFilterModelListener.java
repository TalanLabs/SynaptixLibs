package com.synaptix.swing.event;

import java.util.EventListener;

public interface SearchFilterModelListener extends EventListener {

	public void filterAdded(SearchFilterModelEvent e);

	public void filterRemoved(SearchFilterModelEvent e);

	public void filterMoved(SearchFilterModelEvent e);

}
