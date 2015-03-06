package com.synaptix.client.view;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractViewWorker<E, F> implements IViewWorker<E, F> {

	private List<PublisherListener<F>> listenerList;

	public AbstractViewWorker() {
		listenerList = new ArrayList<PublisherListener<F>>();
	}

	public final void publish(F msg) {
		for (PublisherListener<F> l : listenerList) {
			l.publishMessage(msg);
		}
	}

	public final void addPublisherListener(PublisherListener<F> l) {
		listenerList.add(l);
	}

	public final void removePublisherListener(PublisherListener<F> l) {
		listenerList.remove(l);
	}
}
