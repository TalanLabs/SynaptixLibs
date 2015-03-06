package com.synaptix.smackx.abonnement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

import com.synaptix.abonnement.AbonnementDescriptor;
import com.synaptix.abonnement.IParameters;
import com.synaptix.smackx.abonnement.packet.RequestAdhererGestionAbonnementIQ;
import com.synaptix.smackx.abonnement.packet.RequestInfoAbonnementGestionAbonnementIQ;
import com.synaptix.smackx.abonnement.packet.RequestInfoAbonnementsGestionAbonnementIQ;
import com.synaptix.smackx.abonnement.packet.RequestNotifierGestionAbonnementIQ;
import com.synaptix.smackx.abonnement.packet.RequestRevoquerGestionAbonnementIQ;
import com.synaptix.smackx.abonnement.packet.ResultInfoAbonnementGestionAbonnementIQ;
import com.synaptix.smackx.abonnement.packet.ResultInfoAbonnementsGestionAbonnementIQ;
import com.synaptix.smackx.abonnement.packet.ResultNotificationGestionAbonnementIQ;

public class GestionAbonementManager {

	private static final Map<XMPPConnection, GestionAbonementManager> instances = new WeakHashMap<XMPPConnection, GestionAbonementManager>();

	public static GestionAbonementManager getInstance(
			final XMPPConnection connection) {
		if (connection == null) {
			return null;
		}
		synchronized (instances) {
			GestionAbonementManager manager = instances.get(connection);
			if (manager == null) {
				manager = new GestionAbonementManager(connection);
				instances.put(connection, manager);
			}

			return manager;
		}
	}

	private final XMPPConnection connection;

	protected Map<String, List<INotificationAbonnement>> listenerMap;

	private GestionAbonementManager(XMPPConnection connection) {
		this.connection = connection;
		initialize();
	}

	private void initialize() {
		listenerMap = new HashMap<String, List<INotificationAbonnement>>();

		connection.addConnectionListener(new MyConnectionListener());

		connection.addPacketListener(new MyPacketListener(),
				new PacketTypeFilter(
						ResultNotificationGestionAbonnementIQ.class));
	}

	private final class MyPacketListener implements PacketListener {

		public void processPacket(Packet packet) {
			ResultNotificationGestionAbonnementIQ result = (ResultNotificationGestionAbonnementIQ) packet;
			fireNotification(result.getName(), result.getFrom(), result
					.getValue());
		}
	}

	public synchronized void addNotificationAbonnement(String name,
			INotificationAbonnement l) {
		if (!listenerMap.containsKey(name)) {
			listenerMap.put(name, new ArrayList<INotificationAbonnement>());
		}
		listenerMap.get(name).add(l);
	}

	public synchronized void removeNotificationAbonnement(String name,
			INotificationAbonnement l) {
		if (listenerMap.containsKey(name)) {
			listenerMap.get(name).remove(l);
		}
	}

	private void fireNotification(String name, String from, Object value) {
		if (listenerMap.containsKey(name)) {
			for (INotificationAbonnement l : listenerMap.get(name)) {
				l.notification(name, from, value);
			}
		}
	}

	public static AbonnementDescriptor[] getAbonnementDesciptors(
			XMPPConnection connection) throws XMPPException {
		RequestInfoAbonnementsGestionAbonnementIQ iq = new RequestInfoAbonnementsGestionAbonnementIQ();

		PacketCollector collector = connection
				.createPacketCollector(new PacketIDFilter(iq.getPacketID()));

		connection.sendPacket(iq);

		IQ result = (IQ) collector.nextResult();
		if (result == null) {
			throw new XMPPException("No response from the server.");
		}
		if (result.getType() == IQ.Type.ERROR) {
			throw new XMPPException(result.getError());
		}
		return ((ResultInfoAbonnementsGestionAbonnementIQ) result)
				.getAbonnementDescriptors();
	}

	public String[] getNameAbonnements() throws XMPPException {
		AbonnementDescriptor[] ad = getAbonnementDesciptors(connection);
		String[] names = new String[ad.length];
		for (int i = 0; i < ad.length; i++) {
			names[i] = ad[i].getName();
		}
		return names;
	}

	public String[] getAdherants(String name) throws XMPPException {
		RequestInfoAbonnementGestionAbonnementIQ iq = new RequestInfoAbonnementGestionAbonnementIQ(
				name);

		PacketCollector collector = connection
				.createPacketCollector(new PacketIDFilter(iq.getPacketID()));

		connection.sendPacket(iq);

		IQ result = (IQ) collector.nextResult();
		if (result == null) {
			throw new XMPPException("No response from the server.");
		}
		if (result.getType() == IQ.Type.ERROR) {
			throw new XMPPException(result.getError());
		}
		return ((ResultInfoAbonnementGestionAbonnementIQ) result)
				.getAdherants();
	}

	public void adherer(String name, IParameters<?> parameters)
			throws XMPPException {
		RequestAdhererGestionAbonnementIQ iq = new RequestAdhererGestionAbonnementIQ(
				name, parameters);

		PacketCollector collector = connection
				.createPacketCollector(new PacketIDFilter(iq.getPacketID()));

		connection.sendPacket(iq);

		IQ result = (IQ) collector.nextResult();
		if (result == null) {
			throw new XMPPException("No response from the server.");
		}
		if (result.getType() == IQ.Type.ERROR) {
			throw new XMPPException(result.getError());
		}
	}

	public void revoquer(String name) throws XMPPException {
		RequestRevoquerGestionAbonnementIQ iq = new RequestRevoquerGestionAbonnementIQ(
				name);
		PacketCollector collector = connection
				.createPacketCollector(new PacketIDFilter(iq.getPacketID()));

		connection.sendPacket(iq);

		IQ result = (IQ) collector.nextResult();

		if (result == null) {
			throw new XMPPException("No response from the server.");
		}
		if (result.getType() == IQ.Type.ERROR) {
			throw new XMPPException(result.getError());
		}
	}

	/**
	 * Permet de notifier tous les adherants sauf moi
	 * 
	 * @param name
	 * @param value
	 * @throws XMPPException
	 */
	public void notifier(String name, Object value) throws XMPPException {
		notifier(name, value, true);
	}

	/**
	 * Permet de notifier tous les adherents
	 * 
	 * @param name
	 * @param value
	 * @param sansMoi
	 * @throws XMPPException
	 */
	public void notifier(String name, Object value, boolean sansMoi)
			throws XMPPException {
		RequestNotifierGestionAbonnementIQ iq = new RequestNotifierGestionAbonnementIQ(
				name, value, sansMoi);

		PacketCollector collector = connection
				.createPacketCollector(new PacketIDFilter(iq.getPacketID()));

		connection.sendPacket(iq);

		IQ result = (IQ) collector.nextResult();
		if (result == null) {
			throw new XMPPException("No response from the server.");
		}
		if (result.getType() == IQ.Type.ERROR) {
			throw new XMPPException(result.getError());
		}
	}

	private final class MyConnectionListener implements ConnectionListener {
		public void connectionClosed() {
			instances.remove(connection);
		}

		public void connectionClosedOnError(Exception e) {
		}

		public void reconnectionFailed(Exception e) {
		}

		public void reconnectingIn(int seconds) {
		}

		public void reconnectionSuccessful() {
		}
	}
}
