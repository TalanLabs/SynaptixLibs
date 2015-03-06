package com.synaptix.pmgr.core.apis;

import java.util.List;

import org.apache.commons.logging.Log;

public interface ChannelSlot extends Channel {

	public void plugChannel(PluggableChannel channel);

	public void unplugChannel();

	public boolean isPlugged();

	public PluggableChannel getPluggedChannel();

	public int getBufferedMessageCount();

	/**
	 * Method to ask the channel to process a message.
	 * 
	 * @param message
	 *            the message to process
	 * @return true if this channel is available.
	 */
	public HandleReport acceptMessage(Object message);

	public Log getLogger();

	public List<Object> getBufferedMessage();

	public void storeBufferedMessage(String filebase, Object message, int cpt);

	/**
	 * At least one agent is working
	 * 
	 * @return
	 */
	public boolean isBusy();

	public void clearSavedMessages();

}
