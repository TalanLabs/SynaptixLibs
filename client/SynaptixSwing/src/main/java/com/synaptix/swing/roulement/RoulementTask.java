package com.synaptix.swing.roulement;

public interface RoulementTask extends IDayDatesMinMax {

	public abstract RoulementTimelineTaskRenderer getTaskRenderer();

	/**
	 * Est ce que le dessin de la tache peut dépasser ça taille d'origine
	 * 
	 * @return
	 */
	public abstract boolean isNoClipping();

	/**
	 * Ordre d'affichage des taches, plus petit en premier
	 * 
	 * @return
	 */
	public abstract int getOrdre();

	/**
	 * La tache est séléctionnable
	 * 
	 * @return
	 */
	public abstract boolean isSelectable();

	/**
	 * Permet de savoir si on a une tache qui est la base d'un groupe
	 */
	public abstract boolean isGroup();

	/**
	 * Renvoie si la tache prend toute la place dispo de la ressource
	 * 
	 * @return
	 */
	public abstract boolean isUseMaxHeight();

}
