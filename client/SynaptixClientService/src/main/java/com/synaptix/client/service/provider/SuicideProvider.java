package com.synaptix.client.service.provider;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import com.synaptix.client.service.packet.ResultSuicideIQ;

public class SuicideProvider implements IQProvider {

	@Override
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		ResultSuicideIQ res = new ResultSuicideIQ();

		boolean done = false;
		while (!done) {
			int eventType = parser.getEventType();
			if (eventType == XmlPullParser.START_TAG && "suicide".equalsIgnoreCase(parser.getName())) {
				for (int i = 0; i < parser.getAttributeCount(); i++) {
					if ("force".equals(parser.getAttributeName(i))) {
						if ("true".equals(parser.getAttributeValue(i))) {
							res.setForce(true);
						}
					}
				}
			} else if (eventType == XmlPullParser.END_TAG && "suicide".equalsIgnoreCase(parser.getName())) {
				done = true;
			}
			if (!done) {
				parser.next();
			}
		}
		return res;
	}
}