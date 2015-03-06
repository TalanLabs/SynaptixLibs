package com.synaptix.swing.table;

public class SimpleColumn implements Column {

	private Class<?> columnClass;

	private String id;

	private String name;

	private boolean defaultVisible;

	private boolean searchable;

	private boolean lock;

	private boolean sortable;

	private Class<?> sumClass;

	private boolean existSumValue;

	private boolean editable;

	public SimpleColumn(String name) {
		this(name, name);
	}

	public SimpleColumn(String id, String name) {
		this(id, name, Object.class);
	}

	public SimpleColumn(String id, String name, boolean defaultVisible) {
		this(id, name, Object.class, defaultVisible);
	}

	public SimpleColumn(String id, String name, Class<?> c) {
		this(id, name, c, true);
	}

	public SimpleColumn(String id, String name, Class<?> c, boolean defaultVisible) {
		this(id, name, c, defaultVisible, false, true, true);
	}

	public SimpleColumn(String id, String name, Class<?> c, boolean defaultVisible, boolean lock, boolean searchable, boolean sortable) {
		this(id, name, c, defaultVisible, lock, searchable, sortable, false, null);
	}

	public SimpleColumn(String id, String name, Class<?> c, boolean defaultVisible, boolean lock, boolean searchable, boolean sortable, boolean editable) {
		this(id, name, c, defaultVisible, lock, searchable, sortable, editable, null);
	}

	public SimpleColumn(String id, String name, Class<?> c, boolean defaultVisible, boolean lock, boolean searchable, boolean sortable, boolean editable, Class<?> sumClass) {
		super();
		this.id = id;
		this.name = name;
		this.columnClass = c;
		this.defaultVisible = defaultVisible;
		this.lock = lock;
		this.searchable = searchable;
		this.sortable = sortable;
		this.editable = editable;
		this.sumClass = sumClass;
		this.existSumValue = sumClass != null;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isDefaultVisible() {
		return defaultVisible;
	}

	@Override
	public boolean isLock() {
		return lock;
	}

	@Override
	public Class<?> getColumnClass() {
		return columnClass;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isSearchable() {
		return searchable;
	}

	@Override
	public boolean isSortable() {
		return sortable;
	}

	@Override
	public boolean isEditable() {
		return editable;
	}

	@Override
	public Class<?> getSumClass() {
		return sumClass;
	}

	@Override
	public boolean isExistSumValue() {
		return existSumValue;
	}
}
