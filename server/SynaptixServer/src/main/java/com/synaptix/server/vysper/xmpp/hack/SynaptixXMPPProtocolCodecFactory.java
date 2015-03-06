package com.synaptix.server.vysper.xmpp.hack;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.vysper.mina.codec.StanzaBuilderFactory;

public class SynaptixXMPPProtocolCodecFactory implements ProtocolCodecFactory {

	public ProtocolEncoder getEncoder(IoSession s) throws Exception {
		return new SynaptixStanzaWriterProtocolEncoder();
	}

	public ProtocolDecoder getDecoder(IoSession s) throws Exception {
		return new SynaptixXMPPDecoder(new StanzaBuilderFactory());
	}
}
