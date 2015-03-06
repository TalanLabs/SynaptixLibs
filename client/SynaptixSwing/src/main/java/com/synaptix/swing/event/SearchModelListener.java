package com.synaptix.swing.event;

import java.util.EventListener;

public interface SearchModelListener extends EventListener {

	public void searchChanged(SearchModelEvent e);
	
}
