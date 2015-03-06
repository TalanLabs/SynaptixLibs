package com.synaptix.local;

import java.util.Properties;

public interface ILocalMessage {
	
	public abstract String getMessage(String messageId);

	public abstract String getMessage(String messageId, Object... args);

}
