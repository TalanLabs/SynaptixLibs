package com.synaptix.swing.wizard.action;

import com.synaptix.swing.wizard.Wizard;
import com.synaptix.swing.wizard.WizardPage;

public class FinishWizardAction<E> extends AbstractWizardAction<E> {

	public WizardPage<E> actionPerformed(Wizard<E> wizard,
			WizardPage<E> wizardPage, WizardAction.Type type, E bean) {
		return null;
	}
}
