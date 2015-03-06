package com.synaptix.pmgr.trigger.dispatcher;

import com.synaptix.pmgr.core.apis.Engine;
import com.synaptix.pmgr.trigger.message.IExportFlux;

public interface IDispatcher<M extends IExportFlux> {

	/**
	 * Send a message
	 * 
	 * @param exportTreatmentMessage
	 * @param processEngine
	 */
	public void send(M exportFlux, Engine processEngine);

}
