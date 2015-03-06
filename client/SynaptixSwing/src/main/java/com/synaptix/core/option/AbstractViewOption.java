package com.synaptix.core.option;

import javax.swing.event.EventListenerList;

import com.jgoodies.validation.ValidationResult;
import com.synaptix.core.event.ViewOptionListener;
import com.synaptix.core.event.ViewOptionStateEvent;

public abstract class AbstractViewOption implements IViewOption {

	protected EventListenerList listenerList;

	public AbstractViewOption() {
		listenerList = new EventListenerList();
	}

	public String getCategorie() {
		return "Inconnue";
	}

	public void viewOptionStateChanged(ViewOptionStateEvent e) {
	}

	public void addViewOptionListener(ViewOptionListener l) {
		listenerList.add(ViewOptionListener.class, l);
	}

	public void removeViewOptionListener(ViewOptionListener l) {
		listenerList.remove(ViewOptionListener.class, l);
	}

	protected void fireValidationResultChanged(ValidationResult result) {
		ViewOptionListener[] listeners = listenerList
				.getListeners(ViewOptionListener.class);
		for (ViewOptionListener listener : listeners) {
			listener.viewOptionValidationResultChanged(this, result);
		}
	}
}
