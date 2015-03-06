package com.synaptix.server.vysper.xmpp.modules;

import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.IQStanza;

public interface IServiceExecutionObserver {

	public void startExecution(String id, IQStanza stanza, String serviceFactoryName, String serviceName, String methodName, Class<?>[] argTypes, Object[] args, SessionContext sessionContext);

	public void endExecution(String id, Object result);

}
