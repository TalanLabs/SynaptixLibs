package com.synaptix.smackx.service.packet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.packet.IQ;

import com.synaptix.smackx.service.ConverterStringUtils;

public class ResultServiceIQ extends ServiceIQ {

	private static final Log LOG = LogFactory.getLog(ResultServiceIQ.class);

	private Object result;

	public ResultServiceIQ() {
		super();

		this.setType(IQ.Type.RESULT);
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<result xmlns=\"synaptix.service:iq\">");
		try {
			sb.append(ConverterStringUtils.objectToString(result));
		} catch (Exception e) {
			LOG.error(e, e);
		}
		sb.append("</result>");
		return sb.toString();
	}
}
