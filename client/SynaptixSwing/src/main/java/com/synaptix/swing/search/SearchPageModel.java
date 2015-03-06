package com.synaptix.swing.search;

import java.util.Map;

public interface SearchPageModel extends SearchHeaderModel, SearchTableModel {

	public String getSaveName();

	public int getPageMaxCount();

	public int searchCount(Map<String, Object> filters) throws Exception;

	public Result search(Map<String, Object> filters, int skip, int nbLines)
			throws Exception;

}
