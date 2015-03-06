package com.synaptix.core.menu;

public class ViewMenuItem implements IViewMenuItem {

	private String id;

	private String name;

	private String path;

	private IViewMenuItemAction menuItemAction;

	public ViewMenuItem(String id, String name, String path,
			IViewMenuItemAction menuItemAction) {
		this.id = id;
		this.name = name;
		this.path = path;
		this.menuItemAction = menuItemAction;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public IViewMenuItemAction getMenuItemAction() {
		return menuItemAction;
	}
}
