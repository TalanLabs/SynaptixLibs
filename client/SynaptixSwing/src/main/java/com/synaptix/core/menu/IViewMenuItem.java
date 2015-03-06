package com.synaptix.core.menu;

public interface IViewMenuItem {

	public abstract String getId();

	public abstract String getName();

	public abstract String getPath();

	public abstract IViewMenuItemAction getMenuItemAction();

}