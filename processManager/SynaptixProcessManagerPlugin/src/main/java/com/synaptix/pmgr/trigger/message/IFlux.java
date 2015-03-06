package com.synaptix.pmgr.trigger.message;

import com.synaptix.pmgr.trigger.listener.IMessageListener;

public interface IFlux {

	public String getName();

	public String getFilename();

	public void addMessageListener(IMessageListener messageListener);

	public void fireMessageListener(IMessageListener.Status status);

}
