package com.synaptix.client.service;

import org.jivesoftware.smack.XMPPConnection;

import com.synaptix.widget.core.controller.ISimpleFrontendContext;

public interface IFrontendContext extends ISimpleFrontendContext {

	/**
	 * Get a xmppConnection
	 * 
	 * @return
	 */
	public XMPPConnection getXmppConnection();

	/**
	 * Get a service communicator
	 * 
	 * @return
	 */
	public IServiceCommunicator getServiceCommunicator();

}
