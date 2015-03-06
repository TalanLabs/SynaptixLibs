package com.synaptix.server.vysper.xmpp.modules;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.vysper.xml.fragment.XMLElement;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.modules.core.base.handler.DefaultIQHandler;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.server.SessionState;
import org.apache.vysper.xmpp.stanza.IQStanza;
import org.apache.vysper.xmpp.stanza.IQStanzaType;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;

import com.synaptix.abonnement.AbonnementDescriptor;
import com.synaptix.abonnement.IAbonnement;
import com.synaptix.abonnement.IParameters;
import com.synaptix.server.abonnement.AbonnementManager;
import com.synaptix.server.abonnement.NotFoundAbonnementException;
import com.synaptix.smackx.service.ConverterStringUtils;

public class SynaptixAbonnementIQHandler extends DefaultIQHandler {

	private static final Log LOG = LogFactory.getLog(SynaptixAbonnementIQHandler.class);

	public static final String SYNAPTIX_ABONNEMENT_IQ = "synaptix.abonnement:iq";

	@Override
	protected boolean verifyNamespace(Stanza stanza) {
		return verifyInnerNamespace(stanza, SYNAPTIX_ABONNEMENT_IQ);
	}

	@Override
	protected Stanza handleGet(IQStanza stanza, ServerRuntimeContext serverRuntimeContext, SessionContext sessionContext) {
		if (SessionState.AUTHENTICATED.equals(sessionContext.getState())) {
			XMLElement requestXMLElement = stanza.getFirstInnerElement().getFirstInnerElement();
			if (requestXMLElement != null && "request".equals(requestXMLElement.getName())) {
				String name = requestXMLElement.getAttributeValue("name");
				if (name == null) {
					StanzaBuilder stanzaBuilder = StanzaBuilder.createIQStanza(stanza.getTo(), stanza.getFrom(), IQStanzaType.RESULT, stanza.getID());
					stanzaBuilder.startInnerElement("query", SYNAPTIX_ABONNEMENT_IQ);
					stanzaBuilder.startInnerElement("abonnements", SYNAPTIX_ABONNEMENT_IQ);
					for (AbonnementDescriptor desc : AbonnementManager.getInstance().getAbonnementDescriptors()) {
						stanzaBuilder.startInnerElement("abonnement", SYNAPTIX_ABONNEMENT_IQ);
						stanzaBuilder.addAttribute("name", desc.getName());
						if (desc.getDescription() != null) {
							stanzaBuilder.startInnerElement("description", SYNAPTIX_ABONNEMENT_IQ).addText(desc.getDescription()).endInnerElement();
						}
						stanzaBuilder.endInnerElement();
					}
					stanzaBuilder.endInnerElement();
					stanzaBuilder.endInnerElement();

					return stanzaBuilder.build();
				} else {
					try {
						IAbonnement<Object> abonnement = (IAbonnement<Object>) AbonnementManager.getInstance().getAbonnement(name);

						StanzaBuilder stanzaBuilder = StanzaBuilder.createIQStanza(stanza.getTo(), stanza.getFrom(), IQStanzaType.RESULT, stanza.getID());
						stanzaBuilder.startInnerElement("query", SYNAPTIX_ABONNEMENT_IQ);
						stanzaBuilder.startInnerElement("abonnement", SYNAPTIX_ABONNEMENT_IQ);
						stanzaBuilder.addAttribute("name", name);
						if (abonnement.getAdherants() != null) {
							for (String user : abonnement.getAdherants()) {
								stanzaBuilder.startInnerElement("user", SYNAPTIX_ABONNEMENT_IQ).addAttribute("name", user).endInnerElement();
							}
						}
						stanzaBuilder.endInnerElement();
						stanzaBuilder.endInnerElement();
						StringBuilder sb = new StringBuilder();
						sb.append("<abonnement name=\"");
						sb.append(name);
						sb.append("\">");

						sb.append("</abonnement>");

						return stanzaBuilder.build();
					} catch (NotFoundAbonnementException e) {
						LOG.error("Error name : " + name + " from " + sessionContext.getInitiatingEntity());
					}
				}
			}
		}
		return super.handleGet(stanza, serverRuntimeContext, sessionContext);
	}

	@Override
	protected Stanza handleSet(IQStanza stanza, ServerRuntimeContext serverRuntimeContext, SessionContext sessionContext) {
		if (SessionState.AUTHENTICATED.equals(sessionContext.getState())) {
			XMLElement requestXMLElement = stanza.getFirstInnerElement().getFirstInnerElement();
			if (requestXMLElement != null && "request".equals(requestXMLElement.getName())) {
				String type = requestXMLElement.getAttributeValue("type");
				if (type != null && !type.isEmpty()) {
					String name = requestXMLElement.getAttributeValue("name");
					try {
						IAbonnement<Object> abonnement = (IAbonnement<Object>) AbonnementManager.getInstance().getAbonnement(name);

						if ("adherer".equals(type)) {
							LOG.debug("adherer " + name + " from " + sessionContext.getInitiatingEntity());
							IParameters<?> parameters = (IParameters<?>) ConverterStringUtils.stringToObject(requestXMLElement.getInnerText().getText());

							abonnement.adherer(sessionContext.getInitiatingEntity().getFullQualifiedName(), (IParameters<Object>) parameters);
						} else if ("revoquer".equals(type)) {
							LOG.debug("revoquer " + name + " from " + sessionContext.getInitiatingEntity());

							abonnement.revoquer(sessionContext.getInitiatingEntity().getFullQualifiedName());
						} else if ("notifier".equals(type)) {
							LOG.debug("notifier " + name);

							boolean sansMoi = Boolean.parseBoolean(requestXMLElement.getAttributeValue("sansMoi"));
							Object notifier = (Object) ConverterStringUtils.stringToObject(requestXMLElement.getInnerText().getText());

							String[] ss = abonnement.notifier(sessionContext.getInitiatingEntity().getFullQualifiedName(), notifier, sansMoi);

							LOG.debug("Notifie : " + Arrays.toString(ss));
							if (ss != null && ss.length != 0) {
								for (String s : ss) {
									Entity entity = EntityImpl.parse(s);
									List<SessionContext> scs = serverRuntimeContext.getResourceRegistry().getSessions(entity);
									for (SessionContext sc : scs) {
										StanzaBuilder stanzaBuilder = StanzaBuilder.createIQStanza(sessionContext.getInitiatingEntity(), entity, IQStanzaType.GET, stanza.getID());
										stanzaBuilder.startInnerElement("query", SYNAPTIX_ABONNEMENT_IQ);
										stanzaBuilder.startInnerElement("notification", SYNAPTIX_ABONNEMENT_IQ);
										stanzaBuilder.addAttribute("name", name);
										stanzaBuilder.addText(requestXMLElement.getInnerText().getText());
										stanzaBuilder.endInnerElement();
										stanzaBuilder.endInnerElement();
										Stanza stanza2 = stanzaBuilder.build();
										sc.getResponseWriter().write(stanza2);
									}
								}
							}
						}

						StanzaBuilder stanzaBuilder = StanzaBuilder.createIQStanza(stanza.getTo(), stanza.getFrom(), IQStanzaType.RESULT, stanza.getID());
						stanzaBuilder.startInnerElement("query", SYNAPTIX_ABONNEMENT_IQ);
						stanzaBuilder.endInnerElement();
						return stanzaBuilder.build();
					} catch (Exception e) {
						LOG.error("Error type=" + type + " name=" + name + " from=" + sessionContext.getInitiatingEntity(), e);
					}
				}
			}
		}
		return super.handleSet(stanza, serverRuntimeContext, sessionContext);
	}
}
