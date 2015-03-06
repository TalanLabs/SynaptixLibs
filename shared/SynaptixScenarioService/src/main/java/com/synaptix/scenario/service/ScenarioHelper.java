package com.synaptix.scenario.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.scenario.service.model.IScenario;
import com.synaptix.scenario.service.model.IScenarioAct;

public final class ScenarioHelper {
	
	private static final String UTF_8 = "UTF-8";

	private static final String SCENARIO_EXTENSION;
	
	private static final String SCENARIO_TAG;
	
	private static final String SCENARIO_ACT_TAG;
	
	private static final String SCENARIO_ACT_XML_EXTENSION_TAG;
	
	private static final String SCENARIO_ACT_START_TIME_TAG;
	
	private static final String SCENARIO_ACT_NAME_TAG;

	private static final Log LOG;
	
	static {
		
		SCENARIO_EXTENSION = ".sce";
		
		SCENARIO_TAG = "scenario";
		
		SCENARIO_ACT_TAG = "scenarioAct";
		
		SCENARIO_ACT_XML_EXTENSION_TAG = "xmlExtension";
		
		SCENARIO_ACT_START_TIME_TAG = "time";
		
		SCENARIO_ACT_NAME_TAG = "name";

		LOG = LogFactory.getLog(ScenarioHelper.class);
		
	}

	private ScenarioHelper() {
		
	}

	/**
	 * write a scenario into a file
	 * @param filename
	 * @param scenario
	 */
	public static void saveScenario(
			final String filename, 
			final IScenario scenario
	) {
		try {
			saveScenarioFile(new File(filename), scenario);
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	/**
	 * write a scenario into a file
	 * @param file
	 * @param scenario
	 */
	public static void saveScenarioFile(
			final File file, 
			final IScenario scenario) {
		if(scenario != null) {
			if(isScenarioFile(file)) {
				writeFile(file, constructScenarioXML(scenario));
			}
			else {
				LOG.error("Wrong scenario file extension "+(file != null ? file.getAbsolutePath() : "NULL file"));
			}
		}
		else {
			final NullPointerException npe = new NullPointerException("NULL Scenario");
			LOG.error(npe.getMessage(), npe);
		}
	}
	
	private static String constructScenarioXML(final IScenario scenario) {
		final StringBuilder scenarioSb = new StringBuilder();
		appendStartTag(scenarioSb, SCENARIO_TAG);
		final List<IScenarioAct> scenarioActList = scenario.getScenarioActList();	
		if(scenarioActList != null && !scenarioActList.isEmpty()) {
			Collections.sort(scenarioActList, IScenarioAct.DEFAULT_SCENARIO_ACT_COMPARATOR);
			for(final IScenarioAct scenarioAct : scenarioActList) {
				constructScenarioActXML(scenarioSb, scenarioAct);
			}
		}
		appendEndTag(scenarioSb, SCENARIO_TAG);
		return scenarioSb.toString();
	}

	private static void constructScenarioActXML(
			final StringBuilder sbScenario, 
			final IScenarioAct scenarioAct) {
		appendStartTag(sbScenario, SCENARIO_ACT_TAG);
		
		appendStartTag(sbScenario, SCENARIO_ACT_START_TIME_TAG);
		sbScenario.append(scenarioAct.getStartTime());
		appendEndTag(sbScenario, SCENARIO_ACT_START_TIME_TAG);
		
		appendStartTag(sbScenario, SCENARIO_ACT_XML_EXTENSION_TAG);
		sbScenario.append(scenarioAct.getXMLExtension());
		appendEndTag(sbScenario, SCENARIO_ACT_XML_EXTENSION_TAG);
		
		appendStartTag(sbScenario, SCENARIO_ACT_NAME_TAG);
		sbScenario.append(scenarioAct.getName());
		appendEndTag(sbScenario, SCENARIO_ACT_NAME_TAG);

		final IScenarioAct scenarioActChild = scenarioAct.getChild(); 
		if(scenarioActChild != null) {
			constructScenarioActXML(sbScenario, scenarioActChild);
		}
		appendEndTag(sbScenario, SCENARIO_ACT_TAG);
	}
	
	private static void writeFile(
			final File file, 
			final String xmlScenario) {
		try {
			final Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), UTF_8));
			out.write(xmlScenario);
			out.flush();
			out.close();
		}
		catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	
	/**
	 * construct a scenario from a file
	 * @param filename
	 * @param scenario
	 */
	public static IScenario restaureScenario(
			final File file
	) {
		if(file != null) {
			if(isScenarioFile(file)) {
				try {
					return restaureScenario(new FileInputStream(file));
				}
				catch(final Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
			else {
				LOG.error("Wrong scenario file extension "+(file != null ? file.getAbsolutePath() : "NULL file"));
			}
		}
		else {
			final NullPointerException npe = new NullPointerException("NULL Scenario");
			LOG.error(npe.getMessage(), npe);
		}
		return null;
	}
	
	private static IScenario restaureScenario(
			final InputStream scenarioXML
	) {
		final IScenario scenario = ComponentFactory.getInstance().createInstance(IScenario.class);
		scenario.setScenarioActList(new ArrayList<IScenarioAct>());
		try {
			final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			
			final XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(scenarioXML, UTF_8);
			
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (SCENARIO_TAG.equalsIgnoreCase(xpp.getName())) {
						processScenario(scenario, xpp);
					}
				}
				eventType = xpp.next();
			}
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return scenario;
	}
	
	private static void processScenario(final IScenario scenario, final XmlPullParser xpp) throws XmlPullParserException, IOException {
		boolean done = false;

		while (!done) {
			int eventType = xpp.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (SCENARIO_ACT_TAG.equalsIgnoreCase(xpp.getName())) {
					processScenarioAct(scenario, xpp);
				}
			} 
			else if (eventType == XmlPullParser.END_TAG) {
				if (SCENARIO_TAG.equalsIgnoreCase(xpp.getName())) {
					done = true;
				}
			}
		}
	}
	
	private static void processScenarioAct(final IScenario scenario, final XmlPullParser xpp) throws XmlPullParserException, IOException {
		final IScenarioAct scenarioAct = ComponentFactory.getInstance().createInstance(IScenarioAct.class);
		
		boolean done = false;
		
		String time = null;
		String xmlExtension = null;
		String name = null;

		while (!done) {
			int eventType = xpp.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (SCENARIO_ACT_START_TIME_TAG.equalsIgnoreCase(xpp.getName())) {
					time = xpp.nextText();
				} 
				else if (SCENARIO_ACT_XML_EXTENSION_TAG.equalsIgnoreCase(xpp.getName())) {
					xmlExtension = xpp.nextText();
				} 
				else if(SCENARIO_ACT_NAME_TAG.equalsIgnoreCase(xpp.getName())) {
					name = xpp.nextText();
				}
				else if(SCENARIO_ACT_TAG.equalsIgnoreCase(xpp.getName())) {
					processScenarioActChild(scenarioAct, xpp);
				}
			} 
			else if (eventType == XmlPullParser.END_TAG) {
				if (SCENARIO_ACT_TAG.equalsIgnoreCase(xpp.getName())) {
					done = true;
				}
			}
		}
		
		if(time != null && xmlExtension != null) {
			scenarioAct.setStartTime(Long.parseLong(time));
			scenarioAct.setXMLExtension(xmlExtension);
			scenarioAct.setName(name);
			scenario.getScenarioActList().add(scenarioAct);
		}
	}
	
	private static void processScenarioActChild(
			final IScenarioAct scenarioAct, 
			final XmlPullParser xpp) throws XmlPullParserException, IOException {
		final IScenarioAct scenarioActChild = ComponentFactory.getInstance().createInstance(IScenarioAct.class);
		
		boolean done = false;
		
		String time = null;
		String xmlExtension = null;
		String name = null;

		while (!done) {
			int eventType = xpp.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (SCENARIO_ACT_START_TIME_TAG.equalsIgnoreCase(xpp.getName())) {
					time = xpp.nextText();
				} 
				else if (SCENARIO_ACT_XML_EXTENSION_TAG.equalsIgnoreCase(xpp.getName())) {
					xmlExtension = xpp.nextText();
				} 
				else if(SCENARIO_ACT_NAME_TAG.equalsIgnoreCase(xpp.getName())) {
					name = xpp.nextText();
				}
				else if(SCENARIO_ACT_TAG.equalsIgnoreCase(xpp.getName())) {
					processScenarioActChild(scenarioActChild, xpp);
				}
			} 
			else if (eventType == XmlPullParser.END_TAG) {
				if (SCENARIO_ACT_TAG.equalsIgnoreCase(xpp.getName())) {
					done = true;
				}
			}
		}
		
		if(time != null && xmlExtension != null) {
			scenarioActChild.setStartTime(Long.parseLong(time));
			scenarioActChild.setXMLExtension(xmlExtension);
			scenarioActChild.setName(name);
			scenarioAct.setChild(scenarioActChild);
		}
	}
	
	private static void appendStartTag(final StringBuilder sb, final String tag) {
		sb.append("<").append(tag).append(">");
	}
	
	private static void appendEndTag(final StringBuilder sb, final String tag) {
		sb.append("</").append(tag).append(">");
	}
	
	/**
	 * verrify scenario file extension
	 * @param file
	 * @param scenario
	 */
	public static boolean isScenarioFile(final File file) {
		return file != null && file.getAbsolutePath().endsWith(SCENARIO_EXTENSION);
	}
	
	/**
	 * give scenario file extension
	 * @param file
	 * @param scenario
	 */
	public static String getExtension() {
		return SCENARIO_EXTENSION;
	}
}
