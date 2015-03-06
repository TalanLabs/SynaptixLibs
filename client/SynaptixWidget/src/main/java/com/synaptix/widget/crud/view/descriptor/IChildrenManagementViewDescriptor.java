package com.synaptix.widget.crud.view.descriptor;

import java.util.List;

import com.synaptix.component.IComponent;

public interface IChildrenManagementViewDescriptor<E extends IComponent, C extends IComponent> {

	/**
	 * Set children
	 * 
	 * @param parentComponent
	 * @param childComponents
	 */
	public void setChildComponents(E parentComponent, List<C> childComponents);

}
