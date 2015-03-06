package com.synaptix.mybatis.cache;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EmptySynaptixCache implements ISynaptixCache {

	private final String id;

	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	public EmptySynaptixCache(String id) {
		super();
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public void putObject(Object key, Object value) {
	}

	@Override
	public Object getObject(Object key) {
		return null;
	}

	@Override
	public Object removeObject(Object key) {
		return null;
	}

	@Override
	public void clear(boolean fire) {
	}

	@Override
	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}
}
