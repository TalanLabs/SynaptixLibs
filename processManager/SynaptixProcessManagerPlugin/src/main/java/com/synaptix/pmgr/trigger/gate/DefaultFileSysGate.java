package com.synaptix.pmgr.trigger.gate;

import java.io.File;

import org.apache.commons.logging.Log;

import com.synaptix.pmgr.trigger.injector.MessageInjector;

public class DefaultFileSysGate extends AbstractFileSysGate {

	public DefaultFileSysGate(String name, File ef, File af, File rf, File retryf, File arf, long retryPeriod, MessageInjector injector, Log logger) {
		super(name, ef, af, rf, retryf, arf, retryPeriod, injector);
		super.setLogger(logger);
		super.init();
	}

	public DefaultFileSysGate(String name, File ef, File af, File rf, File retryf, File arf, long retryPeriod, MessageInjector injector) {
		super(name, ef, af, rf, retryf, arf, retryPeriod, injector);
		super.init();
	}

	public DefaultFileSysGate(String name, File rootDir, long retryPeriod, MessageInjector injector) {
		this(name, rootDir, new File(rootDir + "/accepted"), new File(rootDir + "/rejected"), new File(rootDir + "/retry"), new File(rootDir + "/archive"), retryPeriod, injector);
	}

	public DefaultFileSysGate(String name, File rootDir, long retryPeriod, MessageInjector injector, Log logger) {

		this(name, rootDir, new File(rootDir.getAbsolutePath() + "/accepted"), new File(rootDir.getAbsolutePath() + "/rejected"), new File(rootDir.getAbsolutePath() + "/retry"), new File(
				rootDir.getAbsolutePath() + "/archive"), retryPeriod, injector, logger);
	}

}