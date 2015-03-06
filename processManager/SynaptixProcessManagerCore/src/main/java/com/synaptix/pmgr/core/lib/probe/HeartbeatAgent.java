package com.synaptix.pmgr.core.lib.probe;

import com.synaptix.pmgr.core.apis.Engine;
import com.synaptix.pmgr.core.lib.ProcessingChannel.Agent;

public class HeartbeatAgent implements Agent {

	private final String channel;

	private final String beat;

	private final long delay;

	private HeartbeatProbe probe;

	public HeartbeatAgent(String channel, String beat, long delay) {
		super();

		this.channel = channel;
		this.beat = beat;
		this.delay = delay;
	}

	@Override
	public void work(Object message, Engine processEngine) {
		synchronized (this) {
			if ("STOP".equals(message)) {
				probe.disable();
				probe = null;
			} else if (probe == null) {
				probe = new HeartbeatProbe(delay, channel, beat);
				probe.start();
			}
		}
	}
}
