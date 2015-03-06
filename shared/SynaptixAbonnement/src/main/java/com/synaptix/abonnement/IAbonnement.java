package com.synaptix.abonnement;

public interface IAbonnement<E> {

	/**
	 * Permet d'adherer à l'abonnement
	 * 
	 * @param jid
	 * @param parameters
	 */
	public void adherer(String jid, IParameters<E> parameters);

	/**
	 * Permet de revoquer l'abonnement
	 * 
	 * @param jid
	 */
	public void revoquer(String jid);

	/**
	 * Permet de notifier les adherants On peut s'inclure ou pas
	 * 
	 * @param jid
	 * @param e
	 * @param sansMoi
	 * @return
	 */
	public String[] notifier(String jid, E e, boolean sansMoi);

	/**
	 * Donne la liste des adherents
	 * 
	 * @return
	 */
	public String[] getAdherants();

}
