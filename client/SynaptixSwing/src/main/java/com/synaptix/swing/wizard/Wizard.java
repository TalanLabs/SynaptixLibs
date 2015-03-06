package com.synaptix.swing.wizard;

import com.synaptix.swing.wizard.event.WizardListener;

public interface Wizard<E> {

	public abstract void addWizardListener(WizardListener<E> l);

	public abstract void removeWizardListener(WizardListener<E> l);

	/**
	 * Ajout une page de wizard
	 * 
	 * @param wizardPage
	 */
	public abstract void addWizardPage(WizardPage<E> wizardPage);

	/**
	 * Retire une page de wizard
	 * 
	 * @param wizardPage
	 */
	public abstract void removeWizardPage(WizardPage<E> wizardPage);

	/**
	 * Tableau des pages
	 * 
	 * @return
	 */
	public abstract WizardPage<E>[] getWizardPages();

	/**
	 * Permet d'aller à la page précédente si possible
	 */
	public abstract void nextWizardPage();

	/**
	 * Permet d'aller à la page suivante si possible
	 */
	public abstract void previousWizardPage();

	/**
	 * Appele le help de la page si possible
	 */
	public abstract void helpWizardPage();

	/**
	 * Fini le wizard si possible
	 */
	public abstract void finishWizardPage();

	/**
	 * Annule le wizard
	 */
	public abstract void cancelWizard();

	/**
	 * Recherche un wizard selon son id
	 * 
	 * @param id
	 * @return
	 */
	public abstract WizardPage<E> findWizardPageById(String id);

	/**
	 * Démarre le wizard avec le bean
	 * 
	 * @param bean
	 */
	public abstract void startWizard(E bean);

	/**
	 * Renvoie la nouvelle valeur du bean
	 * 
	 * @return
	 */
	public abstract E getBean();

}