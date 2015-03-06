package com.synaptix.smackx.abonnement.provider;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import com.synaptix.abonnement.AbonnementDescriptor;
import com.synaptix.smackx.abonnement.ConverterStringUtils;
import com.synaptix.smackx.abonnement.packet.GestionAbonnementIQ;
import com.synaptix.smackx.abonnement.packet.ResultInfoAbonnementGestionAbonnementIQ;
import com.synaptix.smackx.abonnement.packet.ResultInfoAbonnementsGestionAbonnementIQ;
import com.synaptix.smackx.abonnement.packet.ResultNotificationGestionAbonnementIQ;

public class GestionAbonnementProvider implements IQProvider {

	public IQ parseIQ(XmlPullParser parser) throws Exception {
		GestionAbonnementIQ res = null;
		
		boolean done = false;
		while (!done) {
			int eventType = parser.next();

			if (eventType == XmlPullParser.START_TAG) {
				if ("abonnements".equalsIgnoreCase(parser.getName())) {
					res = processResultInfoAbonnements(parser);
				} else if ("abonnement".equalsIgnoreCase(parser.getName())) {
					res = processResultInfoAbonnement(parser);
				} else if ("notification".equalsIgnoreCase(parser.getName())) {
					res = processResultNotification(parser);
				}
			} else if (eventType == XmlPullParser.END_TAG
					&& "query".equalsIgnoreCase(parser.getName())) {
				done = true;
			}
		}

		return res;
	}

	private ResultInfoAbonnementsGestionAbonnementIQ processResultInfoAbonnements(
			XmlPullParser parser) throws Exception {
		boolean done = false;

		List<AbonnementDescriptor> descList = new ArrayList<AbonnementDescriptor>();

		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if ("abonnement".equalsIgnoreCase(parser.getName())) {
					descList.add(processAbonnementDescriptor(parser));
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("abonnements".equalsIgnoreCase(parser.getName())) {
					done = true;
				}
			}
		}

		return new ResultInfoAbonnementsGestionAbonnementIQ(descList
				.toArray(new AbonnementDescriptor[descList.size()]));
	}

	private AbonnementDescriptor processAbonnementDescriptor(
			XmlPullParser parser) throws Exception {
		boolean done = false;

		String name = parser.getAttributeValue(null, "name");
		String description = null;

		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if ("description".equalsIgnoreCase(parser.getName())) {
					description = parser.nextText();
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("abonnement".equalsIgnoreCase(parser.getName())) {
					done = true;
				}
			}
		}

		return new AbonnementDescriptor(name, null, description);
	}

	private ResultInfoAbonnementGestionAbonnementIQ processResultInfoAbonnement(
			XmlPullParser parser) throws Exception {
		boolean done = false;

		String name = parser.getAttributeValue(null, "name");

		List<String> userList = new ArrayList<String>();

		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if ("user".equalsIgnoreCase(parser.getName())) {
					String n = parser.getAttributeValue(null, "name");
					userList.add(n);
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("abonnement".equalsIgnoreCase(parser.getName())) {
					done = true;
				}
			}
		}

		return new ResultInfoAbonnementGestionAbonnementIQ(name, userList
				.toArray(new String[userList.size()]));
	}

	private ResultNotificationGestionAbonnementIQ processResultNotification(
			XmlPullParser parser) throws Exception {
		boolean done = false;

		String name = parser.getAttributeValue(null, "name");

		Object value = null;

		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.TEXT) {
				value = ConverterStringUtils.stringToObject(parser.getText());
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("notification".equalsIgnoreCase(parser.getName())) {
					done = true;
				}
			}
		}

		return new ResultNotificationGestionAbonnementIQ(name, value);
	}
}
