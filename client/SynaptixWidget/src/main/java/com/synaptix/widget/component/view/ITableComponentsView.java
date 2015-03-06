package com.synaptix.widget.component.view;

import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.component.IComponent;

public interface ITableComponentsView<E extends IComponent> extends IView {

	/**
	 * Set a list of component
	 * 
	 * @param components
	 */
	public void setComponents(List<E> components);

}
