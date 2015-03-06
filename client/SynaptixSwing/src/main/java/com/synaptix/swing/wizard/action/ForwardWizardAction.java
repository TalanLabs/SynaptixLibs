package com.synaptix.swing.wizard.action;

import com.synaptix.swing.wizard.Wizard;
import com.synaptix.swing.wizard.WizardPage;

public class ForwardWizardAction<E> extends AbstractWizardAction<E> {

	private String nextId;

	public ForwardWizardAction(String nextId) {
		super();

		this.nextId = nextId;
	}

	public WizardPage<E> actionPerformed(Wizard<E> wizard,
			WizardPage<E> wizardPage, WizardAction.Type type, E bean) {
		return wizard.findWizardPageById(nextId);
	}
}
