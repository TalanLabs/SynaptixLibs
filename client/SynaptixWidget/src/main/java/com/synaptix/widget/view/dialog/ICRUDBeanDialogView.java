package com.synaptix.widget.view.dialog;

import com.synaptix.entity.IEntity;
import com.synaptix.widget.component.controller.dialog.ICRUDDialogController;

/**
 * A CRUD bean dialog view which allows the user to browse between the entities
 * 
 * @author Nicolas P
 *
 * @param <E>
 */
public interface ICRUDBeanDialogView<E extends IEntity> extends IBeanDialogView<E> {

	public void setCRUDDialogContext(ICRUDDialogController<E> crudDialogContext);

}
