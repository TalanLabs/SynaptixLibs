package com.synaptix.client.view.io;

public interface StatsOutput {

	public abstract void writeDouble(String id, Double value);
	
	public abstract void writeFloat(String id, Float value);
	
	public abstract void writeInteger(String id, Integer value);
	
	public abstract void writeLong(String id, Long value);
	
	public abstract void writeByte(String id, Byte value);
	
	public abstract void writeBoolean(String id, Boolean value);
	
	public abstract void writeString(String id, String value);
	
	public abstract void writeObject(String id, Object value);

}
