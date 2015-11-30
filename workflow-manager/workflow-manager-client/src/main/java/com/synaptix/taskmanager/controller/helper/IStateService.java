package com.synaptix.taskmanager.controller.helper;

import com.synaptix.entity.IId;

public interface IStateService {

	/**
	 * Finds and returns the ID of the IState designed by its name and its country name.
	 * 
	 * @param state
	 *            the name of the state
	 * @param country
	 *            the name of the country
	 * @return the IId id of the IState
	 */
	public IId getIdStateByNameCountry(final String state, final String country);
}
