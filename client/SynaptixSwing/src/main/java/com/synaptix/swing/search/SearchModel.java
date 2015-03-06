package com.synaptix.swing.search;

import java.util.Map;

import com.synaptix.swing.event.SearchModelListener;

public interface SearchModel extends SearchHeaderModel, SearchTableModel {

	public String getSaveName();

	public boolean isUseCount();

	public int getMaxCount();

	public int searchCount(Map<String, Object> filters) throws Exception;

	public Result search(Map<String, Object> filters) throws Exception;

	public void addSearchModelListener(SearchModelListener x);

	public void removeSearchModelListener(SearchModelListener x);

}
