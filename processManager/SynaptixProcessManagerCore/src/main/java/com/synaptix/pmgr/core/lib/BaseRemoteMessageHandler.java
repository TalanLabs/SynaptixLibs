package com.synaptix.pmgr.core.lib;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.synaptix.pmgr.core.apis.MessageHandler;
import com.synaptix.pmgr.core.apis.RemoteMessageHandler;

public class BaseRemoteMessageHandler extends UnicastRemoteObject implements
		RemoteMessageHandler {
	private static final long serialVersionUID = 8284575197277530559L;
	private MessageHandler handler;

	public BaseRemoteMessageHandler(MessageHandler handler)
			throws RemoteException {
		this.handler = handler;
	}

	boolean opened = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synaptix.pmgr.api.RemoteMessageHandler#remoteHandle(java.lang.String,
	 * java.lang.Object)
	 */
	public synchronized void remoteHandle(String channelid, Object message)
			throws RemoteException {
		handler.handleIfLocal(channelid, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synaptix.pmgr2.apis.RemoteMessageHandler#isAvailable(java.lang.String
	 * )
	 */
	public boolean isAvailable(String channelName) throws RemoteException {
		return handler.isAvailable(channelName);
	}

}