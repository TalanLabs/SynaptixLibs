package com.synaptix.client.utils;

public interface IResult<T> {

	public abstract void success(T t);

	public abstract void fail(Exception e);

}
