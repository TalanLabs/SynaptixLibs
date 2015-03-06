package com.synaptix.core.controller;

import javax.swing.JComponent;

import com.synaptix.core.dock.IViewDockable;
import com.synaptix.core.menu.IViewRibbonTask;
import com.synaptix.core.option.IViewOption;

public abstract class AbstractControllerDockable implements IControllerDockable {

	protected IControllerDockable controller;

	public AbstractControllerDockable(IControllerDockable controller) {
		this.controller = controller;
	}

	public void setEnabledMenuById(String id, boolean enabled) {
		controller.setEnabledMenuById(id, enabled);
	}

	public void setEnabledMenuItemById(String id, boolean enabled) {
		controller.setEnabledMenuItemById(id, enabled);
	}

	public void registerDockable(IViewDockable viewDockable) {
		controller.registerDockable(viewDockable);
	}

	public void showDockable(String id) {
		controller.showDockable(id);
	}

	public JComponent getDockable(String id) {
		return controller.getDockable(id);
	}

	public boolean isDockableClosed(String id) {
		return controller.isDockableClosed(id);
	}

	public void registerOption(IViewOption viewOption) {
		controller.registerOption(viewOption);
	}

	public void registryRibbonTask(IViewRibbonTask viewRibbonTask) {
		controller.registryRibbonTask(viewRibbonTask);
	}

	public void showRibbonTask(String id) {
		controller.showDockable(id);
	}

	public void hideRibbonTask(String id) {
		controller.hideRibbonTask(id);
	}

}
