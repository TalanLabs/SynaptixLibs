package com.synaptix.swing.search;

import java.util.Map;

public abstract class AbstractSearchMoteurContext implements
		ISearchMoteurContext {

	public int getMaxCount() {
		return 500;
	}

	public int getPageMaxCount() {
		return 50;
	}

	public Result search(Map<String, Object> filters) throws Exception {
		return null;
	}

	public Result search(Map<String, Object> filters, int skip, int nbLines)
			throws Exception {
		return null;
	}
}
