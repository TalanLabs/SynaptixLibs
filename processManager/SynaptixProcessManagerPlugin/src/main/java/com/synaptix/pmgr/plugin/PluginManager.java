//package com.synaptix.pmgr.plugin;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.Enumeration;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.impl.LogFactoryImpl;
//import org.apache.log4j.PropertyConfigurator;
//
//import com.synaptix.pmgr.core.apis.Engine;
//import com.synaptix.pmgr.core.lib.ProcessEngine;
//import com.synaptix.pmgr.trigger.gate.GateFactory;
//
//public class PluginManager {
//	public static void initPlugins(String pmgrName) {
//		PropertyConfigurator.configure(PluginManager.class.getClassLoader().getResource("log4j.properties"));
//		Log logger = LogFactoryImpl.getLog(PluginManager.class);
//
//		initPlugins(logger, pmgrName);
//
//	}
//
//	public static void initPlugins(Log logger, String pmgrName) {
//		initProcessManager(logger, pmgrName);
//		initGates(logger);
//	}
//
//	public static void initProcessManager(Log logger, String pmgrName) {
//		Engine engine = ProcessEngine.getInstance(pmgrName);
//		if (engine.getLogger() == null) {
//			engine.setLogger(logger);
//		}
//		logger.info("DEMARRAGE DU PROCESS MGR ID : " + pmgrName);
//		try {
//			Enumeration<URL> en = PluginManager.class.getClassLoader().getResources("META-INF/agents.xml");
//			while (en.hasMoreElements()) {
//				URL res = en.nextElement();
//				ProcessEngine.initPlugs(res.openStream());
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		engine.activateChannels();
//		// Nouvelle version du process manager
//		// rechargement des messages en erreur
//		// engine.handleMessageOnStratUp();
//	}
//
//	public static void initGates(Log logger) {
//		try {
//			Enumeration<URL> en = PluginManager.class.getClassLoader().getResources("META-INF/triggers.xml");
//			GateFactory gateFactory = new GateFactory(PluginManager.class.getClassLoader(), logger);
//			while (en.hasMoreElements()) {
//				URL res = en.nextElement();
//				gateFactory.initPlugs(res.openStream());
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
// }
