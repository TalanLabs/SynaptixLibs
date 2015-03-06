package com.synaptix.service.hierarchical.model;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface IHierarchicalTitle<E extends IComponent> extends IComponent {

	public E getTitleComponent();

	public void setTitleComponent(final E titleComponent);
}
