package com.synaptix.local;

public interface ILocalMessage {
	
	public abstract String getMessage(String messageId);

	public abstract String getMessage(String messageId, Object... args);

}
