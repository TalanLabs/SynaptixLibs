package com.synaptix.taskmanager.controller.helper;

import com.synaptix.entity.IEntity;
import com.synaptix.entity.INlsMessage;
import com.synaptix.widget.crud.controller.ICRUDWithChildrenManagementController;

/**
 * Interface for CRUD controllers
 * 
 * @param <E>
 */
public interface INlsCRUDWithChildrenManagementController<G extends IEntity & INlsMessage, C extends IEntity> extends ICRUDWithChildrenManagementController<G, C>, INlsMessageExtendedContext {

}
