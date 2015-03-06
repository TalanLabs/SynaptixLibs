package com.synaptix.pmgr.core.lib.handlereport;

import com.synaptix.pmgr.core.apis.ChannelSlot;
import com.synaptix.pmgr.core.apis.HandleReport;

public class BaseHandleReport implements HandleReport {
	private String sid;
	private ChannelSlot slot;
	/**
	 * 
	 */
	public BaseHandleReport(String sid, ChannelSlot slot) {
		this.sid = sid;
		this.slot = slot;
	}

	/* (non-Javadoc)
	 * @see com.synaptix.pmgr2.apis.HandleReport#getSessionID()
	 */
	public String getSessionID() {
		return sid;
	}

	/* (non-Javadoc)
	 * @see com.synaptix.pmgr2.apis.HandleReport#getChannelSlot()
	 */
	public ChannelSlot getChannelSlot() {
		return slot;
	}

}
