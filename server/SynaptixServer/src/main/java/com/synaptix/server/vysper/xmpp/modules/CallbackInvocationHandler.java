package com.synaptix.server.vysper.xmpp.modules;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.IQStanza;
import org.apache.vysper.xmpp.stanza.IQStanzaType;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;

import com.synaptix.smackx.service.ConverterStringUtils;

public class CallbackInvocationHandler implements InvocationHandler {

	private static final Log LOG = LogFactory.getLog(CallbackInvocationHandler.class);

	public static final String SYNAPTIX_SERVICE_CALLBACK_IQ = "synaptix.service.callback:iq";

	private IQStanza stanza;

	private ServerRuntimeContext serverRuntimeContext;

	private SessionContext sessionContext;

	private int positionArg;

	public CallbackInvocationHandler(IQStanza stanza, ServerRuntimeContext serverRuntimeContext, SessionContext sessionContext, int positionArg) {
		super();

		this.stanza = stanza;
		this.serverRuntimeContext = serverRuntimeContext;
		this.sessionContext = sessionContext;
		this.positionArg = positionArg;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		StanzaBuilder stanzaBuilder = StanzaBuilder.createIQStanza(stanza.getTo(), stanza.getFrom(), IQStanzaType.RESULT, stanza.getID());
		stanzaBuilder.startInnerElement("query", SYNAPTIX_SERVICE_CALLBACK_IQ);
		stanzaBuilder.startInnerElement("request", SYNAPTIX_SERVICE_CALLBACK_IQ);
		stanzaBuilder.addAttribute("positionArg", String.valueOf(positionArg));
		stanzaBuilder.addAttribute("methodName", method.getName());

		stanzaBuilder.startInnerElement("argTypes", SYNAPTIX_SERVICE_CALLBACK_IQ);
		try {
			stanzaBuilder.addText(ConverterStringUtils.objectToString(method.getParameterTypes()));
		} catch (Exception e) {
			LOG.error("ConverterStringUtils.objectToString(method.getParameterTypes()) " + sessionContext.getInitiatingEntity(), e);
		}
		stanzaBuilder.endInnerElement();

		stanzaBuilder.startInnerElement("args", SYNAPTIX_SERVICE_CALLBACK_IQ);
		try {
			stanzaBuilder.addText(ConverterStringUtils.objectToString(args));
		} catch (Exception e) {
			LOG.error("ConverterStringUtils.objectToString(method.getParameterTypes()) " + sessionContext.getInitiatingEntity(), e);
		}
		stanzaBuilder.endInnerElement();

		stanzaBuilder.endInnerElement();
		stanzaBuilder.endInnerElement();

		sessionContext.getResponseWriter().write(stanzaBuilder.build());

		return null;
	}
}