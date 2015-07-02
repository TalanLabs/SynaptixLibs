package com.synaptix.pmgr.trigger.message;

import com.synaptix.pmgr.trigger.listener.IMessageListener;

public interface IFlux {

	public String getName();

	public String getFilename();

	/**
	 * Add a listener to detect the end of the treatment of a message
	 */
	public void addMessageListener(IMessageListener messageListener);

	/**
	 * Add a listener to detect the end of the treatment of a message, given a priority
	 */
	public void addMessageListener(int index, IMessageListener messageListener);

	public void fireMessageListener(IMessageListener.Status status);

}
