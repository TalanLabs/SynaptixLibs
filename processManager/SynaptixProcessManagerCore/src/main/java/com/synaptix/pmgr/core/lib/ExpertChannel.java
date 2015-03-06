package com.synaptix.pmgr.core.lib;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.pmgr.core.apis.ChannelSlot;
import com.synaptix.pmgr.core.apis.Engine;
import com.synaptix.pmgr.core.apis.HandleReport;
import com.synaptix.pmgr.core.lib.handlereport.HandleReportSet;

public class ExpertChannel extends AbstractChannel {
	List<Rule> rules;

	/**
	 * @param name
	 */
	public ExpertChannel(String name, Engine engine) {
		super(name, engine);
		rules = new ArrayList<Rule>();
	}

	public void addRule(Rule rule) {
		rules.add(rule);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Channel#acceptMessage(java.lang.Object)
	 */
	public HandleReport acceptMessage(Object message, ChannelSlot slot) {
		HandleReportSet report = new HandleReportSet("000", slot);
		for (int i = 0; i < rules.size(); i++) {
			Rule r = rules.get(i);
			String cn = r.chooseChannelName(message);
			if (cn != null) {
				report.add(getProcessEngine().handle(cn, message));
			}
		}
		return report;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Channel#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public void setAvailable(boolean available) {
		// ?!
	}

	@Override
	public int getNbWorking() {
		return -1; // ?!
	}

	@Override
	public int getNbWaiting() {
		return -1; // ?!
	}

	public static interface Rule {
		public String chooseChannelName(Object message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Channel#isLocal()
	 */
	@Override
	public boolean isLocal() {
		return true;
	}

}
