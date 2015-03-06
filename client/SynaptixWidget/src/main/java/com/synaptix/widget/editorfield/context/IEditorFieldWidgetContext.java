package com.synaptix.widget.editorfield.context;

import com.synaptix.common.util.IResultCallback;

public interface IEditorFieldWidgetContext<E> {

	/**
	 * Editor a value
	 * 
	 * @param value
	 * @param readOnly
	 * @param resultCallback
	 */
	public abstract void editor(E value, boolean readOnly, IResultCallback<E> resultCallback);

}
