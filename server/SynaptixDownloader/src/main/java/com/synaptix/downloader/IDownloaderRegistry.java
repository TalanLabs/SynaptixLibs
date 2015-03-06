package com.synaptix.downloader;

public interface IDownloaderRegistry {

	/**
	 * Registry a downloader process, download only one time
	 * 
	 * @param downloaderProcess
	 *            not null
	 * @return get a unique path
	 */
	public String registry(IDownloaderProcess downloaderProcess);

	/**
	 * Registry a downloader process for 24h
	 * 
	 * @param downloaderProcess
	 * @return
	 */
	public String registryDelay(IDownloaderProcess downloaderProcess);

	/**
	 * Get a downloader process
	 * 
	 * @param key
	 *            is not null
	 * @return
	 */
	public IDownloaderProcess get(String key);

}
