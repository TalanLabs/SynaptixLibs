package com.synaptix.mybatis.cache;

import java.util.concurrent.locks.ReadWriteLock;

public interface ISynaptixCache {

	String getId();

	int getSize();

	void putObject(Object key, Object value);

	Object getObject(Object key);

	Object removeObject(Object key);

	void clear(boolean propagation);

	ReadWriteLock getReadWriteLock();

}
