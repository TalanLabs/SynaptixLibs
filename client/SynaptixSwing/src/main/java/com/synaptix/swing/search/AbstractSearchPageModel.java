package com.synaptix.swing.search;

import java.util.Map;

public abstract class AbstractSearchPageModel implements SearchPageModel {

	public int getPageMaxCount() {
		return 50;
	}

	public boolean validate(Map<String, Object> filters) {
		return true;
	}
	
}
