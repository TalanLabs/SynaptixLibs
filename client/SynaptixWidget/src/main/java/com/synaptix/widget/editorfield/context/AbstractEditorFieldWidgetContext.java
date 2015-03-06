package com.synaptix.widget.editorfield.context;

import com.synaptix.common.util.IResultCallback;

public abstract class AbstractEditorFieldWidgetContext<E> implements IEditorFieldWidgetContext<E> {

	@Override
	public void editor(E value, boolean readOnly, IResultCallback<E> resultCallback) {
	}

}
