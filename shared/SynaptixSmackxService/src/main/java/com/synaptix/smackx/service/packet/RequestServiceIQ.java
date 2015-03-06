package com.synaptix.smackx.service.packet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.packet.IQ;

import com.synaptix.service.IServiceCallback;
import com.synaptix.smackx.service.ConverterStringUtils;

public class RequestServiceIQ extends ServiceIQ {

	private static final Log LOG = LogFactory.getLog(RequestServiceIQ.class);

	private String serviceFactoryName;

	private String serviceName;

	private String methodName;

	private Class<?>[] argTypes;

	private Object[] args;

	public RequestServiceIQ() {
		super();

		this.setType(IQ.Type.GET);
	}

	public String getServiceFactoryName() {
		return serviceFactoryName;
	}

	public void setServiceFactoryName(String service) {
		this.serviceFactoryName = service;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String className) {
		this.serviceName = className;
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

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<request xmlns=\"synaptix.service:iq\"");
		sb.append(" serviceFactoryName=\"");
		sb.append(serviceFactoryName);
		sb.append("\" serviceName=\"");
		sb.append(serviceName);
		sb.append("\" methodName=\"");
		sb.append(methodName);
		sb.append("\">");
		sb.append("<argTypes xmlns=\"synaptix.service:iq\">");
		try {
			sb.append(ConverterStringUtils.objectToString(argTypes));
		} catch (Exception e) {
			LOG.error(e, e);
		}
		sb.append("</argTypes>");
		sb.append("<args xmlns=\"synaptix.service:iq\">");
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];

				if (arg instanceof IServiceCallback) {
					sb.append("<arg callback=\"true\"/>");
				} else {
					sb.append("<arg callback=\"false\">");
					try {
						sb.append(ConverterStringUtils.objectToString(arg));
					} catch (Exception e) {
						LOG.error(e, e);
					}
					sb.append("</arg>");
				}
			}
		}
		sb.append("</args>");
		sb.append("</request>");
		return sb.toString();
	}
}
