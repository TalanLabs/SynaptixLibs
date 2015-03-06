package com.synaptix.widget.view;

import com.synaptix.component.IComponent;
import com.synaptix.widget.component.controller.context.ISearchComponentsContext;

public interface IViewDescriptor<E extends IComponent> {

	/**
	 * Set search components context
	 * 
	 * @param searchComponentsContext
	 */
	public void setSearchComponentsContext(ISearchComponentsContext searchComponentsContext);

	/**
	 * Create view descriptor, Called after setSearchComponentsContext
	 */
	public void create();

}
