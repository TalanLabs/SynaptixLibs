package com.synaptix.redpepper.automation.elements.facade;

/**
 * 
 * @author skokaina
 * 
 * @param <T>
 */
public interface HasInputBase<T> {
	public void setInput(T e);

	public T getValue();
}
