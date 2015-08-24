package com.synaptix.taskmanager.controller.helper;

import com.synaptix.entity.IEntity;
import com.synaptix.entity.INlsMessage;
import com.synaptix.widget.crud.controller.ICRUDManagementController;

/**
 * Interface for CRUD controllers
 * 
 * @param <E>
 */
public interface INlsCRUDManagementController<G extends IEntity & INlsMessage> extends ICRUDManagementController<G>, INlsMessageExtendedContext {

}
