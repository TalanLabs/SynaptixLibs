package com.synaptix.pmgr.trigger.listener;

public interface IMessageListener {

	public enum Status {
		ACCEPTED, REJECTED;
	}

	public void messageTreated(Status status);

}
