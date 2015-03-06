package com.synaptix.smackx.abonnement.packet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.packet.IQ;

import com.synaptix.abonnement.IParameters;
import com.synaptix.smackx.abonnement.ConverterStringUtils;

public class RequestAdhererGestionAbonnementIQ extends GestionAbonnementIQ {

	private static Log log = LogFactory
			.getLog(RequestAdhererGestionAbonnementIQ.class);

	private String name;

	private IParameters<?> parameters;

	public RequestAdhererGestionAbonnementIQ(String name, IParameters<?> parameters) {
		this.name = name;
		this.parameters = parameters;

		this.setType(IQ.Type.SET);
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<request xmlns=\"synaptix.abonnement:iq\"");
		sb.append(" name=\"");
		sb.append(name);
		sb.append("\"");
		sb.append(" type=\"adherer\"");
		sb.append(">");
		try {
			sb.append(ConverterStringUtils.objectToString(parameters));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		sb.append("</request>");
		return sb.toString();
	}

}
