package com.synaptix.client.service.provider;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import com.synaptix.client.service.packet.ResultInformationMessageIQ;

public class InformationMessageProvider implements IQProvider {

	@Override
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		ResultInformationMessageIQ res = new ResultInformationMessageIQ();

		boolean start = false;
		boolean done = false;
		while (!done) {
			int eventType = parser.getEventType();
			if (eventType == XmlPullParser.START_TAG && "informationMessage".equalsIgnoreCase(parser.getName())) {
				start = true;
			} else if ((eventType == XmlPullParser.TEXT) && (start)) {
				res.setMessage(parser.getText());
			} else if (eventType == XmlPullParser.END_TAG && "informationMessage".equalsIgnoreCase(parser.getName())) {
				done = true;
			}
			if (!done) {
				parser.next();
			}
		}
		return res;
	}
}