package com.synaptix.server.vysper.xmpp.hack;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.vysper.mina.StanzaLoggingFilter;
import org.apache.vysper.xmpp.server.Endpoint;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;

public class SynaptixTCPEndpoint implements Endpoint {

	private ServerRuntimeContext serverRuntimeContext;

	private int port = 5222;

	private SocketAcceptor acceptor;

	private DefaultIoFilterChainBuilder filterChainBuilder;

	public DefaultIoFilterChainBuilder getFilterChainBuilder() {
		return filterChainBuilder;
	}

	public void setServerRuntimeContext(ServerRuntimeContext serverRuntimeContext) {
		this.serverRuntimeContext = serverRuntimeContext;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void start() throws IOException {
		NioSocketAcceptor acceptor = new NioSocketAcceptor();

		DefaultIoFilterChainBuilder filterChainBuilder = new DefaultIoFilterChainBuilder();
		// filterChainBuilder.addLast("executorFilter", new OrderedThreadPoolExecutor());
		// filterChainBuilder.addLast("xmppCodec", new ProtocolCodecFilter(new XMPPProtocolCodecFactory()));
		filterChainBuilder.addLast("xmppCodec", new ProtocolCodecFilter(new SynaptixXMPPProtocolCodecFactory()));
		filterChainBuilder.addLast("loggingFilter", new StanzaLoggingFilter());
		acceptor.setFilterChainBuilder(filterChainBuilder);

		SynaptixXmppIoHandlerAdapter adapter = new SynaptixXmppIoHandlerAdapter();
		adapter.setServerRuntimeContext(serverRuntimeContext);
		acceptor.setHandler(adapter);

		acceptor.setReuseAddress(true);
		acceptor.bind(new InetSocketAddress(port));

		this.acceptor = acceptor;
	}

	public void stop() {
		if (acceptor != null) {
			acceptor.unbind();
			acceptor.dispose();
		}
	}
}
