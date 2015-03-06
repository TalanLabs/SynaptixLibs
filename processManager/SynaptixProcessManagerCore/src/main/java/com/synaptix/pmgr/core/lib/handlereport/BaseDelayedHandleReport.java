package com.synaptix.pmgr.core.lib.handlereport;

import com.synaptix.pmgr.core.apis.ChannelSlot;
import com.synaptix.pmgr.core.apis.DelayedHandleReport;

public class BaseDelayedHandleReport
	extends BaseHandleReport
	implements DelayedHandleReport {

	/**
	 * @param sid
	 * @param slot
	 */
	public BaseDelayedHandleReport(String sid, ChannelSlot slot) {
		super(sid, slot);
	}

}
