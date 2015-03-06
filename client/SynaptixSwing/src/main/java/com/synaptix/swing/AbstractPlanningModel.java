package com.synaptix.swing;

import java.io.Serializable;

import javax.swing.event.EventListenerList;

import com.synaptix.swing.event.PlanningDataEvent;
import com.synaptix.swing.event.PlanningDataListener;

public abstract class AbstractPlanningModel implements PlanningModel,Serializable {

	private static final long serialVersionUID = 909759392650712599L;
	
	protected EventListenerList listenerList;

	public AbstractPlanningModel() {
		listenerList = new EventListenerList();
	}

	public void addPlanningDataListener(PlanningDataListener l) {
		listenerList.add(PlanningDataListener.class, l);
	}

	public void removePlanningDataListener(PlanningDataListener l) {
		listenerList.remove(PlanningDataListener.class, l);
	}

	public PlanningDataListener[] getPlanningDataListeners() {
		return (PlanningDataListener[]) listenerList
				.getListeners(PlanningDataListener.class);
	}

	protected void fireContentsChanged() {
		PlanningDataListener[] listeners = listenerList.getListeners(PlanningDataListener.class);
		PlanningDataEvent e = new PlanningDataEvent(this,PlanningDataEvent.Type.CONTENTS_CHANGED);

		for(PlanningDataListener listener : listeners) {
			listener.contentsChanged(e);
		}
	}
}
