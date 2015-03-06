package com.synaptix.widget.component.controller;

import com.synaptix.component.IComponent;
import com.synaptix.widget.component.controller.context.ISearchComponentsContext;
import com.synaptix.widget.core.controller.IController;

public interface IComponentsManagementController<E extends IComponent> extends ISearchComponentsContext, IController {

}
