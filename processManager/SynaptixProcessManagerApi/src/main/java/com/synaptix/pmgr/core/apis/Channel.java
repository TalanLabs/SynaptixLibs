package com.synaptix.pmgr.core.apis;

public interface Channel {

	public String getName();

	/**
	 * Is the channel available, ie able to process a message ?
	 * 
	 * @return true if this channel is available.
	 */
	public boolean isAvailable();

	public void setAvailable(boolean available);

	public int getNbWorking();

	public int getNbWaiting();

	public boolean isLocal();

	public void activate();

}
