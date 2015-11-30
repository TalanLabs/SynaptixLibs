package com.synaptix.client.service;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class Cache {

	private Map<String, Map<Method, Map<Arguments, SoftReference<Object>>>> cacheDataMap = new HashMap<String, Map<Method, Map<Arguments, SoftReference<Object>>>>();
	
	private Map<String, Map<Method, Integer>> cacheDelayMap = new HashMap<String, Map<Method, Integer>>();
	
	private Map<String, Map<Method, Date>> cacheFirstAccessMap = new HashMap<String, Map<Method, Date>>();

	private static final <T, V> boolean hasElement(final T element, final Map<T, V> map) {
		return map.containsKey(element) && map.get(element) != null;
	}
	
	public void putServiceFactoryName(final String serviceFactoryName) {
		if(!cacheDataMap.containsKey(serviceFactoryName)) {
			cacheDataMap.put(serviceFactoryName, new HashMap<Method, Map<Arguments, SoftReference<Object>>>());
		}
		if(!cacheDelayMap.containsKey(serviceFactoryName)) {
			cacheDelayMap.put(serviceFactoryName, new HashMap<Method, Integer>());
		}
		if(!cacheFirstAccessMap.containsKey(serviceFactoryName)) {
			cacheFirstAccessMap.put(serviceFactoryName, new HashMap<Method, Date>());
		}
	}

	public void putCacheData(
			final String serviceFactoryName,
			final Method method,
			final Object[] args,
			final Object result) {
		putServiceFactoryName(serviceFactoryName);
		if(	hasElement(method, cacheDelayMap.get(serviceFactoryName))) {
			if(!hasElement(method, cacheDataMap.get(serviceFactoryName))) {
				cacheDataMap.get(serviceFactoryName).put(method, new HashMap<Arguments, SoftReference<Object>>());
			}
			if(!hasElement(method, cacheFirstAccessMap.get(serviceFactoryName))) {
				cacheDataMap.get(serviceFactoryName).get(method).put(new Arguments(args), new SoftReference<Object>(result));
				cacheFirstAccessMap.get(serviceFactoryName).put(method, new Date());
			}
		}
	}

	public void putCacheDelay(
			final String serviceFactoryName,
			final Method method, 
			final Integer delay) {
		putServiceFactoryName(serviceFactoryName);
		cacheDelayMap.get(serviceFactoryName).put(method, delay);
	}
	
	public void putCacheFirstAccess(
			final String serviceFactoryName,
			final Method method, 
			final Date firstAccess) {
		putServiceFactoryName(serviceFactoryName);
		cacheFirstAccessMap.get(serviceFactoryName).put(method, firstAccess);
	}
	
	public Object getCacheData(
			final String serviceFactoryName,
			final Method method,
			final Object[] args) {
		if(cacheDataMap.containsKey(serviceFactoryName)) {
			if(cacheDelayMap.get(serviceFactoryName).containsKey(method)) {
				if(cacheFirstAccessMap.get(serviceFactoryName).containsKey(method)) {
					if(delayIsPassed(
							cacheFirstAccessMap.get(serviceFactoryName).get(method), 
							cacheDelayMap.get(serviceFactoryName).get(method)
					)) {
						flushCache(serviceFactoryName, method);
						return null;
					}
				}
			}
			if(cacheDataMap.get(serviceFactoryName).containsKey(method)) {
				final SoftReference<Object> softReference = cacheDataMap.get(serviceFactoryName).get(method).get(new Arguments(args));
				if(softReference != null) {
					return softReference.get();
				}
			}
			return null;
		}
		throw new IllegalArgumentException("no serviceFactoryName = "+serviceFactoryName);
	}
	
	private final boolean delayIsPassed(
			final Date dateFirstAccess,
			final Integer delay
	) {
		return ((int)((System.currentTimeMillis() - dateFirstAccess.getTime()) / (1000l * 60l))) > delay; 
	}
	
	public Integer getCacheDelay(
			final String serviceFactoryName,
			final Method method 
			) {
		if(cacheDelayMap.containsKey(serviceFactoryName)) {
			return cacheDelayMap.get(serviceFactoryName).get(method);
		}
		throw new IllegalArgumentException("no serviceFactoryName = "+serviceFactoryName);
	}
	
	public Date getCacheFirstAccess(
			final String serviceFactoryName,
			final Method method) {
		if(cacheFirstAccessMap.containsKey(serviceFactoryName)) {
			return cacheFirstAccessMap.get(serviceFactoryName).get(method);
		}
		throw new IllegalArgumentException("no serviceFactoryName = "+serviceFactoryName);
	}
	
	public void flushCache(
			final String serviceFactoryName,
			final Class<?> clazz) {
		if(clazz != null) {
			if(cacheDataMap.containsKey(serviceFactoryName)) {
				final Method[] methods = clazz.getMethods(); 
				if(methods != null && methods.length > 0) {
					for(Method method : methods) {
						flushCache(serviceFactoryName, method);
					}
				}
			}
			throw new IllegalArgumentException("no serviceFactoryName = "+serviceFactoryName);
		}
		throw new IllegalArgumentException("class is null = "+serviceFactoryName);
	}

	public Object flushCache(
			final String serviceFactoryName,
			final Method method) {
		if(cacheDataMap.containsKey(serviceFactoryName)) {
			cacheFirstAccessMap.get(serviceFactoryName).remove(method);
			return cacheDataMap.get(serviceFactoryName).remove(method);
		}
		throw new IllegalArgumentException("no serviceFactoryName = "+serviceFactoryName);
	}
	
	public void flushAllCache(
			final String serviceFactoryName) {
		if(cacheDataMap.containsKey(serviceFactoryName)) {
			final Set<Entry<Method, Map<Arguments, SoftReference<Object>>>> setServiceName =  cacheDataMap.get(serviceFactoryName).entrySet();
			if(setServiceName != null && setServiceName.size() > 0) {
				final Map<Method, Map<Arguments, SoftReference<Object>>> dataMap = cacheDataMap.get(serviceFactoryName);
				final Map<Method, Date> dateMap = cacheFirstAccessMap.get(serviceFactoryName);
				Method curMethod;
				for(Entry<Method, Map<Arguments, SoftReference<Object>>> entry : setServiceName) {
					curMethod = entry.getKey();
					dataMap.remove(curMethod);
					dateMap.remove(curMethod);
				}
			}
		}
		throw new IllegalArgumentException("no serviceFactoryName = "+serviceFactoryName);
	}
	
	public void flushAllCache() {
		final Set<Entry<String, Map<Method, Map<Arguments, SoftReference<Object>>>>> setData =  cacheDataMap.entrySet();
		if(setData != null && setData.size() > 0) {
			for(Entry<String, Map<Method, Map<Arguments, SoftReference<Object>>>> entryData : setData) {
				entryData.getValue().clear();
			}
			final Set<Entry<String, Map<Method, Date>>> setDate =  cacheFirstAccessMap.entrySet();
			if(setDate != null && setDate.size() > 0) {
				for(Entry<String, Map<Method, Date>> entryDate : setDate) {
					entryDate.getValue().clear();
				}
			}
		}
	}
}
