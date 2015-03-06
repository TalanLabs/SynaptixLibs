package com.synaptix.pmgr.core.lib;

import com.synaptix.pmgr.core.apis.ChannelSlot;
import com.synaptix.pmgr.core.apis.Engine;
import com.synaptix.pmgr.core.apis.HandleReport;
import com.synaptix.pmgr.core.lib.handlereport.HandleReportSet;

public class BranchChannel extends AbstractChannel {
	public static final int MODE_SEQ = 0;
	public static final int MODE_PAR = 1;
	public static final int MODE_RND = 2;

	private String[] channelNames;
	private int mode;
	private int current = 0;

	/**
	 * @param name
	 */
	public BranchChannel(String name, String[] channelNames, int mode, Engine engine) {
		super(name, engine);
		this.channelNames = channelNames;
		this.mode = mode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Channel#acceptMessage(java.lang.Object)
	 */
	public HandleReport acceptMessage(Object message, ChannelSlot slot) {
		HandleReportSet report = new HandleReportSet("000", slot);
		switch (mode) {
		case (MODE_PAR):
			for (int i = 0; i < channelNames.length; i++) {
				report.add(getProcessEngine().handle(channelNames[i], message));
			}
			break;
		case (MODE_SEQ):
			String c = channelNames[current];

			current++;
			if (current >= channelNames.length) {
				current = 0;
			}
			report.add(getProcessEngine().handle(c, message));
			break;
		case (MODE_RND):
			c = channelNames[(int) Math.floor(Math.random() * channelNames.length) % channelNames.length];
			report.add(getProcessEngine().handle(c, message));
			break;
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
