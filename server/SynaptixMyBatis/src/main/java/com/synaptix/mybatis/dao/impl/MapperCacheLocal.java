package com.synaptix.mybatis.dao.impl;

import java.util.HashMap;
import java.util.Map;

public class MapperCacheLocal {

	private ThreadLocal<Map<Class<?>, Object>> mapperThreadLocal = new ThreadLocal<Map<Class<?>, Object>>();

	public MapperCacheLocal() {
		super();
	}

	/**
	 * Get a mapper cache for current thread
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getMapper(Class<T> type) {
		T res = null;
		Map<Class<?>, Object> map = mapperThreadLocal.get();
		if (map != null) {
			res = (T) map.get(type);
		}
		return res;
	}

	/**
	 * Put a mapper cache for current thread
	 * 
	 * @param type
	 * @param instance
	 */
	public <T> void putMapper(Class<T> type, T instance) {
		Map<Class<?>, Object> map = mapperThreadLocal.get();
		if (map == null) {
			map = new HashMap<Class<?>, Object>();
			mapperThreadLocal.set(map);
		}
		map.put(type, instance);
	}

	/**
	 * Remove mapper cache for current thread
	 */
	public void remove() {
		mapperThreadLocal.remove();
	}
}
