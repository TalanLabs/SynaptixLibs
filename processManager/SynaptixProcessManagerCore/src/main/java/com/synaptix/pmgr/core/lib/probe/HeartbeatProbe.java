package com.synaptix.pmgr.core.lib.probe;

import com.synaptix.pmgr.core.lib.ProcessEngine;

public class HeartbeatProbe extends Thread {

	private final long delay;

	private final String channel;

	private final String beat;

	public HeartbeatProbe(long delay, String channel, String beat) {
		super();

		this.delay = delay;
		this.channel = channel;
		this.beat = beat;

		setDaemon(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			while (true) {
				sleep(delay);
				ProcessEngine.handle(channel, beat);
			}
		} catch (InterruptedException e) {
		}
	}

	public void disable() {
		this.interrupt();
	}
}
