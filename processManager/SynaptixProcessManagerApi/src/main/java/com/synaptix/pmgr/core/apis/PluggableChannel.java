package com.synaptix.pmgr.core.apis;

public interface PluggableChannel extends Channel {
	/**
	 * Method to ask the channel to process a message.
	 * 
	 * @param message
	 *            the message to process
	 * @return true if this channel is available.
	 */
	public HandleReport acceptMessage(Object message, ChannelSlot slot);

	public boolean isOverloaded();

	/**
	 * At least one agent is working
	 * 
	 * @return
	 */
	public boolean isBusy();

}
