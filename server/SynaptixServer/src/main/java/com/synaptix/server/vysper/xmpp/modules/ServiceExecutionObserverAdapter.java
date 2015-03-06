package com.synaptix.server.vysper.xmpp.modules;

import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.IQStanza;

public class ServiceExecutionObserverAdapter implements IServiceExecutionObserver {

	@Override
	public void startExecution(String id, IQStanza stanza, String serviceFactoryName, String serviceName, String methodName, Class<?>[] argTypes, Object[] args, SessionContext sessionContext) {
	}

	@Override
	public void endExecution(String id, Object result) {
	}
}
