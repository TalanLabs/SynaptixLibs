package com.synaptix.smackx.service.packet;

import org.jivesoftware.smack.packet.IQ;

public class ResultServiceFactoryIQ extends ServiceFactoryIQ {

	private String name;

	private ServiceDescriptor[] serviceDescriptors;

	public ResultServiceFactoryIQ() {
		super();

		this.setType(IQ.Type.RESULT);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ServiceDescriptor[] getServiceDescriptors() {
		return serviceDescriptors;
	}

	public void setServiceDescriptors(ServiceDescriptor[] serviceDescriptors) {
		this.serviceDescriptors = serviceDescriptors;
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<result xmlns=\"synaptix.servicefactory:iq\">");
		sb.append("<servicefactory name=\"");
		sb.append(name);
		sb.append("\">");
		for (ServiceDescriptor serviceDescriptor : serviceDescriptors) {
			sb.append("<servicedescriptor>");
			sb.append("<scope>");
			sb.append(serviceDescriptor.getScope());
			sb.append("</scope>");
			sb.append("<description>");
			sb.append(serviceDescriptor.getDescription());
			sb.append("</description>");
			sb.append("<interfacename>");
			sb.append(serviceDescriptor.getInterfaceName());
			sb.append("</interfacename>");
			sb.append("</servicedescriptor>");
		}
		sb.append("</servicefactory>");
		sb.append("</result>");
		return sb.toString();
	}

	public static final class ServiceDescriptor {

		private String scope;

		private String interfaceName;

		private String description;

		public ServiceDescriptor(String scope, String interfaceName,
				String description) {
			super();
			this.scope = scope;
			this.interfaceName = interfaceName;
			this.description = description;
		}

		public String getScope() {
			return scope;
		}

		public String getInterfaceName() {
			return interfaceName;
		}

		public String getDescription() {
			return description;
		}
	}
}
