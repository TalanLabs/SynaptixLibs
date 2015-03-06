package com.synaptix.swing.search;

import java.util.Map;

public interface ISearchMoteurContext {

	public abstract int getMaxCount();

	public abstract int getPageMaxCount();
	
	public abstract int searchCount(Map<String, Object> filters)
			throws Exception;

	public abstract Result search(Map<String, Object> filters) throws Exception;

	public abstract Result search(Map<String, Object> filters, int skip, int nbLines)
			throws Exception;

}
