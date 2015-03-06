package com.synaptix.smackx.service.packet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.packet.IQ;

import com.synaptix.smackx.service.ConverterStringUtils;

public class RequestServiceCallbackIQ extends ServiceCallbackIQ {

	private static final Log LOG = LogFactory.getLog(RequestServiceCallbackIQ.class);

	private int positionArg;

	private String methodName;

	private Class<?>[] argTypes;

	private Object[] args;

	private Exception exception;

	public RequestServiceCallbackIQ() {
		super();

		this.setType(IQ.Type.GET);
	}

	public int getPositionArg() {
		return positionArg;
	}

	public void setPositionArg(int positionArg) {
		this.positionArg = positionArg;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getArgTypes() {
		return argTypes;
	}

	public void setArgTypes(Class<?>[] argTypes) {
		this.argTypes = argTypes;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public Exception getException() {
		return exception;
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<request xmlns=\"synaptix.service.callback:iq\"");
		sb.append(" positionArg=\"");
		sb.append(positionArg);
		sb.append("\" methodName=\"");
		sb.append(methodName);
		sb.append("\">");
		sb.append("<argTypes>");
		try {
			sb.append(ConverterStringUtils.objectToString(argTypes));
		} catch (Exception e) {
			LOG.error(e, e);
		}
		sb.append("</argTypes>");
		sb.append("<args>");
		try {
			sb.append(ConverterStringUtils.objectToString(args));
		} catch (Exception e) {
			LOG.error(e, e);
		}
		sb.append("</args>");
		sb.append("</request>");
		return sb.toString();
	}
}
