package com.synaptix.smackx.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.PacketCollector;

import com.synaptix.client.service.IReWaitResult;
import com.synaptix.smackx.service.packet.ResultServiceIQ;

public class WaitResultServiceIQCollectorResult {

	private static final Log log = LogFactory.getLog(WaitResultServiceIQCollectorResult.class);

	private String currentID;

	private PacketCollector collector;

	private IReWaitResult reWaitResult;

	public WaitResultServiceIQCollectorResult(String currentID, PacketCollector collector, IReWaitResult reWaitResult) {
		this.currentID = currentID;
		this.collector = collector;
		this.reWaitResult = reWaitResult;
	}

	public synchronized ResultServiceIQ waitResult(final long timeout) {
		log.debug("Wait message currentID : " + currentID + " timeout : " + timeout);

		ResultServiceIQ message = null;
		boolean isWaitEncore = true;
		try {
			while (isWaitEncore && message == null) {
				message = (ResultServiceIQ) collector.nextResult(timeout);
				if (message == null) {
					isWaitEncore = reWaitResult != null ? reWaitResult.isReWaitResult(timeout) : false;
				}
			}
			collector.cancel();
		} catch (Exception e) {
		}

		return message;
	}
}
