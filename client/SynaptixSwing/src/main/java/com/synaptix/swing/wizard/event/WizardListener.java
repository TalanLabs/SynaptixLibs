package com.synaptix.swing.wizard.event;

import java.util.EventListener;

import com.synaptix.swing.wizard.Wizard;
import com.synaptix.swing.wizard.WizardPage;

public interface WizardListener<E> extends EventListener {

	/**
	 * Appeler quand le wizard ajoute une page
	 * 
	 * @param wizard
	 * @param wizardPage
	 */
	public abstract void addedWizardPage(Wizard<E> wizard,
			WizardPage<E> wizardPage);

	/**
	 * Appeler quand le wizard retire une page
	 * 
	 * @param wizard
	 * @param wizardPage
	 */
	public abstract void removedWizardPage(Wizard<E> wizard,
			WizardPage<E> wizardPage);

	/**
	 * Appeler quand une page est séléctionnée, même la première
	 * 
	 * @param wizard
	 * @param wizardPage
	 */
	public abstract void selectedWizardPage(Wizard<E> wizard,
			WizardPage<E> wizardPage);

	/**
	 * Appeler quand le wizard commence
	 * 
	 * @param wizard
	 */
	public abstract void startedWizard(Wizard<E> wizard);

	/**
	 * Appeler quand le wizard fini, appeler soit par le finish ou le cancel
	 * 
	 * @param wizard
	 * @param finished
	 */
	public abstract void stopedWizard(Wizard<E> wizard, boolean finished);

}
