package com.synaptix.pmgr.plugin;

import java.util.List;
import java.util.Set;

import com.synaptix.pmgr.core.lib.ProcessingChannel.Agent;
import com.synaptix.pmgr.exception.NotFoundAgentException;
import com.synaptix.pmgr.exception.NotFoundInjectorException;
import com.synaptix.pmgr.model.ICronProcessDefinition;
import com.synaptix.pmgr.model.IExportProcessDefinition;
import com.synaptix.pmgr.model.IHeartbeatProcessDefinition;
import com.synaptix.pmgr.model.IImportProcessDefinition;
import com.synaptix.pmgr.model.ISimpleProcessDefinition;
import com.synaptix.pmgr.trigger.injector.IInjector;

public interface IIntegratorFactory {

	/**
	 * Get the list of available agents
	 */
	public List<Class<? extends Agent>> getAgentList();

	/**
	 * Get the list of available injectors
	 */
	public List<Class<? extends IInjector>> getInjectorList();

	public <A extends Agent> A getAgent(Class<A> type) throws NotFoundAgentException;

	/**
	 * Get an injector
	 * @throws NotFoundInjectorException
	 */
	public <I extends IInjector> I getInjector(Class<I> type) throws NotFoundInjectorException;

	/**
	 * Get the import process definition set defined using guice injection
	 */
	public Set<IImportProcessDefinition<?>> getImportProcessDefinitionSet();

	/**
	 * Get the export process definition set defined using guice injection
	 */
	public Set<IExportProcessDefinition<?>> getExportProcessDefinitionSet();

	/**
	 * Get the heartbeat process definition set defined using guice injection
	 * 
	 * @return
	 */
	public Set<IHeartbeatProcessDefinition> getHeartbeatProcessDefinitionSet();

	/**
	 * Get the cron process definition set defined using guice injection
	 */
	public Set<ICronProcessDefinition> getCronProcessDefinitionSet();

	/**
	 * Get the simple process definition set defined using guice injection
	 */
	public Set<ISimpleProcessDefinition> getSimpleProcessDefinitionSet();

}
