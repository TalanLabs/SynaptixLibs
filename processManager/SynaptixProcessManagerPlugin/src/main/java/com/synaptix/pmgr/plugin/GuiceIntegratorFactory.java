package com.synaptix.pmgr.plugin;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.synaptix.pmgr.core.lib.ProcessingChannel.Agent;
import com.synaptix.pmgr.exception.NotFoundAgentException;
import com.synaptix.pmgr.exception.NotFoundInjectorException;
import com.synaptix.pmgr.model.ICronProcessDefinition;
import com.synaptix.pmgr.model.IExportProcessDefinition;
import com.synaptix.pmgr.model.IHeartbeatProcessDefinition;
import com.synaptix.pmgr.model.IImportProcessDefinition;
import com.synaptix.pmgr.model.ISimpleProcessDefinition;
import com.synaptix.pmgr.trigger.injector.IInjector;

public class GuiceIntegratorFactory implements IIntegratorFactory {

	private static final Log LOG = LogFactory.getLog(GuiceIntegratorFactory.class);

	private final Injector injector;

	private List<Class<? extends Agent>> agentList;

	private final Map<Class<?>, Object> implAgentMap;

	private final List<Class<? extends IInjector<?>>> injectorList;

	private final Map<Class<?>, Object> implInjectorMap;

	private Boolean initialized;

	@Inject(optional = true)
	private Set<IImportProcessDefinition<?>> importProcessDefinitionSet;

	@Inject(optional = true)
	private Set<IExportProcessDefinition<?>> exportProcessDefinitionSet;

	@Inject(optional = true)
	private Set<IHeartbeatProcessDefinition> heartbeatProcessDefinitionSet;

	@Inject(optional = true)
	private Set<ICronProcessDefinition> cronProcessDefinitionSet;

	@Inject(optional = true)
	private Set<ISimpleProcessDefinition> simpleProcessDefinitionSet;

	@Inject
	public GuiceIntegratorFactory(Injector injector) {
		super();

		initialized = false;

		this.agentList = new ArrayList<Class<? extends Agent>>();
		this.implAgentMap = new HashMap<Class<?>, Object>();

		this.injectorList = new ArrayList<Class<? extends IInjector<?>>>();
		this.implInjectorMap = new HashMap<Class<?>, Object>();

		this.injector = injector;
	}

	protected final synchronized void initIntegratorMap() {
		synchronized (initialized) {
			if (!initialized) {
				for (Binding<?> binding : injector.getBindings().values()) {
					Type type = binding.getKey().getTypeLiteral().getType();
					if (type instanceof Class) {
						Class<?> beanClass = (Class<?>) type;
						if (Agent.class.isAssignableFrom(beanClass)) {
							@SuppressWarnings("unchecked")
							Class<? extends Agent> agentClass = (Class<? extends Agent>) beanClass;
							addAgent(agentClass);
						} else if (IInjector.class.isAssignableFrom(beanClass)) {
							@SuppressWarnings("unchecked")
							Class<? extends IInjector<?>> injectorClass = (Class<? extends IInjector<?>>) beanClass;
							addInjector(injectorClass);
						}
					}
				}
				init();

				initialized = true;
			}
		}
	}

	protected void init() {
	}

	private <A extends Agent> void addAgent(Class<A> agentClass) {
		if (agentClass == null) {
			throw new IllegalArgumentException("agentClass is null");
		}
		LOG.info("Add agent " + agentClass.getName());
		agentList.add(agentClass);
	}

	private <I extends IInjector<?>> void addInjector(Class<I> injectorClass) {
		if (injectorClass == null) {
			throw new IllegalArgumentException("injectorClass is null");
		}
		LOG.info("Add injector " + injectorClass.getName());
		injectorList.add(injectorClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A extends Agent> A getAgent(Class<A> type) throws NotFoundAgentException {
		initIntegratorMap();
		if (!implAgentMap.containsKey(type)) {
			int indexOf = agentList.indexOf(type);
			if (indexOf < 0) {
				throw new NotFoundAgentException(type.getName() + " not exist"); //$NON-NLS-1$
			}
			Agent agentImpl = injector.getInstance(agentList.get(indexOf));
			implAgentMap.put(type, agentImpl);
		}
		return (A) implAgentMap.get(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <I extends IInjector<?>> I getInjector(Class<I> type) throws NotFoundInjectorException {
		initIntegratorMap();
		if (!implInjectorMap.containsKey(type)) {
			int indexOf = injectorList.indexOf(type);
			if (indexOf < 0) {
				throw new NotFoundInjectorException(type.getName() + " not exist"); //$NON-NLS-1$
			}
			IInjector<?> injectorImpl = injector.getInstance(injectorList.get(indexOf));
			implInjectorMap.put(type, injectorImpl);
		}
		return (I) implInjectorMap.get(type);
	}

	@Override
	public List<Class<? extends Agent>> getAgentList() {
		initIntegratorMap();
		return Collections.unmodifiableList(agentList);
	}

	@Override
	public List<Class<? extends IInjector<?>>> getInjectorList() {
		initIntegratorMap();
		return Collections.unmodifiableList(injectorList);
	}

	@Override
	public Set<IImportProcessDefinition<?>> getImportProcessDefinitionSet() {
		initIntegratorMap();
		return importProcessDefinitionSet;
	}

	@Override
	public Set<IExportProcessDefinition<?>> getExportProcessDefinitionSet() {
		initIntegratorMap();
		return exportProcessDefinitionSet;
	}

	@Override
	public Set<IHeartbeatProcessDefinition> getHeartbeatProcessDefinitionSet() {
		initIntegratorMap();
		return heartbeatProcessDefinitionSet;
	}

	@Override
	public Set<ICronProcessDefinition> getCronProcessDefinitionSet() {
		initIntegratorMap();
		return cronProcessDefinitionSet;
	}

	@Override
	public Set<ISimpleProcessDefinition> getSimpleProcessDefinitionSet() {
		initIntegratorMap();
		return simpleProcessDefinitionSet;
	}
}