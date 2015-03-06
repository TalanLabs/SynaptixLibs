package com.synaptix.swing.wizard.action;

import java.beans.PropertyChangeListener;

import javax.swing.event.SwingPropertyChangeSupport;

public abstract class AbstractWizardAction<E> implements WizardAction<E> {

	private SwingPropertyChangeSupport propertyChangeSupport;

	private boolean enabled;

	public AbstractWizardAction() {
		super();

		propertyChangeSupport = new SwingPropertyChangeSupport(this);
		enabled = true;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		boolean oldValue = this.enabled;
		if (oldValue != enabled) {
			this.enabled = enabled;
			propertyChangeSupport.firePropertyChange("enabled", oldValue,
					enabled);
		}
	}

	public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
		return propertyChangeSupport.getPropertyChangeListeners();
	}

	public synchronized void addPropertyChangeListener(
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
}
