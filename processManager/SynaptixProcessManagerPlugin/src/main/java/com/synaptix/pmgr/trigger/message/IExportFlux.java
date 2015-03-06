package com.synaptix.pmgr.trigger.message;

import com.synaptix.pmgr.model.annotation.Flux;

public interface IExportFlux extends IFlux {

	/**
	 * Return the name of the flux (based on the {@link Flux} annotation)
	 * 
	 * @return
	 */
	public String getName();

}
