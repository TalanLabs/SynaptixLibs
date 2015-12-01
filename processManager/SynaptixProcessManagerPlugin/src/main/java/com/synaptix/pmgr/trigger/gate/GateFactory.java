package com.synaptix.pmgr.trigger.gate;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.synaptix.pmgr.core.lib.ProcessEngine;
import com.synaptix.pmgr.trigger.injector.InjectorDefinition;
import com.synaptix.pmgr.trigger.injector.MessageInjector;
import com.synaptix.toolkits.nanoxml.NanoXMLKit;

import nanoxml.XMLElement;

public class GateFactory {

	protected static Map<String, InjectorDefinition> injectorDefs = new HashMap<String, InjectorDefinition>();
	protected static ClassLoader classLoader;
	protected Log logger;

	public GateFactory(ClassLoader clloader, Log log) {
		classLoader = clloader;
		logger = log;
	}

	public void initPlugs(InputStream plugconf) {
		XMLElement conf = NanoXMLKit.load(plugconf, true);
		if (conf != null) {
			parseConf(conf);
		}
	}

	public void initPlugs(String plugconf) {
		XMLElement conf = NanoXMLKit.load(plugconf, GateFactory.class, true);
		if (conf != null) {
			parseConf(conf);
		}
	}

	@SuppressWarnings("unchecked")
	private void parseConf(XMLElement conf) {
		for (Enumeration e = conf.enumerateChildren(); e.hasMoreElements();) {
			XMLElement elem = (XMLElement) e.nextElement();
			if (elem.getName().equals("triggers")) {
				parseInjectorDef(elem);
			}
			if (elem.getName().equals("gates")) {
				parseGateConf(elem);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void parseInjectorDef(XMLElement conf) {
		for (Enumeration e = conf.enumerateChildren(); e.hasMoreElements();) {
			XMLElement elem = (XMLElement) e.nextElement();
			if (elem.getName().equals("trigger")) {
				String id = elem.getStringAttribute("id");
				String cls = elem.getStringAttribute("class");
				String desc = elem.getStringAttribute("desc", "-");
				List parameters = new ArrayList();
				for (Enumeration ep = elem.enumerateChildren(); ep.hasMoreElements();) {
					XMLElement p = (XMLElement) ep.nextElement();
					if (p.getName().equals("arg")) {
						parameters.add(p.getContent());
					}
				}
				try {
					addInjectorDef(new InjectorDefinition(id, desc, (String[]) parameters.toArray(new String[0]), cls, classLoader));
				} catch (ClassNotFoundException e1) {
					if ((ProcessEngine.getInstance() != null) && (ProcessEngine.getInstance().getLogger() != null)) {
						logger.error(e1);
					} else {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public void addInjectorDef(InjectorDefinition injectDef) {
		injectorDefs.put(injectDef.getID(), injectDef);
	}

	@SuppressWarnings("unchecked")
	private void parseGateConf(XMLElement conf) {
		for (Enumeration e = conf.enumerateChildren(); e.hasMoreElements();) {
			XMLElement elem = (XMLElement) e.nextElement();
			if (elem.getName().equals("gate")) {
				String id = elem.getStringAttribute("id");
				long delay = Long.parseLong(elem.getStringAttribute("delay"));
				buildGate(elem, id, delay);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> parseParameters(XMLElement conf) {
		Map<String, String> m = new HashMap<String, String>();
		for (Enumeration<XMLElement> e = conf.enumerateChildren(); e.hasMoreElements();) {
			XMLElement elem = e.nextElement();
			if (elem.getName().equals("param")) {
				String name = elem.getStringAttribute("name");
				String value = elem.getStringAttribute("value");
				m.put(name, value);
			}
		}
		return m;
	}

	private Gate buildGate(XMLElement conf, String id, long delay) {
		Map<String, String> params = parseParameters(conf);

		InjectorDefinition injectDef = injectorDefs.get(params.get("injecteur"));
		Gate gate = null;
		if (injectDef != null) {
			try {
				gate = buildGate(id, delay, injectDef.resolveInjector());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}

		}

		params.clear();
		if (gate != null) {
			return gate;

		}
		return null;
	}

	public Gate buildGate(String id, long delay, MessageInjector messageInjector) {
		Gate gate = new DefaultFileSysGate(id, messageInjector.getWorkDir(), new File(messageInjector.getWorkDir().getPath() + "/accepted"), new File(messageInjector.getWorkDir().getPath()
				+ "/rejected"), new File(messageInjector.getWorkDir().getPath() + "/retry"), new File(messageInjector.getWorkDir().getPath() + "/archive"), delay, messageInjector, logger);

		logger.info("INSTALLATION DE LA GATE " + gate.getName() + " REUSSIE [" + id + "(" + messageInjector.getClass().getSimpleName() + ")][" + messageInjector.getWorkDir().getPath() + "]");
		return gate;
	}
}
