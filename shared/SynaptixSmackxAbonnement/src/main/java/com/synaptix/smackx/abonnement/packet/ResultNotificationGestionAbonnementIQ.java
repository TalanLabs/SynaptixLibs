package com.synaptix.smackx.abonnement.packet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.smackx.abonnement.ConverterStringUtils;

public class ResultNotificationGestionAbonnementIQ extends GestionAbonnementIQ {

	private static Log log = LogFactory
			.getLog(ResultNotificationGestionAbonnementIQ.class);

	private String name;

	private Object value;

	public ResultNotificationGestionAbonnementIQ(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<notification");
		sb.append(" name=\"");
		sb.append(name);
		sb.append("\">");
		try {
			sb.append(ConverterStringUtils.objectToString(value));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		sb.append("</notification>");
		return sb.toString();
	}
}
