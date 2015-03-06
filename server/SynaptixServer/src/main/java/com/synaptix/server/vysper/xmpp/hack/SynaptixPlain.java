package com.synaptix.server.vysper.xmpp.hack;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.vysper.xml.fragment.XMLText;
import org.apache.vysper.xmpp.addressing.EntityFormatException;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.authorization.AuthorizationResponses;
import org.apache.vysper.xmpp.authorization.SASLMechanism;
import org.apache.vysper.xmpp.modules.core.sasl.SASLFailureType;
import org.apache.vysper.xmpp.protocol.NamespaceURIs;
import org.apache.vysper.xmpp.protocol.SessionStateHolder;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.server.SessionState;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;

import com.synaptix.server.vysper.xmpp.auth.CredentialsException;

/**
 * handles SASL PLAIN mechanism. this mechanism is standardized in RFC4616
 *
 * @author The Apache MINA Project (dev@mina.apache.org)
 */
public class SynaptixPlain implements SASLMechanism {

	private static final AuthorizationResponses AUTHORIZATION_RESPONSES = new AuthorizationResponses();

	public String getName() {
		return "PLAIN";
	}

	public Stanza started(SessionContext sessionContext, SessionStateHolder sessionStateHolder, Stanza authStanza) {
		// TODO assure, that connection is secured via TLS. if not, reject SASL PLAIN

		List<XMLText> innerTexts = authStanza.getInnerTexts();
		if (innerTexts == null || innerTexts.isEmpty())
			return AUTHORIZATION_RESPONSES.getFailureMalformedRequest();

		// retrieve credential payload and decode from BASE64
		XMLText base64Encoded = innerTexts.get(0);
		byte[] decoded;
		try {
			decoded = Base64.decodeBase64(base64Encoded.getText().getBytes());
		} catch (Throwable e) {
			return AUTHORIZATION_RESPONSES.getFailure(SASLFailureType.INCORRECT_ENCODING);
		}

		// parse clear text, extract parts, which are separated by zeros
		List<String> decodedParts = new ArrayList<String>();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < decoded.length; i++) {
			char ch = (char) decoded[i];
			if (ch != 0) {
				stringBuilder.append(ch);
			}
			if (ch == 0 || i == decoded.length - 1) {
				decodedParts.add(stringBuilder.toString());
				stringBuilder = new StringBuilder();
			}
		}

		if (decodedParts.size() != 3) {
			return AUTHORIZATION_RESPONSES.getFailureMalformedRequest();
		}

		String alias = decodedParts.get(0); // "authorization identity (identity to act as)", currently unused
		String username = decodedParts.get(1); // "authentication identity (identity whose password will be used)"
		String password = decodedParts.get(2);

		if (!username.contains("@"))
			username = username + "@" + sessionContext.getServerJID().getDomain();
		EntityImpl initiatingEntity;
		try {
			initiatingEntity = EntityImpl.parse(username);
		} catch (EntityFormatException e) {
			return AUTHORIZATION_RESPONSES.getFailureNotAuthorized();
		}

		boolean authorized = false;
		try {
			authorized = sessionContext.getServerRuntimeContext().getUserAuthorization().verifyCredentials(username, password, null);
		} catch (CredentialsException e) {
			String condition = e.getMessage();
			return new StanzaBuilder("failure", NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_SASL).startInnerElement(condition, NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_SASL).endInnerElement().build();
		}

		if (authorized) {
			sessionContext.setInitiatingEntity(initiatingEntity);
			sessionStateHolder.setState(SessionState.AUTHENTICATED);
			return AUTHORIZATION_RESPONSES.getSuccess();
		} else {
			return AUTHORIZATION_RESPONSES.getFailureNotAuthorized();
		}
	}
}
