package com.synaptix.widget.crud.view.descriptor;

import com.synaptix.entity.IEntity;

public interface ICRUDWithChildrenManagementViewDescriptor<E extends IEntity, C extends IEntity> extends ICRUDManagementViewDescriptor<E>, IChildrenManagementViewDescriptor<E, C> {

}
