package com.synaptix.widget.searchfield.context;

import java.util.List;

import com.synaptix.common.util.IResultCallback;

public abstract class AbstractSearchFieldWidgetContext<E> implements ISearchFieldWidgetContext<E> {

	@Override
	public void suggest(String name, IResultCallback<List<E>> resultCallback) {
		resultCallback.setResult(null);
	}

	@Override
	public void searchMany(String name, IResultCallback<List<E>> resultCallback) {
		resultCallback.setResult(null);
	}

	@Override
	public void convert(E value, IResultCallback<E> resultCallback) {
		resultCallback.setResult(value);
	}
}
