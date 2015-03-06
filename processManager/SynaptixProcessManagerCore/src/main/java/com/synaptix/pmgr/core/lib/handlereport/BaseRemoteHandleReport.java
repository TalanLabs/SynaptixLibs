package com.synaptix.pmgr.core.lib.handlereport;

import com.synaptix.pmgr.core.apis.ChannelSlot;
import com.synaptix.pmgr.core.apis.RemoteHandleReport;

public class BaseRemoteHandleReport
	extends BaseHandleReport
	implements RemoteHandleReport {
	private String rebn;
	/**
	 * @param sid
	 * @param slot
	 */
	public BaseRemoteHandleReport(String sid, ChannelSlot slot, String rebn) {
		super(sid, slot);
		this.rebn = rebn;
	}

	/* (non-Javadoc)
	 * @see com.synaptix.pmgr2.apis.RemoteHandleReport#getRemoteEngineBindName()
	 */
	public String getRemoteEngineBindName() {
		return rebn;
	}

}
