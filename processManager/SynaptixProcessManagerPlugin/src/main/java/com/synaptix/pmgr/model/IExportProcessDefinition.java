package com.synaptix.pmgr.model;

import com.synaptix.pmgr.trigger.dispatcher.IDispatcher;
import com.synaptix.pmgr.trigger.message.IExportFlux;

public interface IExportProcessDefinition<M extends IExportFlux> extends IFluxProcessDefinition<M> {

	public Class<? extends IDispatcher<M>> getDispatcherClass();

	public void setDispatcherClass(Class<? extends IDispatcher<M>> dispatcherClass);

}
