package com.synaptix.pmgr.core.apis;

public interface MessageHandler {
	/**
	 * Send a message to this handler.
	 * 
	 * @param channelName
	 *            name of the channel where to send the message.
	 * @param message
	 *            instance of message to send for processing.
	 */
	public HandleReport handle(String channelName, Object message);

	public HandleReport handleIfLocal(String channelName, Object message);

	public void handleAll(String channelPrefix, Object message);

	/**
	 * Is the channel available on this message handler
	 * 
	 * @param channelName
	 *            the name of the channel to check.
	 * @return true or false
	 */
	public boolean isAvailable(String channelName);

	/**
	 * Is the channel busy on this message handler
	 * 
	 * @param channelName
	 *            the name of the channel to check.
	 * @return true if busy, false if idling
	 */
	public boolean isBusy(String channelName);

	/**
	 * Is the channel in overload?
	 * 
	 * @param channelName
	 * @return
	 */
	public boolean isOverloaded(String channelName);

}
