package com.synaptix.pmgr.core.apis;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteMessageHandler extends Remote {

	public void remoteHandle(String channelName, Object message) throws RemoteException;

	public boolean isAvailable(String channelName) throws RemoteException;

}
