package com.synaptix.client.view.header;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.client.view.io.StatsInput;
import com.synaptix.client.view.io.StatsOutput;

public abstract class AbstractHeader implements IHeader {

	private List<HeaderListener> listenerList;

	public AbstractHeader() {
		listenerList = new ArrayList<HeaderListener>();
	}

	public String getCategorie() {
		return "Inconnue";
	}

	public void headerStateChanged(HeaderStateEvent e) {
	}

	public void addHeaderListener(HeaderListener l) {
		listenerList.add(l);
	}

	public void removeHeaderListener(HeaderListener l) {
		listenerList.remove(l);
	}

	public void readStats(StatsInput in) {
	}

	public void writeStats(StatsOutput out) {
	}

	protected void fireHeaderChanged() {
		HeaderEvent e = new HeaderEvent(this, HeaderEvent.Type.HEADER_CHANGED);

		for (HeaderListener listener : listenerList) {
			listener.headerChanged(e);
		}
	}
}
