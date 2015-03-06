package com.synaptix.server.vysper.xmpp.hack;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.vysper.xml.decoder.XMLElementBuilderFactory;
import org.apache.vysper.xml.decoder.XMPPDecoder;

public class SynaptixXMPPDecoder extends XMPPDecoder {

	public SynaptixXMPPDecoder(XMLElementBuilderFactory builderFactory) {
		super(builderFactory);
	}

	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// SessionContext sessionContext = (SessionContext) session.getAttribute("vysperSession");
		// boolean compressed = sessionContext != null && sessionContext.getAttribute("compressed") instanceof Boolean ? (Boolean) sessionContext.getAttribute("compressed") : false;
		// System.out.println("decompressed " + compressed);
		// IoBuffer buf;
		// if (compressed) {
		// try {
		// byte[] inBytes = new byte[in.remaining()];
		//
		// System.out.println(in.limit() + " " + in.remaining() + " " + in.position());
		//
		// in.get(inBytes).flip();
		//
		// byte[] outBytes = new byte[inBytes.length / 2];
		// IoBuffer outBuffer = IoBuffer.allocate(outBytes.length);
		// for (int i = 0; i < inBytes.length; i += 2) {
		// outBytes[i / 2] = inBytes[i];
		// }
		// buf = outBuffer.put(outBytes);
		// } catch (Exception e) {
		// System.out.println("erreur " + e);
		// super.decode(session, in, out);
		// return;
		// }
		// } else {
		// buf = in;
		// }
		super.decode(session, in, out);
	}
}
