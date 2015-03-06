package com.synaptix.server.vysper.xmpp.hack;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.vysper.xmpp.protocol.QueuedStanzaProcessor;
import org.apache.vysper.xmpp.protocol.StanzaProcessor;

public class SynaptixQueuedStanzaProcessor extends QueuedStanzaProcessor {

	public SynaptixQueuedStanzaProcessor(StanzaProcessor stanzaProcessor) {
		this(stanzaProcessor, 10, 20, 2 * 60 * 1000);
	}

	public SynaptixQueuedStanzaProcessor(StanzaProcessor stanzaProcessor, int coreThreadCount, int maxThreadCount, int threadTimeoutSeconds) {
		super(stanzaProcessor);

		if (this.executor != null) {
			this.executor.shutdown();
		}
		this.executor = new ThreadPoolExecutor(coreThreadCount, maxThreadCount, threadTimeoutSeconds, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}
}
