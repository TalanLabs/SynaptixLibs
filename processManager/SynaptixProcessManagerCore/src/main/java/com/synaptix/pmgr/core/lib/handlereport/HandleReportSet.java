package com.synaptix.pmgr.core.lib.handlereport;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.pmgr.core.apis.ChannelSlot;
import com.synaptix.pmgr.core.apis.HandleReport;

public class HandleReportSet extends BaseHandleReport {
	List<HandleReport> reports;
	
	/**
	 * @param sid
	 * @param slot
	 */
	public HandleReportSet(String sid, ChannelSlot slot) {
		super(sid, slot);
		reports = new ArrayList<HandleReport>();
	}

	public HandleReport get(int i){
		return reports.get(i);
	}
	public void add(HandleReport r){
		reports.add(r);
	}
	public int size(){
		return reports.size();
	}
}
