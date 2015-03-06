package com.synaptix.pmgr.core.apis;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;

import com.synaptix.pmgr.registry.SynchronizedRegistry;

public interface Engine extends MessageHandler {
	/**
	 * 
	 * @return bind name
	 */
	public String getRemoteBindName();

	public void plugChannel(PluggableChannel channel, boolean propagateRegistry);

	public void unplugChannel(String channelName);

	public void setListener(EngineListener listener);

	public Collection<ChannelSlot> getChannels();

	public SynchronizedRegistry getRegistry();

	public void shutdown() throws RemoteException, MalformedURLException, NotBoundException;

	public void setLogger(Log logger);

	public Log getLogger();

	public void activateChannels();

	public void handleMessageOnStratUp();

	/**
	 * Declare a group of channels for a unique name
	 * 
	 * @see com.synaptix.pmgr.core.lib.ProcessingChannel
	 * @param name
	 * @param channels
	 */
	public void declareGroup(String name, List<PluggableChannel> channels);
}
