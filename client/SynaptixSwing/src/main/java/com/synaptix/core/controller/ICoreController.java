package com.synaptix.core.controller;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.Icon;

import com.synaptix.client.view.Perspective;
import com.synaptix.core.dock.IViewDockable;
import com.synaptix.core.event.CoreControllerListener;
import com.synaptix.core.menu.IViewMenu;
import com.synaptix.core.menu.IViewMenuItem;
import com.synaptix.core.menu.IViewMenuItemAction;
import com.synaptix.core.menu.IViewRibbonTask;
import com.synaptix.core.option.IViewOption;

public interface ICoreController {

	public enum DirectionComponent {
		North, South, West, East
	}

	public abstract void setAskQuit(boolean askQuit);

	public abstract boolean isAskQuit();

	public abstract void setEnabledShowViewsMenuItem(boolean enabled);

	public abstract void setEnabledMenuById(String id, boolean enabled);

	public abstract void setEnabledMenuItemById(String id, boolean enabled);

	public abstract boolean isEnabledMenuById(String id);

	public abstract boolean isEnabledMenuItemById(String id);

	public abstract void addMenu(String id, String name, String[] separators);

	public abstract void addMenu(String id, String name, String[] separators,
			String path);

	public abstract void addMenu(String id, String name, String[] separators,
			String path, int position);

	public abstract void addMenuItem(String id, String name, String path,
			IViewMenuItemAction menuItemAction);

	public abstract IViewMenu[] getViewMenus();

	public abstract IViewMenu getViewMenu(String id);

	public abstract IViewMenuItem[] getViewMenuItems();

	public abstract void registerDockable(IViewDockable viewDockable);

	public abstract void showDockable(String id);

	public abstract IViewDockable findViewDockablebyId(String id);

	public abstract boolean isDockableClosed(String id);

	public abstract IViewDockable[] getViewDockables();

	public abstract void registerOption(IViewOption viewOption);

	public abstract void registryRibbonTask(IViewRibbonTask viewRibbonTask);

	public abstract void showRibbonTask(String id);

	public abstract void hideRibbonTask(String id);

	public abstract void quit();

	public abstract void setAboutText(String aboutText);

	public abstract void setReleaseNotesText(String releaseNotesText);

	public abstract void showAboutDialog();

	public abstract void showReleaseNotes();
	
	public abstract void setHelpActions(Action[] helpActions);

	public abstract void addDefaultPerspective(Perspective perspective);

	public abstract void addPersonalPerspective();

	public abstract void deletePerspective();

	public abstract void editBindingPerspectives();

	public abstract void applyPerspective(Perspective perspective);

	public abstract void showConsoleDialog();

	public abstract IViewOption findViewOptionById(String id);

	public abstract void showOptions();

	public abstract void showOptions(String id);

	public abstract void titleChanged();

	public abstract void addCoreControllerListener(CoreControllerListener l);

	public abstract void removeCoreControllerListener(CoreControllerListener l);

	public abstract void setDirectionComponent(DirectionComponent direction,
			Component component);

	public abstract void showInfoMessage(Icon icon, String message,
			boolean authorizeClose, InfoMessageListener l);

	public abstract void hideInfoMessage();

}