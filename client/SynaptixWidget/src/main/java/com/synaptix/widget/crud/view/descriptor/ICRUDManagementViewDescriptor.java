package com.synaptix.widget.crud.view.descriptor;

import java.util.List;

import com.synaptix.entity.IEntity;
import com.synaptix.widget.component.view.IComponentsManagementViewDescriptor;

public interface ICRUDManagementViewDescriptor<E extends IEntity> extends IComponentsManagementViewDescriptor<E> {

	public List<E> getComponentList();

	public void selectLine(int line);

}
