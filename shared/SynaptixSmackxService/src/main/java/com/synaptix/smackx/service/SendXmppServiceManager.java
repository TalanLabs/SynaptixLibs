package com.synaptix.smackx.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;

import com.synaptix.client.service.IReWaitResult;
import com.synaptix.client.service.IServiceMessageRecveid;
import com.synaptix.client.service.IServiceMessageRecveidListener;
import com.synaptix.client.service.SendServiceMessageException;
import com.synaptix.service.ServiceException;
import com.synaptix.smackx.service.packet.RequestServiceCallbackIQ;
import com.synaptix.smackx.service.packet.RequestServiceIQ;
import com.synaptix.smackx.service.packet.ResultServiceIQ;

public class SendXmppServiceManager {

	private static final Log log = LogFactory.getLog(SendXmppServiceManager.class);

	private static final Map<XMPPConnection, SendXmppServiceManager> instances = new WeakHashMap<XMPPConnection, SendXmppServiceManager>();

	public static SendXmppServiceManager getInstance(final XMPPConnection connection) {
		if (connection == null) {
			return null;
		}
		synchronized (instances) {
			SendXmppServiceManager manager = instances.get(connection);
			if (manager == null) {
				manager = new SendXmppServiceManager(connection);
				instances.put(connection, manager);
			}

			return manager;
		}
	}

	private final XMPPConnection connection;

	private IReWaitResult defaultReWaitResult;

	private long defaultTimeout;

	private List<IServiceMessageRecveidListener> listenerList;

	public SendXmppServiceManager(XMPPConnection connection) {
		this.connection = connection;
		initialize();
	}

	private void initialize() {
		listenerList = new ArrayList<IServiceMessageRecveidListener>();
		defaultTimeout = 60000;
	}

	public void addServiceMessageRecveidListener(IServiceMessageRecveidListener l) {
		synchronized (listenerList) {
			listenerList.add(l);
		}
	}

	public void removeServiceMessageRecveidListener(IServiceMessageRecveidListener l) {
		synchronized (listenerList) {
			listenerList.remove(l);
		}
	}

	protected void fireMessageRecveidChanged(IServiceMessageRecveid m) {
		synchronized (listenerList) {
			for (IServiceMessageRecveidListener l : listenerList) {
				l.messageRecveidChanged(m);
			}
		}
	}

	protected void fireMessageRecveidAdded(IServiceMessageRecveid m) {
		synchronized (listenerList) {
			for (IServiceMessageRecveidListener l : listenerList) {
				l.messageRecveidAdded(m);
			}
		}
	}

	public void setDefaultReWaitResult(IReWaitResult reWaitResult) {
		this.defaultReWaitResult = reWaitResult;
	}

	public IReWaitResult getDefaultReWaitResult() {
		return defaultReWaitResult;
	}

	public void setDefaultTimeout(long defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

	public long getDefaultTimeout() {
		return defaultTimeout;
	}

	public Object sendMessage(String serviceFactoryName, String serviceName, String methodName, Class<?>[] argTypes, Object[] args) throws Exception {
		return sendMessage(serviceFactoryName, serviceName, methodName, argTypes, args, defaultTimeout, defaultReWaitResult);
	}

	public Object sendMessage(String serviceFactoryName, String serviceName, String methodName, Class<?>[] argTypes, Object[] args, long timeout) throws Exception {
		return sendMessage(serviceFactoryName, serviceName, methodName, argTypes, args, timeout, defaultReWaitResult);
	}

	public Object sendMessage(String serviceFactoryName, String serviceName, String methodName, Class<?>[] argTypes, Object[] args, long timeout, IReWaitResult reWaitResult) throws Exception {
		RequestServiceIQ iq = new RequestServiceIQ();
		String currentID = iq.getPacketID();

		log.debug("Send message currentID : " + currentID + " serviceFactoryName : " + serviceFactoryName + " serviceName : " + serviceName + " methodName : " + methodName + " argTypes : "
				+ Arrays.toString(argTypes) + " timeout : " + timeout);

		iq.setServiceFactoryName(serviceFactoryName);
		iq.setServiceName(serviceName);
		iq.setMethodName(methodName);
		iq.setArgTypes(argTypes);
		iq.setArgs(args);

		ServiceMessageRecveid messageServiceLaunch = new ServiceMessageRecveid();
		messageServiceLaunch.setServiceFactoryName(serviceFactoryName);
		messageServiceLaunch.setServiceName(serviceName);
		messageServiceLaunch.setMethodName(methodName);
		messageServiceLaunch.setArgs(args);
		messageServiceLaunch.setArgTypes(argTypes);
		messageServiceLaunch.setStartDate(null);
		messageServiceLaunch.setEndDate(null);
		messageServiceLaunch.setError(null);

		fireMessageRecveidAdded(messageServiceLaunch);

		Object res = null;

		PacketCollector collector = connection.createPacketCollector(new AndFilter(new PacketTypeFilter(ResultServiceIQ.class), new PacketIDFilter(currentID)));

		ServiceCallbackPacketListener serviceCallbackPacketListener = new ServiceCallbackPacketListener(argTypes, args);

		// On s'inscrit pour recup packet pour le callback
		connection.addPacketListener(serviceCallbackPacketListener, new AndFilter(new PacketTypeFilter(RequestServiceCallbackIQ.class), new PacketIDFilter(currentID)));

		log.debug("Send packet IQ : " + iq.toXML());
		connection.sendPacket(iq);

		messageServiceLaunch.setStartDate(new Date());
		fireMessageRecveidChanged(messageServiceLaunch);

		log.debug("Wait response packet currentID : " + currentID);
		ResultServiceIQ msg = null;
		boolean isWaitEncore = true;
		try {
			while (isWaitEncore && msg == null) {
				msg = (ResultServiceIQ) collector.nextResult(timeout);
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
				if (msg == null) {
					isWaitEncore = reWaitResult != null ? reWaitResult.isReWaitResult(timeout) : false;
				}
			}
		} finally {
			collector.cancel();

			// On desinscrit pour recup packet pour le callback
			connection.removePacketListener(serviceCallbackPacketListener);
		}

		if (msg != null) {
			log.debug("Recveid response packet currentID : " + currentID);

			messageServiceLaunch.setEndDate(new Date());

			res = msg.getResult();
			if (res instanceof ServiceException) {
				ServiceException e = (ServiceException) res;

				messageServiceLaunch.setError(e.getMessage());
				fireMessageRecveidChanged(messageServiceLaunch);

				throw e;
			} else if (res instanceof Exception) {
				Exception e = (Exception) res;
				messageServiceLaunch.setError(e.getMessage());

				fireMessageRecveidChanged(messageServiceLaunch);

				throw new ServiceException(e);
			} else {
				if (res != null) {
					messageServiceLaunch.setResultType(res.getClass());
				}

				fireMessageRecveidChanged(messageServiceLaunch);
			}
		} else {
			log.debug("Timeout response packet currentID : " + currentID);

			StringBuilder sb = new StringBuilder();
			sb.append("Je n'ai pas reçus la réponse à temps ( + de ");
			sb.append(timeout / 1000);
			sb.append(" secondes)");

			messageServiceLaunch.setEndDate(new Date());
			messageServiceLaunch.setError(sb.toString());

			fireMessageRecveidChanged(messageServiceLaunch);

			throw new SendServiceMessageException(SendServiceMessageException.Type.Timeout, sb.toString());
		}

		return res;
	}
}
