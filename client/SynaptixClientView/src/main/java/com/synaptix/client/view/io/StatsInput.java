package com.synaptix.client.view.io;

public interface StatsInput {

	public abstract Double readDouble(String id);

	public abstract Float readFloat(String id);

	public abstract Integer readInteger(String id);

	public abstract Long readLong(String id);

	public abstract Byte readByte(String id);

	public abstract Boolean readBoolean(String id);

	public abstract String readString(String id);

	public abstract Object readObject(String id);

}
