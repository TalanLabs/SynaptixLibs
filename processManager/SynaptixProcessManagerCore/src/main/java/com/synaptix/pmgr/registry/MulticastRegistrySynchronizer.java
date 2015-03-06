package com.synaptix.pmgr.registry;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class MulticastRegistrySynchronizer implements RegistrySynchronizer {
	@SuppressWarnings("unused")
	private NetworkInterface netInterface;

	private boolean listen = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.RegistrySynchronizer#listen()
	 */
	@Override
	public void listen() {
		listen = true;
	}

	// SynchronizedRegistry this synchronizer is responsible for
	private SynchronizedRegistry registry;
	// Max data to be sent through UDP socket
	private int maxdatasize;
	// Multicast address
	private InetAddress multicastAddress;
	// Multicast port
	private int multicastPort;
	// Timetolive
	@SuppressWarnings("unused")
	private int ttl;
	// Multicast socket
	private MulticastSocket socketSend;
	// Multicast socket
	private MulticastSocket socketReceive;

	// Multicast listening thread
	private MulticastListenerThread mcastListenerThread;

	private Map<String, SyncMsg> syncMsgCache;

	public MulticastRegistrySynchronizer(String mcastAddress, NetworkInterface netInterface, int port, int ttl, int maxdatasize) throws UnknownHostException {

		this.multicastAddress = InetAddress.getByName(mcastAddress);
		this.multicastPort = port;
		this.ttl = ttl;
		this.maxdatasize = maxdatasize;
		mcastListenerThread = new MulticastListenerThread(this);
		mcastListenerThread.setDaemon(true);
		try {

			socketSend = new MulticastSocket(port);
			if (netInterface != null) {
				socketSend.setNetworkInterface(netInterface);
				System.out.println(socketSend.getNetworkInterface());
			}
			socketSend.joinGroup(multicastAddress);

			socketReceive = new MulticastSocket(port);

			if (netInterface != null) {
				socketReceive.setNetworkInterface(netInterface);
			}
			socketReceive.joinGroup(multicastAddress);
			// System.out.println(" interface = "+socketSend.getInterface());

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		this.syncMsgCache = new HashMap<String, SyncMsg>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.RegistrySynchronizer#setSynchronizedRegistry(com .synaptix.registry.SynchronizedRegistry)
	 */
	@Override
	public void setSynchronizedRegistry(SynchronizedRegistry registry) {
		this.registry = registry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.RegistrySynchronizer#activate()
	 */
	@Override
	public void activate() {
		mcastListenerThread.start();
		// if (!mcastListenerThread.isAlive()){
		// mcastListenerThread.start();
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.RegistrySynchronizer#deactivate()
	 */
	@Override
	public void deactivate() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.RegistrySynchronizer#sendRequest(com.synaptix.registry .RegistryNode, java.lang.String)
	 */
	@Override
	public void sendRequest(String service) {
		SyncMsg msg = new SyncMsg(registry.getUID(), service, "_", SyncMsg.T_REQ);
		sendSyncMessage(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.RegistrySynchronizer#sendProviderReg(com.synaptix .registry.RegistryNode, java.lang.String, com.synaptix.registry.ServiceProvider)
	 */
	@Override
	public void sendProviderReg(String service, ServiceProvider provider) {
		if (provider.isLocal()) {
			SyncMsg msg = new SyncMsg(registry.getUID(), service, provider.getURI(), SyncMsg.T_REG);
			sendSyncMessage(msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.RegistrySynchronizer#sendServiceOverload(java.lang .String)
	 */
	@Override
	public void sendServiceOverload(String service) {
		SyncMsg msg = (SyncMsg) syncMsgCache.get("OL:" + service);
		if (msg == null) {
			msg = new SyncMsg(registry.getUID(), service, null, SyncMsg.T_OVERLOAD);
			syncMsgCache.put("OL:" + service, msg);
		}
		sendSyncMessage(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.RegistrySynchronizer#sendServiceUnderload(java. lang.String)
	 */
	@Override
	public void sendServiceUnderload(String service) {
		SyncMsg msg = (SyncMsg) syncMsgCache.get("UL:" + service);
		if (msg == null) {
			msg = new SyncMsg(registry.getUID(), service, null, SyncMsg.T_UNDERLOAD);
			syncMsgCache.put("UL:" + service, msg);
		}
		sendSyncMessage(msg);
		// SyncMsg msg =
		// new SyncMsg(registry.getUID(), service, null, SyncMsg.T_UNDERLOAD);
		// sendSyncMessage(msg);
	}

	@Override
	public void sendServiceIdling(String service) {
		SyncMsg msg = (SyncMsg) syncMsgCache.get("IDL:" + service);
		if (msg == null) {
			msg = new SyncMsg(registry.getUID(), service, null, SyncMsg.T_IDLING);
			syncMsgCache.put("IDL" + service, msg);
		}
		sendSyncMessage(msg);
	}

	@Override
	public void sendServiceBusy(String service) {
		SyncMsg msg = (SyncMsg) syncMsgCache.get("BZ:" + service);
		if (msg == null) {
			msg = new SyncMsg(registry.getUID(), service, null, SyncMsg.T_BUSY);
			syncMsgCache.put("BZ" + service, msg);
		}
		sendSyncMessage(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.RegistrySynchronizer#sendProviderUnreg(com.synaptix .registry.RegistryNode, java.lang.String, com.synaptix.registry.ServiceProvider)
	 */
	@Override
	public void sendProviderUnreg(String service, ServiceProvider provider) {
		SyncMsg msg = new SyncMsg(registry.getUID(), service, provider.getURI(), SyncMsg.T_UREG);
		sendSyncMessage(msg);
	}

	private void sendSyncMessage(SyncMsg msg) {
		// Datagramme pour émettre sur le groupe de multicast
		try {
			byte[] buffer = msg.getBytes();
			DatagramPacket datag = new DatagramPacket(buffer, buffer.length, multicastAddress, multicastPort);
			// int len = Math.min(buffer.length, maxdatasize);
			// datag.setData(buffer, 0, len);
			if (registry.getLogger() != null) {
				registry.getLogger().trace("Send message " + msg.toString() + " on " + socketSend.getLocalAddress().getHostName());
			}

			// System.out.println("\t### Sending "+msg.toString()+" on "+socketSend.getLocalAddress().getHostName());
			socketSend.send(datag);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void resendRegisteredProviders(String service) {
		registry.getLogger().trace("Sending providers for service " + service);
		// System.out.println("resending providers for "+service);
		List<ServiceProvider> p = registry.getProviders(service);
		if (p != null) {
			for (int i = 0; i < p.size(); i++) {
				ServiceProvider sp = (ServiceProvider) p.get(i);
				sendProviderReg(service, sp);
			}
		}
	}

	private static class MalformedSyncMsgException extends Exception {
		private static final long serialVersionUID = 1L;

		public MalformedSyncMsgException(String msg) {
			super(msg);
		}
	}

	public static class SyncMsg {
		public static final int T_REQ = 0;
		public static final int T_REG = 1;
		public static final int T_UREG = 2;
		public static final int T_OVERLOAD = 3;
		public static final int T_UNDERLOAD = 4;
		public static final int T_IDLING = 5;
		public static final int T_BUSY = 6;

		public static final int T_UNKNOWN = -1;
		public String registryUID;
		public String serviceID;
		public String providerURI;
		public int type;

		public SyncMsg(String src) throws MalformedSyncMsgException {
			try {
				if (src.startsWith("?")) {
					type = T_REQ;
				} else if (src.startsWith(">")) {
					type = T_REG;
				} else if (src.startsWith("<")) {
					type = T_UREG;
				} else if (src.startsWith("O")) {
					type = T_OVERLOAD;
				} else if (src.startsWith("U")) {
					type = T_UNDERLOAD;
				} else {
					type = T_UNKNOWN;
				}
				StringTokenizer st = new StringTokenizer(src.substring(1), "|", true);
				registryUID = st.nextToken();
				st.nextToken();
				serviceID = st.nextToken();
				st.nextToken();
				providerURI = st.nextToken();
			} catch (NoSuchElementException ex) {
				throw new MalformedSyncMsgException("message " + src + " mal formé : " + ex.toString());
			}
		}

		public SyncMsg(String registryUID, String serviceID, String providerURI, int type) {
			this.registryUID = registryUID;
			this.serviceID = serviceID;
			this.providerURI = providerURI;
			this.type = type;
		}

		public byte[] getBytes() throws UnsupportedEncodingException {
			String t = "_";
			switch (type) {
			case (T_REQ):
				t = "?";
				break;
			case (T_REG):
				t = ">";
				break;
			case (T_UREG):
				t = "<";
				break;
			case (T_OVERLOAD):
				t = "O";
				break;
			case (T_UNDERLOAD):
				t = "U";
				break;
			}
			return (t + registryUID + "|" + serviceID + "|" + providerURI).getBytes("8859_1");
		}

		@Override
		public String toString() {
			String t = "_";
			switch (type) {
			case (T_REQ):
				t = "?";
				break;
			case (T_REG):
				t = ">";
				break;
			case (T_UREG):
				t = "<";
				break;
			case (T_OVERLOAD):
				t = "O";
				break;
			case (T_UNDERLOAD):
				t = "U";
				break;
			}
			return (t + registryUID + "|" + serviceID + "|" + providerURI);
		}
	}

	public static class MulticastListenerThread extends Thread {
		MulticastRegistrySynchronizer synchronizer;

		public MulticastListenerThread(MulticastRegistrySynchronizer synchronizer) {
			super();
			this.synchronizer = synchronizer;
			setDaemon(true);
		}

		protected void displayNetworkDevice(PrintStream out) {
			Enumeration<NetworkInterface> nets;
			StringBuffer sb = new StringBuffer();
			sb.append("#############################################################################\n");
			sb.append("########################## MULTICAST NETWORK ERROR ##########################\n");
			sb.append("################## BIND 'synchronizer.mcast.interface' ######################\n");
			sb.append("################# ON VALID NETWORK NAME ADDRESS BELLOW ######################\n");
			sb.append("#############################################################################");
			try {
				nets = NetworkInterface.getNetworkInterfaces();
				for (NetworkInterface netint : Collections.list(nets)) {
					if (netint.isUp()) {
						sb.append(displayInterfaceInformation(netint));
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			}
			sb.append("\n\t--------------------------------------------------------------------\n");
			sb.append("#############################################################################\n");
			out.printf("%s", sb.toString());
		}

		protected String displayInterfaceInformation(NetworkInterface netint) throws SocketException {
			StringBuffer sb = new StringBuffer();
			sb.append("\n\t--------------------------------------------------------------------\n");
			sb.append("\tNetwork name: " + netint.getName() + "\n");
			sb.append("\t > Active: " + netint.isUp() + "\n");
			Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
			for (InetAddress inetAddress : Collections.list(inetAddresses)) {
				sb.append("\t  - InetAddress: " + inetAddress + "\n");
			}
			sb.append("\t > Display name: " + netint.getDisplayName());
			// sb.append("\n\t > Loopback: "+netint.isLoopback()+"\n");
			// sb.append("\t > PointToPoint: "+netint.isPointToPoint()+"\n");
			// sb.append("\t > Virtual: "+netint.isVirtual()+"");
			return sb.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// Zone de stockage
			byte[] buffer = new byte[synchronizer.maxdatasize];
			// Datagramme de réception
			DatagramPacket datag = new DatagramPacket(buffer, buffer.length);
			// En permanence, afficher ce qu'on reçoit
			while (true) {
				try {
					datag.setLength(synchronizer.maxdatasize);
					// System.out.println("\t### Recieve on : "+synchronizer.socketReceive);
					synchronizer.socketReceive.receive(datag);
					// System.out.println("############# "+datag);
					String msg = new String(buffer, 0, datag.getLength(), "8859_1");

					try {
						SyncMsg m = new SyncMsg(msg);
						if (synchronizer.registry.getLogger() != null) {
							synchronizer.registry.getLogger().trace(synchronizer.registry.getUID() + " <= '" + msg + "'(" + datag.getAddress() + ":" + datag.getPort() + ") [" + m + "]");
						} else {
							System.out.println(synchronizer.registry.getUID() + " <= '" + msg + "'(" + datag.getAddress() + ":" + datag.getPort() + ") [" + m + "]");
						}
						// System.out.print(synchronizer.registry.getUID()+" <= '"
						// );
						// System.out.print(msg);
						// System.out.println("' ("+
						// datag.getAddress() + ":" + datag.getPort()+") ");
						if (m.registryUID.equals(synchronizer.registry.getUID())) {
							if (synchronizer.registry.getLogger() != null) {
								synchronizer.registry.getLogger().trace("Local message ignored");
							} else {
								System.out.println("This is a local message, ignore");
							}

						} else {
							if (synchronizer.listen) {
								switch (m.type) {
								case (SyncMsg.T_REQ):
									if (synchronizer.registry.getLogger() != null) {
										synchronizer.registry.getLogger().trace("Servive request : " + m.serviceID);
									} else {
										System.out.println("Requesting service " + m.serviceID);
									}
									synchronizer.resendRegisteredProviders(m.serviceID);
									break;
								case (SyncMsg.T_REG):
									ServiceProvider sp = synchronizer.registry.getProviderProxy(m.providerURI);
									synchronizer.registry.registerProvider(m.serviceID, sp);
									if (synchronizer.registry.getLogger() != null) {
										synchronizer.registry.getLogger().trace("Service reg. : " + m.serviceID + " provided by " + m.providerURI);
									} else {
										System.out.println("Notifying service registration : " + m.serviceID + " provided by " + m.providerURI);
									}
									break;
								case (SyncMsg.T_UREG):
									if (synchronizer.registry.getLogger() != null) {
										synchronizer.registry.getLogger().trace("Service unreg. : " + m.serviceID + " provided by " + m.providerURI);
									} else {
										System.out.println("Notifying service unregistration : " + m.serviceID + " provided by " + m.providerURI);
									}
									break;
								case (SyncMsg.T_OVERLOAD):

									if (synchronizer.registry.getLogger() != null) {
										synchronizer.registry.getLogger().trace("Service overload. : " + m.serviceID + " provided by " + m.providerURI);
									} else {
										System.out.println("Notifying service overload : " + m.serviceID + " provided by " + m.providerURI);
									}
									synchronizer.registry.setServiceOverload(m.serviceID);
									break;
								case (SyncMsg.T_UNDERLOAD):
									if (synchronizer.registry.getLogger() != null) {
										synchronizer.registry.getLogger().trace("Service underload : " + m.serviceID + " provided by " + m.providerURI);
									} else {
										System.out.println("Notifying service underload : " + m.serviceID + " provided by " + m.providerURI);
									}
									synchronizer.registry.setServiceUnderload(m.serviceID);
									break;
								}
							}
						}
					} catch (MalformedSyncMsgException ex) {
						// System.out.println("This is not a registry synchronisation message ("+msg+")");
					}

				} catch (Exception e) {
					if (synchronizer.registry.getLogger() != null) {
						synchronizer.registry.getLogger().fatal("run()", e);
					} else {
						e.printStackTrace();
						displayNetworkDevice(System.err);
					}

					break;
				}
			}
		}
	}

}
