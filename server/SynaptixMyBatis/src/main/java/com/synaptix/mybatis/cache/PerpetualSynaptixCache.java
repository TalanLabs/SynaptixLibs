package com.synaptix.mybatis.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;

class PerpetualSynaptixCache implements ISynaptixCache {

	private String id;

	private Map<Object, Object> cache = new ConcurrentHashMap<Object, Object>();

	private ReadWriteLock readWriteLock = new MyReadWriteLock();

	public PerpetualSynaptixCache(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public int getSize() {
		return cache.size();
	}

	public void putObject(Object key, Object value) {
		cache.put(key, value);
	}

	public Object getObject(Object key) {
		return cache.get(key);
	}

	public Object removeObject(Object key) {
		return cache.remove(key);
	}

	public void clear(boolean fire) {
		cache.clear();
	}

	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

	public boolean equals(Object o) {
		if (getId() == null)
			throw new CacheException("Cache instances require an ID.");
		if (this == o)
			return true;
		if (!(o instanceof Cache))
			return false;

		Cache otherCache = (Cache) o;
		return getId().equals(otherCache.getId());
	}

	public int hashCode() {
		if (getId() == null)
			throw new CacheException("Cache instances require an ID.");
		return getId().hashCode();
	}

	private final class MyReadWriteLock implements ReadWriteLock {

		private MyLock lock = new MyLock();

		@Override
		public Lock readLock() {
			return lock;
		}

		@Override
		public Lock writeLock() {
			return lock;
		}
	}

	private final class MyLock implements Lock {

		@Override
		public void lock() {
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
		}

		@Override
		public boolean tryLock() {
			return true;
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			return true;
		}

		@Override
		public void unlock() {
		}

		@Override
		public Condition newCondition() {
			return null;
		}
	}
}
