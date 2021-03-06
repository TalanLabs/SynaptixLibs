package com.synaptix.pmgr.core.lib.probe;

import com.synaptix.pmgr.core.apis.Engine;
import com.synaptix.pmgr.core.lib.ProcessEngine;
import com.synaptix.pmgr.core.lib.ProcessingChannel.Agent;

import it.sauronsoftware.cron4j.Scheduler;

public class CronAgent implements Agent {

	private final String channel;

	private final String schedulingPattern;

	private Scheduler scheduler;

	public CronAgent(String channel, String schedulingPattern) {
		super();

		this.channel = channel;
		this.schedulingPattern = schedulingPattern;
	}

	@Override
	public void work(Object message, Engine processEngine) {
		synchronized (this) {
			if ("STOP".equals(message)) {
				if (scheduler != null) {
					scheduler.stop();
					scheduler = null;
				}
			} else if (scheduler == null) {
				scheduler = new Scheduler();
				scheduler.setDaemon(true);
				scheduler.schedule(schedulingPattern, new Runnable() {
					@Override
					public void run() {
						ProcessEngine.handle(channel, "Cron");
					}
				});
				scheduler.start();
			}
		}
	}
}
