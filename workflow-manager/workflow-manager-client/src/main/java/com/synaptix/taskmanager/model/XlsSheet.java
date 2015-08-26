package com.synaptix.taskmanager.model;

public class XlsSheet {

	private int index;

	private String name;

	public XlsSheet(int index, String name) {
		super();
		this.index = index;
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}
}
