package com.synaptix.widget.component.controller.context;

import com.synaptix.client.view.IView;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.IEntity;
import com.synaptix.widget.component.controller.dialog.AbstractCRUDDialogController;
import com.synaptix.widget.editorfield.context.AbstractEditorFieldWidgetContext;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractComponentEditorFieldWidgetContext<V extends ISynaptixViewFactory, E extends IEntity> extends AbstractEditorFieldWidgetContext<E> {

	private final V viewFactory;

	private final IView parent;

	private final Class<E> componentClass;

	public AbstractComponentEditorFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass) {
		super();

		this.viewFactory = viewFactory;
		this.parent = parent;
		this.componentClass = componentClass;
	}

	protected abstract AbstractCRUDDialogController<E> createDefaultEditorComponentDialogController(V viewFactory);

	@Override
	public final void editor(E value, boolean readOnly, IResultCallback<E> resultCallback) {
		AbstractCRUDDialogController<E> dialogController = createDefaultEditorComponentDialogController(viewFactory);
		if (readOnly) {
			dialogController.showEntity(parent, value != null ? value : ComponentFactory.getInstance().createInstance(componentClass));
		} else {
			if (value == null) {
				dialogController.addEntity(parent, resultCallback);
			} else {
				dialogController.editEntity(parent, value, resultCallback);
			}
		}
	}
}
