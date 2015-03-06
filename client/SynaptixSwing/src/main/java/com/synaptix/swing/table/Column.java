package com.synaptix.swing.table;

public interface Column {

	public String getId();

	public String getName();

	public Class<?> getColumnClass();

	public boolean isDefaultVisible();

	public boolean isLock();

	public boolean isSearchable();

	public boolean isSortable();

	public boolean isEditable();

	public boolean isExistSumValue();

	public Class<?> getSumClass();

}
