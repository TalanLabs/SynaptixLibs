package com.synaptix.widget.component.controller.context;

import java.util.List;

import com.synaptix.common.util.IResultCallback;

public interface ISearchDialogContext<E> {

	public void searchOne(IResultCallback<E> resultCallback);

	public void searchList(IResultCallback<List<E>> resultCallback);

}
