package com.synaptix.taskmanager.controller.helper;

import java.io.Serializable;

public interface IStateService {

	/**
	 * Finds and returns the ID of the IState designed by its name and its country name.
	 * 
	 * @param state
	 *            the name of the state
	 * @param country
	 *            the name of the country
	 * @return the Serializable ID of the IState
	 */
	public Serializable getIdStateByNameCountry(final String state, final String country);
}
