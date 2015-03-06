package com.synaptix.client.utils;

public interface IDownload<E> {

	public abstract void call(E e) throws Exception;

}
