package com.synaptix.smackx.service;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.client.service.IReWaitResult;
import com.synaptix.client.service.IServiceMessageRecveidListener;

public class XmppConnectionInformation {

	private IReWaitResult defaultReWaitResult;

	private long defaultTimeout;

	private final List<IServiceMessageRecveidListener> listenerList;

	public XmppConnectionInformation() {
		listenerList = new ArrayList<IServiceMessageRecveidListener>();
		defaultTimeout = 60000;
	}

	public void setDefaultReWaitResult(IReWaitResult defaultReWaitResult) {
		this.defaultReWaitResult = defaultReWaitResult;
	}

	public IReWaitResult getDefaultReWaitResult() {
		return defaultReWaitResult;
	}

	public void setDefaultTimeout(long defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

	public long getDefaultTimeout() {
		return defaultTimeout;
	}

	public void addServiceMessageRecveidListener(IServiceMessageRecveidListener l) {
		synchronized (listenerList) {
			listenerList.add(l);
		}
	}

	public void removeServiceMessageRecveidListener(IServiceMessageRecveidListener l) {
		synchronized (listenerList) {
			listenerList.remove(l);
		}
	}

	public List<IServiceMessageRecveidListener> getListenerList() {
		return listenerList;
	}
}
