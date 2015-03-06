package com.synaptix.server.vysper.xmpp.modules;

import org.apache.vysper.xml.fragment.XMLElement;
import org.apache.vysper.xmpp.modules.core.base.handler.DefaultIQHandler;
import org.apache.vysper.xmpp.protocol.SessionStateHolder;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.server.SessionState;
import org.apache.vysper.xmpp.stanza.IQStanza;
import org.apache.vysper.xmpp.stanza.IQStanzaType;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;

import com.synaptix.server.vysper.xmpp.hack.IStateStanzaHandler;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.IServiceFactory.ServiceDescriptor;
import com.synaptix.service.NotFoundServiceFactoryException;
import com.synaptix.service.ServicesManager;

public class SynaptixServiceFactoryIQHandler extends DefaultIQHandler implements IStateStanzaHandler {

	public static final String SYNAPTIX_SERVICEFACTORY_IQ = "synaptix.servicefactory:iq";

	@Override
	protected boolean verifyNamespace(Stanza stanza) {
		return verifyInnerNamespace(stanza, SYNAPTIX_SERVICEFACTORY_IQ);
	}

	@Override
	public boolean checkState(SessionContext sessionContext, SessionStateHolder sessionStateHolder, Stanza stanza) {
		return SessionState.AUTHENTICATED.equals(sessionContext.getState()) || SessionState.ENCRYPTED.equals(sessionContext.getState());
	}

	@Override
	protected Stanza handleGet(IQStanza stanza, ServerRuntimeContext serverRuntimeContext, SessionContext sessionContext) {
		XMLElement requestXMLElement = stanza.getFirstInnerElement().getFirstInnerElement();
		if (requestXMLElement != null && "request".equals(requestXMLElement.getName())) {
			String serviceFactoryName = requestXMLElement.getAttributeValue("serviceFactoryName");
			if (serviceFactoryName == null) {
				StanzaBuilder stanzaBuilder = StanzaBuilder.createIQStanza(stanza.getTo(), stanza.getFrom(), IQStanzaType.RESULT, stanza.getID());
				stanzaBuilder.startInnerElement("query", SYNAPTIX_SERVICEFACTORY_IQ);
				stanzaBuilder.startInnerElement("result", SYNAPTIX_SERVICEFACTORY_IQ);
				stanzaBuilder.startInnerElement("servicefactories", SYNAPTIX_SERVICEFACTORY_IQ);
				String[] names = ServicesManager.getInstance().getServiceFactoryNames();
				for (String name : names) {
					stanzaBuilder.startInnerElement("servicefactory", SYNAPTIX_SERVICEFACTORY_IQ);
					stanzaBuilder.addAttribute("name", name);
					stanzaBuilder.endInnerElement();
				}
				stanzaBuilder.endInnerElement();
				stanzaBuilder.endInnerElement();
				stanzaBuilder.endInnerElement();

				return stanzaBuilder.build();
			} else {
				try {
					IServiceFactory serviceFactory = ServicesManager.getInstance().getServiceFactory(serviceFactoryName);

					StanzaBuilder stanzaBuilder = StanzaBuilder.createIQStanza(stanza.getTo(), stanza.getFrom(), IQStanzaType.RESULT, stanza.getID());
					stanzaBuilder.startInnerElement("query", SYNAPTIX_SERVICEFACTORY_IQ);
					stanzaBuilder.startInnerElement("result", SYNAPTIX_SERVICEFACTORY_IQ);

					stanzaBuilder.startInnerElement("servicefactory", SYNAPTIX_SERVICEFACTORY_IQ);
					stanzaBuilder.addAttribute("name", serviceFactoryName);
					for (ServiceDescriptor serviceDescriptor : serviceFactory.getServiceDescriptors()) {
						stanzaBuilder.startInnerElement("servicedescriptor", SYNAPTIX_SERVICEFACTORY_IQ);
						stanzaBuilder.startInnerElement("scope", SYNAPTIX_SERVICEFACTORY_IQ).addText(serviceDescriptor.getScope() != null ? serviceDescriptor.getScope() : "").endInnerElement();
						stanzaBuilder.startInnerElement("description", SYNAPTIX_SERVICEFACTORY_IQ).addText(serviceDescriptor.getDescription() != null ? serviceDescriptor.getDescription() : "")
								.endInnerElement();
						stanzaBuilder.startInnerElement("interfacename", SYNAPTIX_SERVICEFACTORY_IQ).addText(serviceDescriptor.getServiceClass().getName()).endInnerElement();
						stanzaBuilder.endInnerElement();
					}
					stanzaBuilder.endInnerElement();

					stanzaBuilder.endInnerElement();
					stanzaBuilder.endInnerElement();

					return stanzaBuilder.build();
				} catch (NotFoundServiceFactoryException e) {
					StanzaBuilder stanzaBuilder = StanzaBuilder.createIQStanza(stanza.getTo(), stanza.getFrom(), IQStanzaType.ERROR, stanza.getID());
					stanzaBuilder.startInnerElement("error");
					stanzaBuilder.addAttribute("type", "cancel");
					stanzaBuilder.endInnerElement();

					return stanzaBuilder.build();
				}
			}
		}

		return super.handleGet(stanza, serverRuntimeContext, sessionContext);
	}
}
