package com.synaptix.swing.search;

import java.util.Map;

public abstract class AbstractSearchHeaderModel implements SearchHeaderModel {

	public boolean validate(Map<String, Object> filters) {
		return true;
	}
	
}
