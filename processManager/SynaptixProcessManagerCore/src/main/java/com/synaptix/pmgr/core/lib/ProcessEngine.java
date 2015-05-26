package com.synaptix.pmgr.core.lib;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import nanoxml.XMLElement;

import org.apache.commons.logging.Log;

import com.synaptix.pmgr.core.apis.Engine;
import com.synaptix.pmgr.core.apis.PluggableChannel;
import com.synaptix.pmgr.core.lib.ProcessingChannel.Agent;
import com.synaptix.pmgr.registry.BaseSynchronizedRegistry;
import com.synaptix.pmgr.registry.MulticastRegistrySynchronizer;
import com.synaptix.pmgr.registry.RegistrySynchronizer;
import com.synaptix.pmgr.registry.SynchronizedRegistry;
import com.synaptix.toolkits.nanoxml.NanoXMLKit;
import com.synaptix.toolkits.properties.PropertiesKit;

public class ProcessEngine { // implements MessageHandler, RegistryListener{

	private static Engine engine;
	private static Map<String, AgentDef> agentDefs = new HashMap<String, AgentDef>();

	private static List<ProcessEngineListener> processEngineListeners = new ArrayList<ProcessEngineListener>();

	public static final String TYPE_PROCESSING = "processing";
	public static final String TYPE_PROCESSING_GROUP = "processing-group";

	public synchronized static Engine getInstance(String id) {
		return getInstance(id, null);
	}

	/**
	 * Retourne l'instance. Si on la crée, alors on utilise les propriétés données
	 *
	 * @param id
	 * @param classLoader
	 * @param properties
	 * @return
	 */
	public synchronized static Engine getInstance(String id, Properties properties) {
		// System.out.println("=======================================");
		// System.out.println(" Demarrage ProcessEngine");
		// try{
		// throw new Exception("Trace:");
		// }catch (Exception e) {
		// StackTraceElement[] s = e.getStackTrace();
		// System.out.println("\t<="+s[1]);
		// }
		// System.out.println("=======================================");

		if (engine == null) {
			engine = buildEngine(id, properties);
		}
		return engine;
	}

	public synchronized static Engine getInstance() {
		if (engine == null) {
			engine = buildEngine("", null);
		}
		return engine;
	}

	public synchronized static Engine getInstance(ClassLoader classLoader) {
		if (engine == null) {
			engine = buildEngine("", null);
		}
		return engine;
	}

	public static void handle(String channel, Object message) {
		getInstance().handle(channel, message);
	}

	public static void handleAll(String channel, Object message) {
		getInstance().handleAll(channel, message);
	}

	public static void plugChannel(PluggableChannel channel) {
		getInstance().plugChannel(channel, true);
	}

	public static void unplugChannel(String channelName) {
		getInstance().unplugChannel(channelName);
	}

	static int count = 0;

	protected static void displayNetworkDevice(PrintStream out) {
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

	protected static String displayInterfaceInformation(NetworkInterface netint) throws SocketException {
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

	private synchronized static Engine buildEngine(String id, Properties properties) {
		if (count > 0) {
			try {
				throw new Exception("BaseEngine crée 2 fois");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.exit(0);
		}

		if (properties == null) { // config interne
			properties = PropertiesKit.load("process_engine.properties", ProcessEngine.class, true);
			if (properties == null) {
				properties = PropertiesKit.load("process_engine_console.properties", ProcessEngine.class, true);
			}
		}
		if (properties != null) {
			count++;
			String registryRmiPort = properties.getProperty("engine" + id + ".rmiport", "1099");
			try {
				LocateRegistry.createRegistry(Integer.parseInt(registryRmiPort));
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			String registryMcastPort = properties.getProperty("registry" + id + ".synchronizer.mcast.port", "5000");
			String registryMcastGrp = properties.getProperty("registry" + id + ".synchronizer.mcast.group", "224.0.0.1");
			String registryMcastInterface = properties.getProperty("registry" + id + ".synchronizer.mcast.interface", null);
			String registryMcastDatasize = properties.getProperty("registry" + id + ".synchronizer.mcast.datasize", "256");
			String engineBindAddr = properties.getProperty("engine" + id + ".bindaddress", null);
			InetAddress addr = null;
			NetworkInterface netinterface = null;
			if (registryMcastInterface != null) {
				try {
					netinterface = NetworkInterface.getByName(registryMcastInterface);

					// System.out.println(netinterface);
				} catch (SocketException e) {
					e.printStackTrace();
				}
			}
			try {
				if (netinterface != null && !netinterface.isUp()) {
					displayNetworkDevice(System.err);
					return null;
				}
			} catch (SocketException e1) {
				e1.printStackTrace();
			}
			if (engineBindAddr != null) {
				try {
					addr = InetAddress.getByName(engineBindAddr);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			if (addr == null) {
				try {
					addr = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			RegistrySynchronizer synch = null;
			if (addr != null) {
				try {
					synch = new MulticastRegistrySynchronizer(registryMcastGrp, netinterface, Integer.parseInt(registryMcastPort), 100, Integer.parseInt(registryMcastDatasize));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}

			String ruid = properties.getProperty("registry" + id + ".uid", "R_" + id);

			SynchronizedRegistry reg = new BaseSynchronizedRegistry(ruid, synch);
			// new BaseSynchronizedRegistry(ruid, null);
			// reg.setSynchronizer(synch);

			String errorPath = properties.getProperty("engine" + id + ".channels.rmi.errorpath", "/tmp/channels");
			engine = new BaseEngine("E_" + id, addr, new File(errorPath), reg, Integer.parseInt(registryRmiPort));

			// String plugconf = properties.getProperty("engine" + id + ".plugconf", null);
			// if (plugconf != null) {
			// String[] files = plugconf.split(",");
			// for (int i = 0; i < files.length; i++) {
			// initPlugs(files[i].trim());
			// }
			// }

			engine.activateChannels();
			synch.listen();
		}

		return engine;
	}

	public static void initPlugs(File plugconf) {
		XMLElement conf = NanoXMLKit.load(plugconf, true);
		if (conf != null) {
			// Map agentDefs=new HashMap();
			parseConf(conf, agentDefs);
		}
	}

	public static void initPlugs(InputStream plugconf) {
		XMLElement conf = NanoXMLKit.load(plugconf, true);
		if (conf != null) {
			// Map agentDefs=new HashMap();
			parseConf(conf, agentDefs);
		}
	}

	public static void initPlugs(String plugconf) {
		XMLElement conf = NanoXMLKit.load(plugconf, ProcessEngine.class, true);
		if (conf != null) {
			System.out.println("init plugs : " + plugconf);
			parseConf(conf, agentDefs);
		}
	}

	@SuppressWarnings("unchecked")
	private static void parseConf(XMLElement conf, Map<String, AgentDef> agentDefs) {
		for (Enumeration<XMLElement> e = conf.enumerateChildren(); e.hasMoreElements();) {
			XMLElement elem = e.nextElement();
			if (elem.getName().equals("agents")) {
				parseAgentsConf(elem);
			}
			if (elem.getName().equals("channels")) {
				parseChannelsConf(elem);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void parseAgentsConf(XMLElement conf) {
		for (Enumeration<XMLElement> e = conf.enumerateChildren(); e.hasMoreElements();) {
			XMLElement elem = e.nextElement();
			if (elem.getName().equals("agent")) {
				String id = elem.getStringAttribute("id");
				String cls = elem.getStringAttribute("class");
				String desc = elem.getStringAttribute("desc", "-");
				getInstance().getLogger().info("Declaring agent " + id + " (" + cls + ")");
				// System.out.println("Declaring agent " + id + " (" + cls + ")");
				List<String> parameters = new ArrayList<String>();
				for (Enumeration<XMLElement> ep = elem.enumerateChildren(); ep.hasMoreElements();) {
					XMLElement p = ep.nextElement();
					if (p.getName().equals("arg")) {
						parameters.add(p.getContent());
					}
				}
				try {
					addAgentDef(new AgentDef(id, desc, resolveAgent(parameters.toArray(new String[0]), cls)));
				} catch (ClassNotFoundException e1) {
					if ((ProcessEngine.getInstance() != null) && (ProcessEngine.getInstance().getLogger() != null)) {
						ProcessEngine.getInstance().getLogger().error(e1);
					} else {
						e1.printStackTrace();
					}
				} catch (Exception e1) {
					if ((ProcessEngine.getInstance() != null) && (ProcessEngine.getInstance().getLogger() != null)) {
						ProcessEngine.getInstance().getLogger().error(e1);
					} else {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public static ProcessingChannel.Agent resolveAgent(String[] parameters, String className) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		Class<? extends Agent> agentClass = (Class<? extends Agent>) ProcessEngine.class.getClassLoader().loadClass(className);
		Agent agent = null;
		Constructor<?> constructor = null;
		if ((parameters != null) && (parameters.length > 0)) {
			Class<?>[] sig = new Class[parameters.length];
			for (int i = 0; i < sig.length; i++) {
				sig[i] = String.class;
			}
			constructor = agentClass.getConstructor(sig);
		}
		if ((parameters != null) && (parameters.length > 0)) {
			agent = (ProcessingChannel.Agent) constructor.newInstance(parameters);
		} else {
			agent = agentClass.newInstance();
		}
		return agent;
	}

	@SuppressWarnings("unchecked")
	private static void parseChannelsConf(XMLElement conf) {
		for (Enumeration<XMLElement> e = conf.enumerateChildren(); e.hasMoreElements();) {
			XMLElement elem = e.nextElement();
			if (elem.getName().equals("channel")) {
				String id = elem.getStringAttribute("id");
				String type = elem.getStringAttribute("type");
				if (type.equals(TYPE_PROCESSING_GROUP)) {
					List<PluggableChannel> mappedQueues = buildChannelGroup(elem, id, type);
					if (mappedQueues != null && mappedQueues.size() > 0) {
						for (PluggableChannel chnl : mappedQueues) {
							if (chnl != null) {
								engine.plugChannel(chnl, true);
							}
						}
					}
				} else {
					PluggableChannel chnl = buildChannel(elem, id, type);
					if (chnl != null) {
						engine.plugChannel(chnl, true);
					}
				}
			}
		}
	}

	private static List<PluggableChannel> buildChannelGroup(XMLElement conf, String id, String type) {
		if (type.equals(TYPE_PROCESSING_GROUP)) {
			Map<String, String> params = parseParameters(conf);

			AgentDef agt = agentDefs.get(params.get("agent"));
			int max_channels = Integer.parseInt(params.get("max_working"));
			int max_working = 1;
			int max_waiting = Integer.parseInt(params.get("max_waiting"));
			List<PluggableChannel> channels = new ArrayList<PluggableChannel>();
			for (int i = 0; i < max_channels; i++) {
				if (agt != null) {
					try {
						String idChannel = id + "_" + i;
						PluggableChannel chn = new ProcessingChannel(idChannel, max_working, max_waiting, agt.getAgent(), engine);
						channels.add(chn);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
			if (agt != null) {
				try {
					String idChannel = id + "_DEFAULT";
					PluggableChannel chn = new ProcessingChannel(idChannel, max_working, max_waiting, agt.getAgent(), engine);
					channels.add(chn);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}

			params.clear();
			if (channels != null) {
				return channels;

			}
		}
		return null;
	}

	private static PluggableChannel buildChannel(XMLElement conf, String id, String type) {
		if (type.equals(TYPE_PROCESSING)) {
			Map<String, String> params = parseParameters(conf);

			AgentDef agt = agentDefs.get(params.get("agent"));
			PluggableChannel chn = null;
			if (agt != null) {
				try {
					chn = new ProcessingChannel(id, Integer.parseInt(params.get("max_working")), Integer.parseInt(params.get("max_waiting")), agt.getAgent(), engine);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}

			params.clear();
			if (chn != null) {
				return chn;

			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, String> parseParameters(XMLElement conf) {
		Map<String, String> m = new HashMap<String, String>();
		for (Enumeration<XMLElement> e = conf.enumerateChildren(); e.hasMoreElements();) {
			XMLElement elem = e.nextElement();
			if (elem.getName().equals("param")) {
				String name = elem.getStringAttribute("name");
				String value = elem.getStringAttribute("value");
				m.put(name, value);
			}
		}
		return m;
	}

	public static Map<String, AgentDef> getAgentDefs() {
		return agentDefs;
	}

	public static void addAgentDef(AgentDef agtDef) {
		agentDefs.put(agtDef.id, agtDef);
		fireAgentDefsChange();
	}

	public static void addListener(ProcessEngineListener listener) {
		processEngineListeners.add(listener);
	}

	public static void removeListener(ProcessEngineListener listener) {
		processEngineListeners.remove(listener);
	}

	public static void fireAgentDefsChange() {
		for (int i = 0; i < processEngineListeners.size(); i++) {
			processEngineListeners.get(i).notifyAgentDefsChange();
		}
	}

	public static interface ProcessEngineListener {
		public void notifyAgentDefsChange();
	}

	public static class AgentDef {
		private String id;
		private String desc;
		private Agent agent;

		public AgentDef(String id, String desc, Agent agent) {
			this.id = id;
			this.desc = desc;
			this.agent = agent;
		}

		public String getID() {
			return id;
		}

		public String getDesc() {
			return desc;
		}

		public Agent getAgent() {
			return agent;
		}
	}

	public static void setLogger(Log logger) {
		getInstance().setLogger(logger);
	}

	public static void shutdown() {
		try {
			getInstance().shutdown();
			count--;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			System.out.println("ProcessEngine shutdown : le service est déjà désactivé. Opération ignorée.");
		}
	}
}
