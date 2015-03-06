package com.synaptix.swing.wizard.event;

import com.synaptix.swing.wizard.Wizard;
import com.synaptix.swing.wizard.WizardPage;

public abstract class WizardAdapter<E> implements WizardListener<E> {

	public void addedWizardPage(Wizard<E> wizard, WizardPage<E> wizardPage) {
	}

	public void removedWizardPage(Wizard<E> wizard, WizardPage<E> wizardPage) {
	}

	public void selectedWizardPage(Wizard<E> wizard, WizardPage<E> wizardPage) {
	}

	public void startedWizard(Wizard<E> wizard) {
	}

	public void stopedWizard(Wizard<E> wizard, boolean finished) {
	}
}
