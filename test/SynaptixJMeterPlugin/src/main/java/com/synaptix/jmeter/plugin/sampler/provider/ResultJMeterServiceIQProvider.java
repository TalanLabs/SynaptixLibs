package com.synaptix.jmeter.plugin.sampler.provider;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import com.synaptix.jmeter.plugin.sampler.packet.ResultJMeterServiceIQ;

public class ResultJMeterServiceIQProvider implements IQProvider {

	public IQ parseIQ(XmlPullParser parser) throws Exception {
		ResultJMeterServiceIQ res = new ResultJMeterServiceIQ();

		boolean done = false;
		while (!done) {
			int eventType = parser.next();

			if (eventType == XmlPullParser.START_TAG && "result".equalsIgnoreCase(parser.getName())) {
				res.setResult(parser.nextText());
			} else if (eventType == XmlPullParser.END_TAG && "query".equalsIgnoreCase(parser.getName())) {
				done = true;
			}
		}

		return res;
	}

}
