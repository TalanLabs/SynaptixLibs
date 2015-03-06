package com.synaptix.core.controller;

import javax.swing.JComponent;

import com.synaptix.core.dock.IViewDockable;
import com.synaptix.core.menu.IViewRibbonTask;
import com.synaptix.core.option.IViewOption;

public interface IControllerDockable {

	public abstract void setEnabledMenuById(String id, boolean enabled);

	public abstract void setEnabledMenuItemById(String id, boolean enabled);

	public abstract void registerDockable(IViewDockable viewDockable);

	public abstract void showDockable(String id);

	public abstract JComponent getDockable(String id);

	public abstract boolean isDockableClosed(String id);

	public abstract void registerOption(IViewOption viewOption);

	public abstract void registryRibbonTask(IViewRibbonTask viewRibbonTask);

	public abstract void showRibbonTask(String id);

	public abstract void hideRibbonTask(String id);

}