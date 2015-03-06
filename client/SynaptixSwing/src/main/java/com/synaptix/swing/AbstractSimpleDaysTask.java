package com.synaptix.swing;

public abstract class AbstractSimpleDaysTask implements SimpleDaysTask {

	private boolean group;

	private boolean useMaxHeight;

	public AbstractSimpleDaysTask() {
		this(false);
	}

	public AbstractSimpleDaysTask(boolean group) {
		this.group = group;

		this.useMaxHeight = false;
	}

	/**
	 * Est ce que la tache est le groupe
	 * 
	 * @param group
	 */
	protected void setGroup(boolean group) {
		this.group = group;
	}

	/**
	 * Permet de savoir si on a une tache qui est la base d'un groupe. Par
	 * défaut à false
	 */
	public boolean isGroup() {
		return group;
	}

	/**
	 * Est que la tache doit prendre toute la place dispo sur la ressource Cela
	 * ignore le groupe
	 * 
	 * @param useMaxHeight
	 */
	protected void setUseMaxHeight(boolean useMaxHeight) {
		this.useMaxHeight = useMaxHeight;
	}

	/**
	 * Renvoie si la tache prend toute la place dispo de la ressource
	 * 
	 * @return
	 */
	public boolean isUseMaxHeight() {
		return useMaxHeight;
	}
}
