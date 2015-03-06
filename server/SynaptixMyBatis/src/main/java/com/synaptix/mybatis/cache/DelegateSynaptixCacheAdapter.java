package com.synaptix.mybatis.cache;

import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.Cache;

public class DelegateSynaptixCacheAdapter implements Cache, ISynaptixCache, SynaptixCacheListener {

	private final ISynaptixCache delegate;

	public DelegateSynaptixCacheAdapter(ISynaptixCache delegate) {
		super();

		this.delegate = delegate;
	}

	@Override
	public String getId() {
		return delegate.getId();
	}

	@Override
	public Object getObject(Object key) {
		return delegate.getObject(key);
	}

	@Override
	public void putObject(Object key, Object value) {
		delegate.putObject(key, value);
	}

	@Override
	public Object removeObject(Object key) {
		return delegate.removeObject(key);
	}

	@Override
	public void clear() {
		clear(true);
	}

	@Override
	public void clear(boolean propagation) {
		delegate.clear(propagation);
	}

	@Override
	public ReadWriteLock getReadWriteLock() {
		return delegate.getReadWriteLock();
	}

	@Override
	public int getSize() {
		return delegate.getSize();
	}

	@Override
	public void cleared(String idCache, boolean propagation) {
		clear(propagation);
	}
}