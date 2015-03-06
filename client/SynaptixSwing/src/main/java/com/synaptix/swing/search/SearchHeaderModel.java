package com.synaptix.swing.search;

import java.util.Map;

public interface SearchHeaderModel {

	public abstract int getFilterCount();

	public abstract Filter getFilter(int index);
	
	public abstract boolean validate(Map<String, Object> filters);
	
}
