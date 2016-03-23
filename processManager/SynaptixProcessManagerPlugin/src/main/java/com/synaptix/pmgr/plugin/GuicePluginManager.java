package com.synaptix.pmgr.plugin;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.synaptix.pmgr.core.apis.Engine;
import com.synaptix.pmgr.core.apis.PluggableChannel;
import com.synaptix.pmgr.core.lib.ProcessEngine;
import com.synaptix.pmgr.core.lib.ProcessEngine.AgentDef;
import com.synaptix.pmgr.core.lib.ProcessingChannel;
import com.synaptix.pmgr.core.lib.ProcessingChannel.Agent;
import com.synaptix.pmgr.core.lib.probe.CronAgent;
import com.synaptix.pmgr.core.lib.probe.HeartbeatAgent;
import com.synaptix.pmgr.model.ICronProcessDefinition;
import com.synaptix.pmgr.model.IExportProcessDefinition;
import com.synaptix.pmgr.model.IHeartbeatProcessDefinition;
import com.synaptix.pmgr.model.IImportProcessDefinition;
import com.synaptix.pmgr.model.ISimpleProcessDefinition;
import com.synaptix.pmgr.model.ProcessType;
import com.synaptix.pmgr.trigger.gate.GateFactory;
import com.synaptix.pmgr.trigger.injector.IInjector;
import com.synaptix.pmgr.trigger.injector.MessageInjector;
import com.synaptix.tmgr.TriggerEngine;

public final class GuicePluginManager {

	private final List<String> probeChannelList = new ArrayList<String>();

	@Inject
	private IIntegratorFactory integratorFactory;

	private GateFactory gateFactory;

	@Inject
	public GuicePluginManager() {
	}

	public void initPlugins(Log logger, String pmgrName) {
		initProcessManager(logger, pmgrName, null);
		initGates(logger);
		initHeartbeats();
	}

	public void initProcessManager(Log logger, String pmgrName, Properties properties) {
		Engine engine = ProcessEngine.getInstance(pmgrName, properties);
		if (engine.getLogger() == null) {
			engine.setLogger(logger);
		}
		logger.info("DEMARRAGE DU PROCESS MGR ID : " + pmgrName);
		// Agents
		List<Class<? extends Agent>> agentList = integratorFactory.getAgentList();
		if ((agentList != null) && (!agentList.isEmpty())) {
			for (Class<? extends Agent> agentClass : agentList) {
				AgentDef agentDef = new AgentDef(agentClass.getSimpleName(), null, integratorFactory.getAgent(agentClass));
				ProcessEngine.addAgentDef(agentDef);
			}
		}

		// Import channels
		Set<IImportProcessDefinition<?>> importProcessDefinitionSet = integratorFactory.getImportProcessDefinitionSet();
		if ((importProcessDefinitionSet != null) && (!importProcessDefinitionSet.isEmpty())) {
			for (IImportProcessDefinition<?> importProcessDefinition : importProcessDefinitionSet) {
				if (ProcessType.PROCESSING_GROUP == importProcessDefinition.getProcessType()) {
					List<PluggableChannel> channels = buildChannelGroup(importProcessDefinition.getName(), importProcessDefinition.getMaxWorking(), importProcessDefinition.getMaxWaiting(),
							integratorFactory.getAgent(importProcessDefinition.getAgentClass()), engine);
					for (PluggableChannel channel : channels) {
						engine.plugChannel(channel, true);
					}
					engine.declareGroup(importProcessDefinition.getName(), channels);
				} else {
					ProcessingChannel processingChannel = new ProcessingChannel(importProcessDefinition.getName(), importProcessDefinition.getMaxWorking(), importProcessDefinition.getMaxWaiting(),
							integratorFactory.getAgent(importProcessDefinition.getAgentClass()), engine);
					engine.plugChannel(processingChannel, true);
				}
			}
		}

		// Export channels
		Set<IExportProcessDefinition<?>> exportProcessDefinitionSet = integratorFactory.getExportProcessDefinitionSet();
		if ((exportProcessDefinitionSet != null) && (!exportProcessDefinitionSet.isEmpty())) {
			for (IExportProcessDefinition<?> exportProcessDefinition : exportProcessDefinitionSet) {
				if (ProcessType.PROCESSING_GROUP == exportProcessDefinition.getProcessType()) {
					List<PluggableChannel> channels = buildChannelGroup(exportProcessDefinition.getName(), exportProcessDefinition.getMaxWorking(), exportProcessDefinition.getMaxWaiting(),
							integratorFactory.getAgent(exportProcessDefinition.getAgentClass()), engine);
					for (PluggableChannel channel : channels) {
						engine.plugChannel(channel, true);
					}
					engine.declareGroup(exportProcessDefinition.getName(), channels);
				} else {
					ProcessingChannel processingChannel = new ProcessingChannel(exportProcessDefinition.getName(), exportProcessDefinition.getMaxWorking(), exportProcessDefinition.getMaxWaiting(),
							integratorFactory.getAgent(exportProcessDefinition.getAgentClass()), engine);
					engine.plugChannel(processingChannel, true);
				}
			}
		}

		// Heartbeat channels
		Set<IHeartbeatProcessDefinition> heartbeatProcessDefinitionSet = integratorFactory.getHeartbeatProcessDefinitionSet();
		if ((heartbeatProcessDefinitionSet != null) && (!heartbeatProcessDefinitionSet.isEmpty())) {
			for (IHeartbeatProcessDefinition heartbeatProcessDefinition : heartbeatProcessDefinitionSet) {

				String agentName = heartbeatProcessDefinition.getAgentClass().getSimpleName();
				String heartbeatChannel = agentName + "HeartbeatChannel";
				String heartbeatAgentName = agentName + "HeartbeatAgent";
				if (heartbeatProcessDefinition.getBeat() != null) {
					heartbeatChannel = heartbeatChannel + "_" + heartbeatProcessDefinition.getBeat();
					heartbeatAgentName = heartbeatAgentName + "_" + heartbeatProcessDefinition.getBeat();
					agentName = agentName + "_" + heartbeatProcessDefinition.getBeat();
				}

				HeartbeatAgent heartbeatAgent = new HeartbeatAgent(agentName, heartbeatProcessDefinition.getBeat(), heartbeatProcessDefinition.getSeconds() * 1000);
				AgentDef heartbeatAgentDef = new AgentDef(heartbeatAgentName, null, heartbeatAgent);
				ProcessEngine.addAgentDef(heartbeatAgentDef);

				ProcessingChannel processingChannelHeartbeat = new ProcessingChannel(heartbeatChannel, 1, 1, heartbeatAgent, engine);
				engine.plugChannel(processingChannelHeartbeat, false);

				ProcessingChannel processingChannel = new ProcessingChannel(agentName, 1, 1, integratorFactory.getAgent(heartbeatProcessDefinition.getAgentClass()), engine);
				engine.plugChannel(processingChannel, false);

				probeChannelList.add(heartbeatChannel);
			}
		}

		// Cron channels
		Set<ICronProcessDefinition> cronProcessDefinitionSet = integratorFactory.getCronProcessDefinitionSet();
		if ((cronProcessDefinitionSet != null) && (!cronProcessDefinitionSet.isEmpty())) {
			for (ICronProcessDefinition cronProcessDefinition : cronProcessDefinitionSet) {

				String agentName = cronProcessDefinition.getAgentClass().getSimpleName();
				String cronChannel = agentName + "CronChannel";
				String cronAgentName = agentName + "CronAgent";

				CronAgent cronAgent = new CronAgent(agentName, cronProcessDefinition.getSchedulingPattern());
				AgentDef cronAgentDef = new AgentDef(cronAgentName, null, cronAgent);
				ProcessEngine.addAgentDef(cronAgentDef);

				ProcessingChannel processingChannelCron = new ProcessingChannel(cronChannel, 1, 1, cronAgent, engine);
				engine.plugChannel(processingChannelCron, false);

				ProcessingChannel processingChannel = new ProcessingChannel(agentName, 1, 1, integratorFactory.getAgent(cronProcessDefinition.getAgentClass()), engine);
				engine.plugChannel(processingChannel, false);

				probeChannelList.add(cronChannel);
			}
		}

		// Simple channels
		Set<ISimpleProcessDefinition> simpleProcessDefinitionSet = integratorFactory.getSimpleProcessDefinitionSet();
		if ((simpleProcessDefinitionSet != null) && (!simpleProcessDefinitionSet.isEmpty())) {
			for (ISimpleProcessDefinition simpleProcessDefinition : simpleProcessDefinitionSet) {
				ProcessingChannel processingChannel = new ProcessingChannel(simpleProcessDefinition.getName(), simpleProcessDefinition.getMaxWorking(), simpleProcessDefinition.getMaxWaiting(),
						integratorFactory.getAgent(simpleProcessDefinition.getAgentClass()), engine);
				engine.plugChannel(processingChannel, false);
			}
		}

		// XML channels & agents
		try {
			Enumeration<URL> en = GuicePluginManager.class.getClassLoader().getResources("META-INF/agents.xml");
			while (en.hasMoreElements()) {
				URL res = en.nextElement();
				ProcessEngine.initPlugs(res.openStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		engine.activateChannels();
		// Nouvelle version du process manager
		// rechargement des messages en erreur
		// engine.handleMessageOnStratUp();
	}

	public void initGates(Log logger) {
		GateFactory gateFactory = getGateFactory(logger);
		for (Class<? extends IInjector> injectorClass : integratorFactory.getInjectorList()) {
			IInjector injectorImpl = integratorFactory.getInjector(injectorClass);
			gateFactory.buildGate(injectorImpl.getName(), injectorImpl.getDelay(), (MessageInjector) injectorImpl);

		}
		try {
			Enumeration<URL> en = GuicePluginManager.class.getClassLoader().getResources("META-INF/triggers.xml");
			while (en.hasMoreElements()) {
				URL res = en.nextElement();
				gateFactory.initPlugs(res.openStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initHeartbeats() {
		for (String heartbeatChannel : probeChannelList) {
			ProcessEngine.handle(heartbeatChannel, null);
		}
	}

	private List<PluggableChannel> buildChannelGroup(String id, int maxWorking, int maxWaiting, Agent agent, Engine engine) {
		int max_working = 1;
		List<PluggableChannel> channels = new ArrayList<PluggableChannel>();
		for (int i = 0; i < maxWorking; i++) {
			try {
				String idChannel = id + "_" + i;
				PluggableChannel chn = new ProcessingChannel(idChannel, max_working, maxWaiting, agent, engine);
				channels.add(chn);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		try {
			String idChannel = id + "_DEFAULT";
			PluggableChannel chn = new ProcessingChannel(idChannel, max_working, maxWaiting, agent, engine);
			channels.add(chn);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return channels;
	}

	public void shutdown() {
		if (gateFactory != null) {
			gateFactory.closeGates();
		}
		TriggerEngine.getInstance().shutdown();
		ProcessEngine.shutdown();
	}

	private GateFactory getGateFactory(Log logger) {
		if (gateFactory == null) {
			gateFactory = new GateFactory(GuicePluginManager.class.getClassLoader(), logger);
		}
		return gateFactory;
	}
}
