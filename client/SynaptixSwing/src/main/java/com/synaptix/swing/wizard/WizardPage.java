package com.synaptix.swing.wizard;

import java.awt.Component;

import javax.swing.Icon;

import com.synaptix.swing.wizard.action.WizardAction;

public interface WizardPage<E> {

	/**
	 * Identifiant unique de la page
	 * 
	 * @return
	 */
	public abstract String getId();

	/**
	 * Titre de la page
	 * 
	 * @return
	 */
	public abstract String getTitle();

	/**
	 * Description de la page
	 * 
	 * @return
	 */
	public abstract String getDescription();

	/**
	 * Icon de la page
	 * 
	 * @return
	 */
	public abstract Icon getIcon();

	/**
	 * L'action quand on appuie sur le bouton précendent
	 * 
	 * @return
	 */
	public abstract WizardAction<E> getPreviousAction();

	/**
	 * L'action quand on appuie sur le bouton suivant
	 * 
	 * @return
	 */
	public abstract WizardAction<E> getNextAction();

	/**
	 * L'action quand on appuie sur le bouton terminé Si null, alors non
	 * disponible pour la page
	 * 
	 * @return
	 */
	public abstract WizardAction<E> getFinishAction();

	/**
	 * L'action quand on appuie sur le bouton aide. Si null, alors non
	 * disponible pour la page
	 * 
	 * @return
	 */
	public abstract WizardAction<E> getHelpAction();

	/**
	 * La vue de la page
	 * 
	 * @return
	 */
	public abstract Component getView();

	/**
	 * Quand la page est affiché le load est appelé
	 * 
	 * @param e
	 */
	public abstract void load(E e);

	/**
	 * Quand on fait next, le commit est appelé avant
	 * 
	 * @param e
	 */
	public abstract void commit(E e);

}
