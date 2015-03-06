package com.synaptix.abonnement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractAbonnement<E> implements IAbonnement<E> {

	private Map<String, IParameters<E>> map;

	public AbstractAbonnement() {
		map = new HashMap<String, IParameters<E>>();
	}

	public synchronized void adherer(String jid, IParameters<E> parameters) {
		if (map.containsKey(jid)) {
			map.remove(jid);
		}
		map.put(jid, parameters);
	}

	public synchronized void revoquer(String jid) {
		if (map.containsKey(jid)) {
			map.remove(jid);
		}
	}

	public synchronized String[] notifier(String jid, E e, boolean sansMoi) {
		List<String> lList = new ArrayList<String>();
		for (Entry<String, IParameters<E>> entry : map.entrySet()) {
			String name = entry.getKey();		
			if ((!sansMoi || jid == null || !name.equals(jid))
					&& entry.getValue().validate(e)) {
				lList.add(name);
			}
		}
		return lList.toArray(new String[lList.size()]);
	}

	public synchronized String[] getAdherants() {
		Collection<String> cList = map.keySet();
		return cList.toArray(new String[cList.size()]);
	}
}
