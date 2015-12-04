package com.synaptix.pmgr.model;

import com.synaptix.pmgr.trigger.injector.IInjector;
import com.synaptix.pmgr.trigger.message.IImportFlux;

public interface IImportProcessDefinition<M extends IImportFlux> extends IFluxProcessDefinition<M> {

	public Class<? extends IInjector> getInjectorClass();

	public void setInjectorClass(Class<? extends IInjector> injectorClass);

}
