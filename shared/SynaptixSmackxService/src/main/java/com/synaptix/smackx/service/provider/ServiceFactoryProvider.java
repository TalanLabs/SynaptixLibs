package com.synaptix.smackx.service.provider;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import com.synaptix.smackx.service.packet.ResultServiceFactoriesIQ;
import com.synaptix.smackx.service.packet.ResultServiceFactoryIQ;
import com.synaptix.smackx.service.packet.ResultServiceFactoryIQ.ServiceDescriptor;
import com.synaptix.smackx.service.packet.ServiceFactoryIQ;

public class ServiceFactoryProvider implements IQProvider {

	public IQ parseIQ(XmlPullParser parser) throws Exception {
		ServiceFactoryIQ res = null;

		boolean done = false;
		while (!done) {
			int eventType = parser.next();

			if (eventType == XmlPullParser.START_TAG) {
				if ("servicefactories".equalsIgnoreCase(parser.getName())) {
					res = processServiceFactories(parser);
				} else if ("servicefactory".equalsIgnoreCase(parser.getName())) {
					res = processServiceFactory(parser);
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("query".equalsIgnoreCase(parser.getName())) {
					done = true;
				}
			}
		}

		return res;
	}

	private ResultServiceFactoriesIQ processServiceFactories(
			XmlPullParser parser) throws Exception {
		boolean done = false;

		List<String> names = new ArrayList<String>();

		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if ("servicefactory".equalsIgnoreCase(parser.getName())) {
					names.add(parser.getAttributeValue(null, "name"));
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("servicefactories".equalsIgnoreCase(parser.getName())) {
					done = true;
				}
			}
		}

		ResultServiceFactoriesIQ res = new ResultServiceFactoriesIQ();
		res.setServiceFactoryNames(names.toArray(new String[names.size()]));
		return res;
	}

	private ResultServiceFactoryIQ processServiceFactory(XmlPullParser parser)
			throws Exception {
		boolean done = false;

		String name = parser.getAttributeValue(null, "name");

		List<ServiceDescriptor> serviceDescriptorList = new ArrayList<ServiceDescriptor>();

		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if ("servicedescriptor".equalsIgnoreCase(parser.getName())) {
					ServiceDescriptor s = processServiceDescriptor(parser);
					if (s != null) {
						serviceDescriptorList.add(s);
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("servicefactory".equalsIgnoreCase(parser.getName())) {
					done = true;
				}
			}
		}

		ResultServiceFactoryIQ res = new ResultServiceFactoryIQ();
		res.setName(name);
		res.setServiceDescriptors(serviceDescriptorList
				.toArray(new ServiceDescriptor[serviceDescriptorList.size()]));
		return res;
	}

	private ServiceDescriptor processServiceDescriptor(XmlPullParser xpp)
			throws Exception {
		boolean done = false;

		String scope = null;
		String interfaceName = null;
		String description = null;

		while (!done) {
			int eventType = xpp.next();
			if (eventType == XmlPullParser.START_TAG) {
				if ("scope".equalsIgnoreCase(xpp.getName())) {
					scope = xpp.nextText();
				} else if ("interfaceName".equalsIgnoreCase(xpp.getName())) {
					interfaceName = xpp.nextText();
				} else if ("description".equalsIgnoreCase(xpp.getName())) {
					description = xpp.nextText();
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("servicedescriptor".equalsIgnoreCase(xpp.getName())) {
					done = true;
				}
			}
		}

		return new ServiceDescriptor(scope, interfaceName, description);
	}
}
