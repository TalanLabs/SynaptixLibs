package com.synaptix.pmgr.core.lib.handlereport;

import com.synaptix.pmgr.core.apis.ChannelSlot;
import com.synaptix.pmgr.core.apis.LocalHandleReport;

public class BaseLocalHandleReport
	extends BaseHandleReport
	implements LocalHandleReport {
	private Runnable runnable;
	/**
	 * @param sid
	 * @param slot
	 */
	public BaseLocalHandleReport(String sid, ChannelSlot slot, Runnable runnable) {
		super(sid, slot);
		this.runnable = runnable;
	}

	/* (non-Javadoc)
	 * @see com.synaptix.pmgr2.apis.LocalHandleReport#getThread()
	 */
	public Runnable getRunnable() {
		return runnable;
	}

}
