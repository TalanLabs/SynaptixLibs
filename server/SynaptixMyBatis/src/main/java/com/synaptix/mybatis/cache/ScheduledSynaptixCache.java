package com.synaptix.mybatis.cache;

import java.util.concurrent.locks.ReadWriteLock;

class ScheduledSynaptixCache implements ISynaptixCache {

	private ISynaptixCache delegate;
	protected long clearInterval;
	protected long lastClear;

	public ScheduledSynaptixCache(ISynaptixCache delegate) {
		this.delegate = delegate;
		this.clearInterval = 60 * 60 * 1000; // 1 hour
		this.lastClear = System.currentTimeMillis();
	}

	public void setClearInterval(long clearInterval) {
		this.clearInterval = clearInterval;
	}

	public String getId() {
		return delegate.getId();
	}

	public int getSize() {
		clearWhenStale();
		return delegate.getSize();
	}

	public void putObject(Object key, Object object) {
		clearWhenStale();
		delegate.putObject(key, object);
	}

	public Object getObject(Object key) {
		if (clearWhenStale()) {
			return null;
		} else {
			return delegate.getObject(key);
		}
	}

	public Object removeObject(Object key) {
		clearWhenStale();
		return delegate.removeObject(key);
	}

	public void clear(boolean fire) {
		lastClear = System.currentTimeMillis();
		delegate.clear(fire);
	}

	public ReadWriteLock getReadWriteLock() {
		return delegate.getReadWriteLock();
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	private boolean clearWhenStale() {
		if (System.currentTimeMillis() - lastClear > clearInterval) {
			clear(false);
			return true;
		}
		return false;
	}

}
