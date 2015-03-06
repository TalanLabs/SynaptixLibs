package com.synaptix.smackx.version.provider;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import com.synaptix.smackx.version.packet.ResultServerVersionIQ;
import com.synaptix.smackx.version.packet.ServerVersionIQ;

public class ServerVersionProvider implements IQProvider {

	public IQ parseIQ(XmlPullParser parser) throws Exception {
		ServerVersionIQ res = null;

		boolean done = false;
		while (!done) {
			int eventType = parser.next();

			if (eventType == XmlPullParser.START_TAG) {
				if ("result".equalsIgnoreCase(parser.getName())) {
					res = processResult(parser);
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("query".equalsIgnoreCase(parser.getName())) {
					done = true;
				}
			}
		}

		return res;
	}

	private ResultServerVersionIQ processResult(XmlPullParser parser) throws Exception {
		boolean done = false;

		String serverVersion = null;

		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if ("serverVersion".equalsIgnoreCase(parser.getName())) {
					serverVersion = parser.nextText();
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("result".equalsIgnoreCase(parser.getName())) {
					done = true;
				}
			}
		}

		ResultServerVersionIQ res = new ResultServerVersionIQ();
		res.setServerVersion(serverVersion);
		return res;
	}
}
