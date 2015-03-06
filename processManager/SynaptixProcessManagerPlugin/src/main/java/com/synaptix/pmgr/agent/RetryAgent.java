package com.synaptix.pmgr.agent;

import com.synaptix.pmgr.core.apis.Engine;
import com.synaptix.pmgr.core.lib.ProcessEngine;
import com.synaptix.pmgr.core.lib.ProcessingChannel.Agent;
import com.synaptix.pmgr.trigger.message.AbstractFlux;

public class RetryAgent implements Agent {

	@Override
	public void work(Object message, Engine processEngine) {
		if (message instanceof AbstractFlux) {
			AbstractFlux flux = (AbstractFlux) message;
			if (flux.retry()) {
				ProcessEngine.handle(flux.getName(), message);
			}
		}
	}
}
