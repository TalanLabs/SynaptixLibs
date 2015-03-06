package com.synaptix.server.vysper.xmpp.hack;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.vysper.mina.codec.StanzaWriteInfo;
import org.apache.vysper.mina.codec.StanzaWriterProtocolEncoder;
import org.apache.vysper.xml.fragment.Renderer;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.writer.StanzaWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynaptixStanzaWriterProtocolEncoder extends StanzaWriterProtocolEncoder {

	private Logger log = LoggerFactory.getLogger(SynaptixStanzaWriterProtocolEncoder.class);

	public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
		if (!(o instanceof StanzaWriteInfo)) {
			throw new IllegalArgumentException("StanzaWriterProtocolEncoder only handles StanzaWriteInfo objects");
		}
		StanzaWriteInfo stanzaWriteInfo = (StanzaWriteInfo) o;

		Stanza element = stanzaWriteInfo.getStanza();
		Renderer renderer = new Renderer(element);

		if (log.isDebugEnabled()) {
			log.debug("Encoder reading stanza: {}", renderer.getComplete());
		}

		IoBuffer byteBuffer = IoBuffer.allocate(16).setAutoExpand(true);
		if (stanzaWriteInfo.isWriteProlog())
			byteBuffer.putString(StanzaWriter.XML_PROLOG, getSessionEncoder());
		if (stanzaWriteInfo.isWriteOpeningElement())
			byteBuffer.putString(renderer.getOpeningElement(), getSessionEncoder());
		if (stanzaWriteInfo.isWriteContent())
			byteBuffer.putString(renderer.getElementContent(), getSessionEncoder());
		if (stanzaWriteInfo.isWriteClosingElement())
			byteBuffer.putString(renderer.getClosingElement(), getSessionEncoder());

		byteBuffer.flip();

		// SessionContext sessionContext = (SessionContext) ioSession.getAttribute("vysperSession");
		// boolean compressed = sessionContext != null && sessionContext.getAttribute("compressed") instanceof Boolean ? (Boolean) sessionContext.getAttribute("compressed") : false;
		// System.out.println("compressed " + compressed);
		// if (compressed) {
		// boolean first = sessionContext != null && sessionContext.getAttribute("first") instanceof Boolean ? (Boolean) sessionContext.getAttribute("first") : false;
		// if (first) {
		// sessionContext.putAttribute("first", false);
		// } else {
		// try {
		// Zlib zlib = new Zlib(Zlib.COMPRESSION_DEFAULT, Zlib.MODE_DEFLATER);
		// byteBuffer = zlib.deflate(byteBuffer);
		// // byte[] inBytes = new byte[byteBuffer.remaining()];
		// // byteBuffer.get(inBytes).flip();
		// // IoBuffer out = IoBuffer.allocate(inBytes.length * 2).setAutoExpand(true);
		// // for (int i = 0; i < inBytes.length; i++) {
		// // out.put((byte) 255);
		// // out.put(inBytes[i]);
		// // }
		// // out.flip();
		// // byteBuffer = out;
		// } catch (Exception e) {
		// e.printStackTrace();
		//
		// }
		// }
		// }

		protocolEncoderOutput.write(byteBuffer);
	}
}
