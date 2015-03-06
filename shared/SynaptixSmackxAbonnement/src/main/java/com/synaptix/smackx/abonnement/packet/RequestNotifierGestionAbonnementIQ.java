package com.synaptix.smackx.abonnement.packet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.packet.IQ;

import com.synaptix.abonnement.IParameters;
import com.synaptix.smackx.abonnement.ConverterStringUtils;

public class RequestNotifierGestionAbonnementIQ extends GestionAbonnementIQ {

	private static Log log = LogFactory
			.getLog(RequestNotifierGestionAbonnementIQ.class);

	private String name;

	private Object notifier;

	private boolean sansMoi;

	public RequestNotifierGestionAbonnementIQ(String name, Object notifier,
			boolean sansMoi) {
		this(name, "notifier", null, notifier, sansMoi);
	}

	private RequestNotifierGestionAbonnementIQ(String name, String type,
			IParameters<?> parameters, Object notifier, boolean sansMoi) {
		this.name = name;
		this.notifier = notifier;
		this.sansMoi = sansMoi;

		this.setType(IQ.Type.SET);
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<request xmlns=\"synaptix.abonnement:iq\"");
		sb.append(" name=\"");
		sb.append(name);
		sb.append("\"");
		sb.append(" type=\"notifier\"");
		sb.append(" sansMoi=\"");
		sb.append(sansMoi);
		sb.append("\"");
		sb.append(">");
		try {
			sb.append(ConverterStringUtils.objectToString(notifier));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		sb.append("</request>");
		return sb.toString();
	}

}
