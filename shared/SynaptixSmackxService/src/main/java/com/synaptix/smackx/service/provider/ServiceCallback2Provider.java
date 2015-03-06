package com.synaptix.smackx.service.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import com.synaptix.smackx.service.ConverterStringUtils;
import com.synaptix.smackx.service.packet.RequestServiceCallbackIQ;
import com.synaptix.smackx.service.packet.ServiceCallbackIQ;

public class ServiceCallback2Provider implements IQProvider {

	private static final Log LOG = LogFactory.getLog(ServiceCallback2Provider.class);

	public IQ parseIQ(XmlPullParser parser) throws Exception {
		ServiceCallbackIQ res = null;

		boolean done = false;
		while (!done) {
			int eventType = parser.next();

			if (eventType == XmlPullParser.START_TAG && "request".equalsIgnoreCase(parser.getName())) {
				res = processRequest(parser);
			} else if (eventType == XmlPullParser.END_TAG && "query".equalsIgnoreCase(parser.getName())) {
				done = true;
			}
		}

		return res;
	}

	private RequestServiceCallbackIQ processRequest(XmlPullParser parser) throws Exception {
		boolean done = false;

		int positionArg = Integer.parseInt(parser.getAttributeValue(null, "positionArg"));
		String methodName = parser.getAttributeValue(null, "methodName");
		Class<?>[] argTypes = null;
		Object[] args = null;
		Exception exception = null;
		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if ("argTypes".equalsIgnoreCase(parser.getName())) {
					try {
						argTypes = (Class<?>[]) ConverterStringUtils.stringToObject(parser.nextText());
					} catch (Exception e) {
						LOG.error(e, e);
						exception = e;
					}
				} else if ("args".equalsIgnoreCase(parser.getName())) {
					try {
						args = (Object[]) ConverterStringUtils.stringToObject(parser.nextText());
					} catch (Exception e) {
						LOG.error(e, e);
						exception = e;
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("request".equalsIgnoreCase(parser.getName())) {
					done = true;
				}
			}
		}

		RequestServiceCallbackIQ res = new RequestServiceCallbackIQ();
		res.setPositionArg(positionArg);
		res.setMethodName(methodName);
		res.setArgTypes(argTypes);
		res.setArgs(args);
		res.setException(exception);
		return res;
	}
}
