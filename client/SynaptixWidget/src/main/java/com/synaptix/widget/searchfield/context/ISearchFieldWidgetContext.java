package com.synaptix.widget.searchfield.context;

import java.util.List;

import com.synaptix.common.util.IResultCallback;

public interface ISearchFieldWidgetContext<E> {

	/**
	 * Search a value
	 * 
	 * @param name
	 * @param resultCallback
	 */
	public void search(String name, IResultCallback<E> resultCallback);

	/**
	 * Search many values
	 * 
	 * @param name
	 * @param resultCallback
	 */
	public void searchMany(String name, IResultCallback<List<E>> resultCallback);

	/**
	 * Suggest a list of value
	 * 
	 * @param name
	 * @param resultCallback
	 */
	public void suggest(String name, IResultCallback<List<E>> resultCallback);

	/**
	 * Convert
	 * 
	 * @param value
	 * @return
	 */
	public void convert(E value, IResultCallback<E> resultCallback);

}
