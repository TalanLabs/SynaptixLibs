package com.synaptix.swing.wizard.action;

import java.beans.PropertyChangeListener;

import com.synaptix.swing.wizard.Wizard;
import com.synaptix.swing.wizard.WizardPage;

public interface WizardAction<E> {

	public enum Type {
		Previous, Next, Help, Finish
	};

	public abstract WizardPage<E> actionPerformed(Wizard<E> wizard,
			WizardPage<E> wizardPage, WizardAction.Type type, E bean);

	public abstract void setEnabled(boolean b);

	public abstract boolean isEnabled();

	public abstract void addPropertyChangeListener(
			PropertyChangeListener listener);

	public abstract void removePropertyChangeListener(
			PropertyChangeListener listener);

}
