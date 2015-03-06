package com.synaptix.client.view;

public interface IWaitWorker {

	public abstract boolean cancel(boolean mayInterruptIfRunning);

	public abstract boolean isCancelled();

	public abstract boolean isDone();
	
}
