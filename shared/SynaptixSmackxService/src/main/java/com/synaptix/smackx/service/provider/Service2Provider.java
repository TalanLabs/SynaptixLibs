package com.synaptix.smackx.service.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import com.synaptix.smackx.service.ConverterStringUtils;
import com.synaptix.smackx.service.packet.ResultServiceIQ;

public class Service2Provider implements IQProvider {

	private static final Log LOG = LogFactory.getLog(Service2Provider.class);

	public IQ parseIQ(XmlPullParser parser) throws Exception {
		ResultServiceIQ res = new ResultServiceIQ();

		Object result = null;
		boolean done = false;
		while (!done) {
			int eventType = parser.next();

			if (eventType == XmlPullParser.START_TAG && "result".equalsIgnoreCase(parser.getName())) {
				try {
					result = ConverterStringUtils.stringToObject(parser.nextText());
				} catch (Exception e) {
					LOG.error(e, e);
					result = e;
				}
				res.setResult(result);
			} else if (eventType == XmlPullParser.END_TAG && "query".equalsIgnoreCase(parser.getName())) {
				done = true;
			}
		}

		return res;
	}

}
