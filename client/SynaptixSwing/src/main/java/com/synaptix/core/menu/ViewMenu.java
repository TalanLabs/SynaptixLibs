package com.synaptix.core.menu;

public class ViewMenu implements IViewMenu {

	private String id;

	private String name;

	private String[] separators;

	private String path;

	private int position;

	public ViewMenu(String id, String name, String[] separators) {
		this(id, name, separators, null);
	}

	public ViewMenu(String id, String name, String[] separators, String path) {
		this(id, name, separators, path, 0);
	}

	public ViewMenu(String id, String name, String[] separators, String path,
			int position) {
		this.id = id;
		this.name = name;
		this.separators = separators;
		this.path = path;
		this.position = position;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String[] getSeparators() {
		return separators;
	}

	public String getPath() {
		return path;
	}
	
	public int getPosition() {
		return position;
	}
}
