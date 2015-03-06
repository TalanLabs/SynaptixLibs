package com.synaptix.server.service.extension.properties;

public interface IPropertiesFactory {

	public abstract Object get(String name);

	public abstract String getString(String name, String def);

	public abstract boolean getBoolean(String name, boolean def);

	public abstract int getInt(String name, int def);

}
