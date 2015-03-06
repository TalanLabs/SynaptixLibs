package com.synaptix.swing.search;

import java.util.Enumeration;
import java.util.List;

import com.synaptix.swing.event.SearchFilterModelListener;

public interface SearchFilterModel {

	public void addFilter(SearchFilter aFilter);

	public void removeFilter(SearchFilter aFilter);

	public void moveFilter(int filterIndex, int newIndex);

	public void visibleFilter(SearchFilter aFilter);

	public void invisibleFilter(SearchFilter aFilter);

	public SearchFilter getFilter(int filterIndex, boolean includeHidden);

	public List<SearchFilter> getFilters(boolean includeHidden, boolean initial);

	public Enumeration<SearchFilter> getFilters();
	
	public int getFilterCount();
	
	public int getFilterCount(boolean includeHidden);

	public int getFilterIndex(Object filterIdentifier, boolean includeHidden);

	public SearchFilter getFilter(int filterIndex);
	
	public void defaultFilters();

	public void save();

	public boolean isSave();

	public void load();

	public int getFilterIndexAtX(int xPosition);

	public int getTotalFilterWidth();

	public void addFilterModelListener(SearchFilterModelListener x);

	public void removeFilterModelListener(SearchFilterModelListener x);
}
