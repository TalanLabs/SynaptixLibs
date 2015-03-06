import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.compression.XMPPInputOutputStream;

import com.jcraft.jzlib.ZInputStream;

public class MainStartClient {

	public static void main(String[] args) throws Exception {
		SmackConfiguration.setPacketReplyTimeout(600000);
		SmackConfiguration.setKeepAliveInterval(30000);

		XMPPConnection xmppConnection1 = login("demo");

		// ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(xmppConnection1);
		// DiscoverInfo result = sdm.discoverInfo(null);
		// System.out.println(result.containsFeature("coucou"));
		//
		// // XMPPConnection xmppConnection2 = login("rien");
		//
		// Message m = new Message("rien@gaby.com");
		// m.setBody("Coucou");
		// xmppConnection1.sendPacket(m);

		Thread.sleep(200000);

		xmppConnection1.disconnect();
		// xmppConnection2.disconnect();

		System.out.println("finish");
		System.exit(0);
	}

	private static XMPPConnection login(String user) throws Exception {
		ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration("localhost");
		connectionConfiguration.setCompressionEnabled(false);
		connectionConfiguration.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
		connectionConfiguration.setVerifyChainEnabled(false);
		connectionConfiguration.setVerifyRootCAEnabled(false);
		connectionConfiguration.setDebuggerEnabled(true);
		connectionConfiguration.setSelfSignedCertificateEnabled(true);
		connectionConfiguration.setSASLAuthenticationEnabled(true);
		XMPPConnection xmppConnection = new XMPPConnection(connectionConfiguration);

		xmppConnection.connect();
		// xmppConnection.login(user, "123456");
		xmppConnection.loginAnonymously();
		return xmppConnection;
	}

	private static class MyXMPPConnection extends XMPPConnection {

		static {
			compressionHandlers.add(new MyXMPPInputOutputStream());
		}

		public MyXMPPConnection(ConnectionConfiguration configuration) {
			super(configuration);
		}

	}

	private static class MyXMPPInputOutputStream extends XMPPInputOutputStream {

		public MyXMPPInputOutputStream() {
			super();
			this.compressionMethod = "gaby";
		}

		@Override
		public boolean isSupported() {
			return true;
		}

		@Override
		public InputStream getInputStream(InputStream inputStream) throws Exception {
			return new ZInputStream(inputStream);
		}

		@Override
		public OutputStream getOutputStream(OutputStream outputStream) throws Exception {
			return new MyOutputStream(outputStream);
		}
	}

	private static class MyInputStream extends InputStream {

		private InputStream in;

		List<Byte> bytes = new ArrayList<Byte>();

		public MyInputStream(InputStream in) {
			super();
			this.in = in;
		}

		@Override
		public int read() throws IOException {
			int i1 = in.read();
			int i2 = in.read();

			bytes.add((byte) i2);

			byte[] bs = new byte[bytes.size()];
			for (int i = 0; i < bytes.size(); i++) {
				bs[i] = bytes.get(i);
			}
			System.out.println(new String(bs));

			return i2;
		}

		@Override
		public void close() throws IOException {
			super.close();
		}
	}

	private static class MyOutputStream extends OutputStream {

		private OutputStream out;

		public MyOutputStream(OutputStream out) {
			super();
			this.out = out;
		}

		@Override
		public void write(int b) throws IOException {
			// out.write(255);
			out.write(b);
		}

		@Override
		public void flush() throws IOException {
			super.flush();
			out.flush();
		}

		@Override
		public void close() throws IOException {
			super.close();
			out.close();
		}
	}
}
