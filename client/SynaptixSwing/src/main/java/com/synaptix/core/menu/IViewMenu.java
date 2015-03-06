package com.synaptix.core.menu;

public interface IViewMenu {

	public abstract String getId();

	public abstract String getName();

	public abstract String[] getSeparators();

	public abstract String getPath();

	public abstract int getPosition();

}