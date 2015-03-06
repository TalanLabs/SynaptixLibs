package com.synaptix.downloader.impl;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.synaptix.downloader.IDownloaderProcess;
import com.synaptix.downloader.IDownloaderRegistry;

public class DefaultDownloaderRegistry implements IDownloaderRegistry {

	private final String downloaderServletPath;

	private Cache<String, MyValue> cache;

	private AtomicInteger sequence;

	@Inject
	public DefaultDownloaderRegistry(@Named("DownloaderServletPath") String downloaderServletPath) {
		super();
		this.downloaderServletPath = downloaderServletPath;

		this.sequence = new AtomicInteger();

		this.cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build();
	}

	@Override
	public String registry(IDownloaderProcess downloaderProcess) {
		if (downloaderProcess == null) {
			throw new IllegalArgumentException("key is null");
		}
		String key = newKey();
		this.cache.put(key, new MyValue(downloaderProcess, true));
		return new StringBuilder(downloaderServletPath).append("?key=").append(key).toString();
	}

	@Override
	public String registryDelay(IDownloaderProcess downloaderProcess) {
		if (downloaderProcess == null) {
			throw new IllegalArgumentException("key is null");
		}
		String key = newKey();
		this.cache.put(key, new MyValue(downloaderProcess, false));
		return new StringBuilder(downloaderServletPath).append("?key=").append(key).toString();
	}

	@Override
	public IDownloaderProcess get(String key) {
		if (key == null) {
			throw new IllegalArgumentException("key is null");
		}
		MyValue myValue = cache.getIfPresent(key);
		if (myValue != null) {
			if (myValue.remove) {
				cache.invalidate(key);
			}
			return myValue.downloaderProcess;
		}
		return null;
	}

	private synchronized String newKey() {
		return new StringBuilder(UUID.randomUUID().toString()).append(Integer.toHexString(sequence.getAndIncrement())).toString();
	}

	private class MyValue {

		final IDownloaderProcess downloaderProcess;

		final boolean remove;

		public MyValue(IDownloaderProcess downloaderProcess, boolean remove) {
			super();
			this.downloaderProcess = downloaderProcess;
			this.remove = remove;
		}
	}
}
