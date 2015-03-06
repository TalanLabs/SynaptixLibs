package com.synaptix.tmgr.apis;

public interface TriggerEvent {
	public long getTimeStamp();
	public Trigger getSource();
	public Object getAttachment();
}
