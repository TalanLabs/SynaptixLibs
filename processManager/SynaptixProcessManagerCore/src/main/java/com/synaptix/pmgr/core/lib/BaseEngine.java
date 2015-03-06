package com.synaptix.pmgr.core.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.synaptix.pmgr.core.apis.Channel;
import com.synaptix.pmgr.core.apis.ChannelSlot;
import com.synaptix.pmgr.core.apis.DelayedHandleReport;
import com.synaptix.pmgr.core.apis.Engine;
import com.synaptix.pmgr.core.apis.EngineListener;
import com.synaptix.pmgr.core.apis.HandleReport;
import com.synaptix.pmgr.core.apis.PluggableChannel;
import com.synaptix.pmgr.core.apis.RemoteMessageHandler;
import com.synaptix.pmgr.core.lib.handlereport.BaseDelayedHandleReport;
import com.synaptix.pmgr.registry.BaseServiceProvider;
import com.synaptix.pmgr.registry.Registry;
import com.synaptix.pmgr.registry.RegistryListener;
import com.synaptix.pmgr.registry.ServiceProvider;
import com.synaptix.pmgr.registry.SynchronizedRegistry;

public class BaseEngine implements Engine, RegistryListener {

	static int count = 0;

	private EngineListener listener;
	private long maxMsgAge;
	private int maxMsgCount;
	private RemoteMessageHandler remoteMsgHandler;
	private int registryRmiPort;

	private String uid;
	private InetAddress bindAddress;
	private SynchronizedRegistry registry;

	private final Map<String, ChannelSlot> channelslots;

	private final Map<String, List<ChannelSlot>> channelSlotsGroup;
	// private RemoteMessageHandler remoteMsgHandler;

	private Log logger;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Engine#setLogger(java.util.logging.Logger)
	 */
	@Override
	public void setLogger(Log logger) {
		// System.out.println("************** Set logger !!!!! ***************");

		if (logger == null) {
			System.exit(0);
		}
		if (this.logger != null) {
			try {
				throw new Exception("Logger appelé 2 fois avec " + logger);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.exit(0);
		}
		this.logger = logger;
		this.registry.setLogger(logger);
		getLogger().info("Logger installé");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Engine#getLogger()
	 */
	@Override
	public Log getLogger() {
		return logger;
	}

	private File errorPath;

	public BaseEngine(String uid, InetAddress bindAddress, File errorPath, SynchronizedRegistry registry, int registryRmiPort) {
		if (getLogger() != null) {
			getLogger().info("****************** CREATION PROCESS ENGINE ************************");
		} else {
			System.out.println("****************** CREATION PROCESS ENGINE ************************");
		}

		count++;
		if (count > 1) {
			try {
				throw new Exception("BaseEngine crée 2 fois");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.exit(0);
		}
		this.uid = uid;
		this.registryRmiPort = registryRmiPort;
		this.bindAddress = bindAddress;
		this.errorPath = errorPath;
		if (!this.errorPath.exists()) {
			this.errorPath.mkdirs();
		}
		this.registry = registry;

		this.channelslots = new HashMap<String, ChannelSlot>();
		this.channelSlotsGroup = new HashMap<String, List<ChannelSlot>>();

		registry.addListener(this);
		listener = new EngineListener() {
			@Override
			public void notifyHandle(String channelName, Object message) {
			}

			@Override
			public void notifyNewSlot(ChannelSlot slot) {
			}

			@Override
			public void notifySlotPlug(ChannelSlot slot) {
			}

			@Override
			public void notifySlotUnplug(ChannelSlot slot) {
			}

			@Override
			public void notifySlotBuffering(ChannelSlot slot, Object message) {
			}

			@Override
			public void notifySlotFlushing(ChannelSlot slot, Object message) {
			}

			@Override
			public void notifySlotTrashing(ChannelSlot slot, Object message) {
			}
		};
		init();
	}

	private void init() {
		channelslots.clear();
		channelSlotsGroup.clear();
		maxMsgAge = 86400000; // 24 hours
		maxMsgCount = 10;
		// create a remote acces to this processengine.
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new PESecurityManager());
		}
		try {
			final String bindname = getRemoteBindName();
			remoteMsgHandler = new BaseRemoteMessageHandler(BaseEngine.this);
			// UnicastRemoteObject.exportObject(remoteMsgHandler);
			System.err.println("******" + bindname);

			Naming.rebind(bindname, remoteMsgHandler);
			if (logger != null) {
				logger.info("ProcessEngine bound on " + bindname);
			}
		} catch (Exception e) {
			if (logger != null) {
				logger.fatal("init()", e);
			} else {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void shutdown() throws RemoteException, MalformedURLException, NotBoundException {
		// System.out.println("ProcessEngine shutdown : désactivation du service en cours.");
		getLogger().info("ProcessEngine désactivation du service en cours.");
		Naming.unbind(getRemoteBindName());
		if (channelslots != null && channelslots.size() > 0) {
			for (String key : channelslots.keySet()) {
				ChannelSlot channelSlot = channelslots.get(key);
				if (!channelSlot.isPlugged() && channelSlot.getBufferedMessageCount() > 0) {
					int cpt = 1;
					for (Object msg : channelSlot.getBufferedMessage()) {
						channelSlot.storeBufferedMessage(errorPath.getAbsolutePath() + "/" + channelSlot.getName(), msg, cpt);
						cpt++;
					}
				}
			}
		}
		UnicastRemoteObject.unexportObject(remoteMsgHandler, true);
		getLogger().info("ProcessEngine shutdown");
	}

	@Override
	public String getRemoteBindName() {
		return "//" + bindAddress.getHostAddress() + ":" + registryRmiPort + "/" + uid;
	}

	/**
	 * called by the network synchronisation system when a channel reference is declared.
	 * 
	 * @param channel
	 */
	@Override
	public void plugChannel(PluggableChannel channel, boolean propagateRegistry) {
		// System.out.println("Plug channel :"+channel);
		if (logger != null) {
			logger.info("Plug channel " + channel.getName());
		}

		ChannelSlot channelSlot = resolveChannelSlot(channel.getName());
		channelSlot.plugChannel(channel);
		if (propagateRegistry) {
			registry.registerProvider(channel.getName(), new BaseServiceProvider(getRemoteBindName(), channel.isLocal()));
		}
	}

	@Override
	public void unplugChannel(String channelName) {
		ChannelSlot channelSlot = resolveChannelSlot(channelName);
		channelSlot.unplugChannel();
	}

	@Override
	public void declareGroup(String name, List<PluggableChannel> channels) {
		List<ChannelSlot> channelSlotList = new ArrayList<ChannelSlot>();
		if ((channels != null) && (channels.size() > 1)) {
			for (PluggableChannel pluggableChannel : channels) {
				ChannelSlot channelSlot = resolveChannelSlot(pluggableChannel.getName());
				if (channelSlot != null) {
					channelSlotList.add(channelSlot);
				}
			}
		}
		channelSlotsGroup.put(name, channelSlotList);
	}

	/**
	 * Called by the send method to return the ChannelSlot plugged (or to be plugged) on a named channel
	 * 
	 * @param channelName
	 *            name of the channel.
	 * @return ChannelSlot plugged or to be plugged to the channel.
	 */
	private ChannelSlot resolveChannelSlot(String channelName) {
		synchronized (channelslots) {
			ChannelSlot channelslot = channelslots.get(channelName);
			if (channelslot != null) {
				return channelslot;
			} else {
				// This channelslot could not be found, create one ...
				channelslot = new ChannelSlotImpl(channelName, maxMsgAge, maxMsgCount, this);
				if (logger != null) {
					logger.info(String.format("Couldn't find %s, creating one", channelName));
				}
				channelslots.put(channelName, channelslot);
				listener.notifyNewSlot(channelslot);
				return channelslot;
			}
		}
	}

	/**
	 * Returns the list of channel slots associated to given group (null if the group doesn't exist)
	 * 
	 * @param channelGroupName
	 * @return
	 */
	private List<ChannelSlot> getChannelSlotsForGroup(String channelGroupName) {
		return channelSlotsGroup.get(channelGroupName);
	}

	/**
	 * Send a message through a named channel.
	 * 
	 * @param channel
	 * @param message
	 */
	@Override
	public HandleReport handle(String channelName, Object message) {
		HandleReport report = null;
		ChannelSlot channel = resolveChannelSlot(channelName);
		if (channel != null) {
			listener.notifyHandle(channelName, message);
			report = channel.acceptMessage(message);
		}
		return report;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.MessageHandler#handleAll(java.lang.String, java.lang.Object)
	 */
	@Override
	public void handleAll(String channelPrefix, Object message) {
		synchronized (channelslots) {
			Iterator<String> it = channelslots.keySet().iterator();
			while (it.hasNext()) {
				String n = it.next();
				if (n.startsWith(channelPrefix)) {
					handle(n, message);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.MessageHandler#handleIfLocal(java.lang.String, java.lang.Object)
	 */
	@Override
	public HandleReport handleIfLocal(String channelName, Object message) {
		HandleReport report = null;
		ChannelSlot channel = resolveChannelSlot(channelName);
		if ((channel != null) && (channel.isLocal())) {
			listener.notifyHandle(channelName, message);
			report = channel.acceptMessage(message);
		} else {
			if (logger != null) {
				logger.warn("Proxying remote messages is forbidden (" + channelName + " is not a local channel)");
			}
		}
		return report;
	}

	/**
	 * called by a ChannelSlot when in need of a channel reference.
	 * 
	 * @param channelName
	 *            name of the needed channel reference.
	 */
	private void askForChannel(String channelName) {
		registry.getSynchronizer().sendRequest(channelName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.MessageHandler#isAvailable(java.lang.String)
	 */
	@Override
	public boolean isAvailable(String channelName) {
		boolean available = false;
		List<ChannelSlot> channelSlotList = getChannelSlotsForGroup(channelName);
		if ((channelSlotList != null) && (channelSlotList.size() > 0)) {
			Iterator<ChannelSlot> ite = channelSlotList.iterator();
			while ((ite.hasNext()) && (!available)) {
				available = ite.next().isAvailable();
			}
		} else {
			ChannelSlot channel = resolveChannelSlot(channelName);
			if ((channel != null) && (channel.getPluggedChannel() != null)) {
				available = channel.isAvailable();
			}
		}

		return available;
	}

	@Override
	public boolean isBusy(String channelName) {
		boolean busy = false;
		List<ChannelSlot> channelSlotList = getChannelSlotsForGroup(channelName);
		if ((channelSlotList != null) && (channelSlotList.size() > 0)) {
			Iterator<ChannelSlot> ite = channelSlotList.iterator();
			while ((ite.hasNext()) && (!busy)) {
				busy = ite.next().isBusy();
			}
		} else {
			ChannelSlot channel = resolveChannelSlot(channelName);
			if ((channel != null) && (channel.getPluggedChannel() != null)) {
				busy = channel.isBusy();
			}
		}

		return busy;
	}

	@Override
	public boolean isOverloaded(String channelName) {
		ChannelSlot channel = resolveChannelSlot(channelName);
		boolean overload = false;
		if ((channel != null) && (channel.getPluggedChannel() != null)) {
			overload = channel.getPluggedChannel().isOverloaded();
		} else {
			List<ChannelSlot> channelSlotList = getChannelSlotsForGroup(channelName);
			if ((channelSlotList != null) && (channelSlotList.size() > 0)) {
				Iterator<ChannelSlot> ite = channelSlotList.iterator();
				while ((ite.hasNext()) && (!overload)) {
					ChannelSlot channelSlot = ite.next();
					if (channelSlot.getPluggedChannel() != null) {
						overload = channelSlot.getPluggedChannel().isOverloaded();
					}
				}
			}
		}

		return overload;
	}

	public class ChannelSlotImpl extends AbstractChannel implements ChannelSlot {
		private PluggableChannel channel;
		@SuppressWarnings("unused")
		private long maxMsgAge;
		private int maxMsgCount;
		private List<Object> savedMessages;

		public ChannelSlotImpl(String name, long maxMsgAge, int maxMsgCount, Engine engine) {
			super(name, engine);
			savedMessages = new ArrayList<Object>();
			this.maxMsgAge = maxMsgAge;
			this.maxMsgCount = maxMsgCount;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.synaptix.pmgr2.apis.ChannelSlot#getLogger()
		 */
		@Override
		public Log getLogger() {
			return logger;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.synaptix.pmgr2.apis.Channel#acceptMessage(java.lang.Object)
		 */
		@Override
		public synchronized HandleReport acceptMessage(Object message) {
			if (channel != null) {
				if (channel.isAvailable()) {
					return channel.acceptMessage(message, this);
				}
			} else {
				// No channel plugged on this slot.
				// ask for network notification.
				askForChannel(getName());
			}
			saveMessage(message);
			return new BaseDelayedHandleReport("0", this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.synaptix.pmgr2.apis.Channel#isAvailable()
		 */
		@Override
		public boolean isAvailable() {
			if (channel != null) {
				return channel.isAvailable();
			}
			return false;
		}

		@Override
		public void setAvailable(boolean available) {
			if (channel != null) {
				channel.setAvailable(available);
			}
		}

		@Override
		public int getNbWorking() {
			if (channel != null) {
				return channel.getNbWorking();
			}
			return -1;
		}

		@Override
		public int getNbWaiting() {
			if (channel != null) {
				return channel.getNbWaiting();
			}
			return -1;
		}

		@Override
		public void clearSavedMessages() {
			synchronized (savedMessages) {
				savedMessages.clear();
			}
		}

		private synchronized void saveMessage(Object message) {
			synchronized (savedMessages) {
				if (savedMessages.size() < maxMsgCount) {
					listener.notifySlotBuffering(this, message);
					savedMessages.add(message);
				} else {
					if (savedMessages.size() > 0) {
						listener.notifySlotTrashing(this, savedMessages.remove(0));

						listener.notifySlotBuffering(this, message);
						savedMessages.add(message);
					}
				}
			}
		}

		@Override
		public synchronized void plugChannel(PluggableChannel channel) {
			this.channel = channel;
			listener.notifySlotPlug(this);
			while (savedMessages.size() > 0) {
				Object msg = savedMessages.remove(0);
				listener.notifySlotFlushing(this, msg);
				HandleReport report = channel.acceptMessage(msg, this);
				if (report instanceof DelayedHandleReport) {
					listener.notifySlotBuffering(this, msg);
					savedMessages.add(0, msg);
					break;
				}
			}
		}

		@Override
		public synchronized void unplugChannel() {
			this.channel = null;
			listener.notifySlotUnplug(this);
		}

		@Override
		public boolean isPlugged() {
			return (channel != null);
		}

		@Override
		public PluggableChannel getPluggedChannel() {
			return channel;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.synaptix.pmgr2.apis.Channel#isLocal()
		 */
		@Override
		public boolean isLocal() {
			if (channel != null) {
				return channel.isLocal();
			}
			return true;
		}

		@Override
		public int getBufferedMessageCount() {
			return savedMessages.size();
		}

		@Override
		public List<Object> getBufferedMessage() {
			return savedMessages;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.synaptix.pmgr2.apis.Channel#activate()
		 */
		@Override
		public void activate() {
			if (channel != null) {
				channel.activate();
			}
		}

		@Override
		public void storeBufferedMessage(String filebase, Object message, int cpt) {
			if (!(new File(filebase)).exists()) {
				(new File(filebase)).mkdirs();
			}
			File stored = new File(filebase, Long.toString(System.currentTimeMillis()) + "." + cpt + ".queued");
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(stored));
				try {
					oos.writeObject(message);
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					try {
						oos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean isBusy() {
			if (channel != null) {
				return channel.isBusy();
			}
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.RegistryListener#notifyProviderReg(com.synaptix .registry.Registry, java.lang.String, com.synaptix.registry.ServiceProvider)
	 */
	@Override
	public void notifyProviderReg(Registry registry, String serviceid, ServiceProvider provider) {
		if (logger != null) {
			logger.info("Registry notified a new provider for " + serviceid + " : " + provider.getURI());
		}
		if (getRemoteBindName().equals(provider.getURI())) {
			if (logger != null) {
				logger.info("local provider is ignored");
			} else {
				System.out.println("\t=>local provider is ignored");
			}
		} else {
			PluggableChannel ch = new RMIChannelProxy(serviceid, provider.getURI(), new File(errorPath, serviceid), this);
			plugChannel(ch, false);
			if (logger != null) {
				logger.info("remote provider " + provider.getURI() + " service " + serviceid);
			} else {
				System.out.println("remote provider " + provider.getURI() + " service " + serviceid);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.RegistryListener#notifyServiceOverload(com.synaptix .registry.Registry, java.lang.String)
	 */
	@Override
	public void notifyServiceOverload(Registry registry, String serviceid) {
		ChannelSlot slot = resolveChannelSlot(serviceid);
		if (slot != null) {
			PluggableChannel pc = slot.getPluggedChannel();
			if (pc != null) {
				if (pc instanceof RMIChannelProxy) {
					((RMIChannelProxy) pc).remoteIsOverLoaded();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.RegistryListener#notifyServiceUnderload(com.synaptix .registry.Registry, java.lang.String)
	 */
	@Override
	public void notifyServiceUnderload(Registry registry, String serviceid) {
		ChannelSlot slot = resolveChannelSlot(serviceid);
		if (slot != null) {
			PluggableChannel pc = slot.getPluggedChannel();
			if (pc != null) {
				if (pc instanceof RMIChannelProxy) {
					((RMIChannelProxy) pc).remoteIsUnderLoaded();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.RegistryListener#notifyProviderUnreg(com.synaptix .registry.Registry, java.lang.String, com.synaptix.registry.ServiceProvider)
	 */
	@Override
	public void notifyProviderUnreg(Registry registry, String serviceid, ServiceProvider provider) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.synaptix.pmgr2.apis.Engine#setListener(com.synaptix.pmgr2.apis. EngineListener)
	 */
	@Override
	public void setListener(EngineListener listener) {
		this.listener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Engine#getChannels()
	 */
	@Override
	public Collection<ChannelSlot> getChannels() {
		synchronized (channelslots) {
			return channelslots.values();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Engine#getRegistry()
	 */
	@Override
	public SynchronizedRegistry getRegistry() {
		return registry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Engine#activateChannels()
	 */
	@Override
	public void activateChannels() {
		synchronized (channelslots) {
			Iterator<ChannelSlot> it = channelslots.values().iterator();
			while (it.hasNext()) {
				Channel ch = it.next();
				ch.activate();
			}
		}
	}

	@Override
	public void handleMessageOnStratUp() {
		File[] chnl = errorPath.listFiles();
		for (int i = 0; i < chnl.length; i++) {
			File c = chnl[i];
			File[] fails = c.listFiles();
			for (int j = 0; j < fails.length; j++) {
				File f = fails[j];
				if (f.isFile()) {
					try {
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
						try {
							Object message = ois.readObject();
							handle(c.getName(), message);
						} catch (ClassNotFoundException ex) {
							ex.printStackTrace();
						} catch (IOException ex) {
							ex.printStackTrace();
						} finally {
							try {
								ois.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					f.delete();
				}
			}
		}
	}
}
