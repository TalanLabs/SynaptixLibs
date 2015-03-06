package com.synaptix.core.controller;

import java.awt.Component;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.synaptix.client.view.Perspective;
import com.synaptix.core.CoreMessages;
import com.synaptix.core.dock.IViewDockable;
import com.synaptix.core.event.CoreConfigurationListener;
import com.synaptix.core.event.CoreControllerListener;
import com.synaptix.core.io.XmlStatsInput;
import com.synaptix.core.io.XmlStatsOutput;
import com.synaptix.core.menu.IViewMenu;
import com.synaptix.core.menu.IViewMenuItem;
import com.synaptix.core.menu.IViewMenuItemAction;
import com.synaptix.core.menu.IViewRibbonTask;
import com.synaptix.core.menu.ViewMenu;
import com.synaptix.core.menu.ViewMenuItem;
import com.synaptix.core.option.IViewOption;
import com.synaptix.core.view.AboutDialog;
import com.synaptix.core.view.CoreView;
import com.synaptix.core.view.EditBindingPerspectivesDialog;
import com.synaptix.core.view.JOutputConsole;
import com.synaptix.core.view.OptionsDialog;
import com.synaptix.core.view.ReleaseNotesDialog;
import com.synaptix.prefs.SyPreferences;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.swing.utils.Toolkit;
import com.synaptix.view.swing.error.ErrorViewManager;

public class CoreController implements CoreConfigurationListener, ICoreController {

	private CoreConfiguration configuration;

	private CoreView view;

	private List<Perspective> defaultPerspectiveList;

	private List<Perspective> personalPerspectiveList;

	private List<IViewDockable> viewDockableList;

	private List<IViewRibbonTask> viewRibbonTaskList;

	private List<IViewOption> viewOptionList;

	private List<IViewMenu> viewMenuList;

	private List<IViewMenuItem> viewMenuItemList;

	private Perspective currentPerspective;

	private JOutputConsole outputConsole;

	private boolean newVersion;

	private EventListenerList listenerList;

	private Action[] helpActions;

	private String aboutText;

	private String releaseNotesText;

	public CoreController(CoreConfiguration configuration) {
		this.configuration = configuration;

		configuration.addCoreConfigurationListener(this);

		listenerList = new EventListenerList();

		outputConsole = new JOutputConsole();

		defaultPerspectiveList = new ArrayList<Perspective>();
		personalPerspectiveList = new ArrayList<Perspective>();

		viewDockableList = new ArrayList<IViewDockable>();
		viewOptionList = new ArrayList<IViewOption>();

		viewMenuList = new ArrayList<IViewMenu>();
		viewMenuItemList = new ArrayList<IViewMenuItem>();

		initComponents();

		titleChanged();
	}

	private void initComponents() {
		view = new CoreView(this);

		if (configuration.getLogo() != null) {
			view.setIconImage(Toolkit.getHeightImageScale(configuration.getLogo(), 16));
		}
	}

	public CoreView getView() {
		return view;
	}

	public void start() {
		view.setVisible(true);

		loadStats();
		loadOptions();

		if (newVersion) {
			if (releaseNotesText != null) {
				this.showReleaseNotes();
			}
		}
	}

	public void setAskQuit(boolean askQuit) {
		view.setAskQuit(askQuit);
	}

	public boolean isAskQuit() {
		return view.isAskQuit();
	}

	public void setEnabledShowViewsMenuItem(boolean enabled) {
		view.setEnabledShowViewsMenuItem(enabled);
	}

	public void showCurrentPerspective() {
		if (currentPerspective != null) {
			view.showPerspective(currentPerspective);
		}
	}

	public void setEnabledMenuById(String id, boolean enabled) {
		view.setEnabledMenuById(id, enabled);
	}

	public void setEnabledMenuItemById(String id, boolean enabled) {
		view.setEnabledMenuItemById(id, enabled);
	}

	public boolean isEnabledMenuById(String id) {
		return view.isEnabledMenuById(id);
	}

	public boolean isEnabledMenuItemById(String id) {
		return view.isEnabledMenuItemById(id);
	}

	public void addMenu(String id, String name, String[] separators) {
		addMenu(id, name, separators, null);
	}

	public void addMenu(String id, String name, String[] separators, String path) {
		addMenu(id, name, separators, path, 0);
	}

	public void addMenu(String id, String name, String[] separators, String path, int position) {
		IViewMenu a = getViewMenu(id);
		if (a == null) {
			viewMenuList.add(new ViewMenu(id, name, separators, path, position));
			Collections.sort(viewMenuList, new ViewMenuComparator());
			view.updateViewMenusAndViewMenuItems(viewMenuList, viewMenuItemList);
		} else {
			List<String> sepList = new ArrayList<String>();
			if (a.getSeparators() != null) {
				sepList.addAll(Arrays.asList(a.getSeparators()));
			}

			if (separators != null) {
				for (String s : separators) {
					if (!sepList.contains(s)) {
						sepList.add(s);
					}
				}
			}

			viewMenuList.remove(a);
			viewMenuList.add(new ViewMenu(id, name, sepList.toArray(new String[sepList.size()]), path, position));

			Collections.sort(viewMenuList, new ViewMenuComparator());
			view.updateViewMenusAndViewMenuItems(viewMenuList, viewMenuItemList);
		}
	}

	public void addMenuItem(String id, String name, String path, IViewMenuItemAction menuItemAction) {
		viewMenuItemList.add(new ViewMenuItem(id, name, path, menuItemAction));

		view.updateViewMenusAndViewMenuItems(viewMenuList, viewMenuItemList);
	}

	public IViewMenu[] getViewMenus() {
		return viewMenuList.toArray(new IViewMenu[viewMenuList.size()]);
	}

	public IViewMenu getViewMenu(String id) {
		IViewMenu res = null;
		int i = 0;
		while (i < viewMenuList.size() && res == null) {
			if (viewMenuList.get(i).getId().equals(id)) {
				res = viewMenuList.get(i);
			}
			i++;
		}
		return res;
	}

	public IViewMenuItem[] getViewMenuItems() {
		return viewMenuItemList.toArray(new IViewMenuItem[viewMenuItemList.size()]);
	}

	public void registerDockable(IViewDockable viewDockable) {
		viewDockableList.add(viewDockable);
		view.registerDockable(viewDockable);
	}

	public void showDockable(String id) {
		view.showDockable(id);
	}

	public IViewDockable findViewDockablebyId(String id) {
		IViewDockable res = null;
		int i = 0;
		while (i < viewDockableList.size() && res == null) {
			IViewDockable v = viewDockableList.get(i);
			if (v.getId().equals(id)) {
				res = v;
			}
			i++;
		}
		return res;
	}

	@Deprecated
	public JComponent getDockable(String id) {
		return (JComponent) view.getDockable(id).getComponent();
	}

	public boolean isDockableClosed(String id) {
		return view.isDockableClosed(id);
	}

	public IViewDockable[] getViewDockables() {
		return viewDockableList.toArray(new IViewDockable[viewDockableList.size()]);
	}

	public void registerOption(IViewOption viewOption) {
		viewOptionList.add(viewOption);

		view.setPreferencesEnabled(true);
	}

	public void registryRibbonTask(IViewRibbonTask viewRibbonTask) {
		viewRibbonTaskList.add(viewRibbonTask);
	}

	public void showRibbonTask(String id) {

	}

	public void hideRibbonTask(String id) {

	}

	private void loadStats() {
		if (configuration.getSave() != null) {
			String save = configuration.getSave();

			SyPreferences prefs = SyPreferences.getPreferences();

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			DisplayMode dm = ge.getDefaultScreenDevice().getDisplayMode();

			int width = Math.min(prefs.getInt(save + "_width", 1024), dm //$NON-NLS-1$
					.getWidth());
			int height = Math.min(prefs.getInt(save + "_height", 768 - 32), dm //$NON-NLS-1$
					.getHeight() - 32);
			view.setSize(width, height);
			view.setLocationRelativeTo(null);

			boolean max = prefs.getBoolean(save + "_maximize", false); //$NON-NLS-1$
			if (max) {
				// view.setExtendedState(JFrame.MAXIMIZED_BOTH);
			}

			String version = prefs.get(save + "_version", ""); //$NON-NLS-1$ //$NON-NLS-2$
			if (!version.equals(configuration.getVersion())) {
				newVersion = true;
			} else {
				newVersion = false;
			}

			personalPerspectiveList.clear();
			int nbWorkspaces = prefs.getInt(save + "_nb_workspaces", 0); //$NON-NLS-1$
			for (int i = 0; i < nbWorkspaces; i++) {
				String desktopName = prefs.get(save + "_workspace_name_" //$NON-NLS-1$
						+ String.valueOf(i), null);
				String binding = prefs.get(save + "_workspace_binding_" //$NON-NLS-1$
						+ String.valueOf(i), null);
				String workspaceXml = prefs.get(save + "_workspace_" //$NON-NLS-1$
						+ String.valueOf(i), null);
				String statsXml = prefs.get(save + "_workspacestats_" //$NON-NLS-1$
						+ String.valueOf(i), null);
				if (desktopName != null && workspaceXml != null) {
					addPersonalPerspective(desktopName, workspaceXml, statsXml, binding);
				}
			}

			String workspaceXml = prefs.get(save + "_workspace_current", null); //$NON-NLS-1$
			String statsXml = prefs.get(save + "_workspacestats_current", null); //$NON-NLS-1$

			if (workspaceXml != null) {
				currentPerspective = new Perspective();
				currentPerspective.setName("current"); //$NON-NLS-1$
				currentPerspective.setWorkspaceXml(workspaceXml);
				currentPerspective.setWorkspaceStatsXml(statsXml);
			} else {
				currentPerspective = null;
			}
		}
	}

	private void saveStats() {
		if (configuration.getSave() != null) {
			String save = configuration.getSave();

			SyPreferences prefs = SyPreferences.getPreferences();

			if (view.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
				prefs.putBoolean(save + "_maximize", true); //$NON-NLS-1$
				prefs.putInt(save + "_width", view.getWidth()); //$NON-NLS-1$
				prefs.putInt(save + "_height", view.getHeight()); //$NON-NLS-1$
			} else {
				prefs.putBoolean(save + "_maximize", false); //$NON-NLS-1$
				prefs.putInt(save + "_width", view.getWidth()); //$NON-NLS-1$
				prefs.putInt(save + "_height", view.getHeight()); //$NON-NLS-1$
			}

			prefs.put(save + "_version", configuration.getVersion()); //$NON-NLS-1$

			int nbWorkspaces = prefs.getInt(save + "_nb_workspaces", 0); //$NON-NLS-1$
			for (int i = 0; i < nbWorkspaces; i++) {
				prefs.remove(save + "_workspace_name_" + String.valueOf(i)); //$NON-NLS-1$
				prefs.remove(save + "_workspace_binding_" + String.valueOf(i)); //$NON-NLS-1$
				prefs.remove(save + "_workspace_" + String.valueOf(i)); //$NON-NLS-1$
				prefs.remove(save + "_workspacestats_" + String.valueOf(i)); //$NON-NLS-1$
			}

			prefs.putInt(save + "_nb_workspaces", personalPerspectiveList.size()); //$NON-NLS-1$
			for (int i = 0; i < personalPerspectiveList.size(); i++) {
				Perspective perspective = personalPerspectiveList.get(i);
				prefs.put(save + "_workspace_name_" + String.valueOf(i), //$NON-NLS-1$
						perspective.getName());
				if (perspective.getBinding() != null) {
					prefs.put(save + "_workspace_binding_" + String.valueOf(i), //$NON-NLS-1$
							perspective.getBinding());
				}
				prefs.put(save + "_workspace_" + String.valueOf(i), perspective //$NON-NLS-1$
						.getWorkspaceXml());

				if (perspective.getWorkspaceStatsXml() != null) {
					prefs.put(save + "_workspacestats_" + String.valueOf(i), //$NON-NLS-1$
							perspective.getWorkspaceStatsXml());
				}
			}

			String workspaceXml = view.getCurrentWorkspaceFromXml("current"); //$NON-NLS-1$
			prefs.put(save + "_workspace_current", workspaceXml); //$NON-NLS-1$

			System.out.println("Workspace xml : \n" + workspaceXml); //$NON-NLS-1$

			String statsXml = view.getCurrentWorkspaceStatsFromXml();
			prefs.put(save + "_workspacestats_current", statsXml); //$NON-NLS-1$

			System.out.println("Workspace stats wml : \n" + statsXml); //$NON-NLS-1$
		}
	}

	public void quit() {
		saveStats();
		saveOptions();

		view.closeAllDockables();
		view.dispose();

		fireClosedCoreController();
	}

	public void setAboutText(String aboutText) {
		this.aboutText = aboutText;
		view.setActiveAbout(aboutText != null);
	}

	public void setReleaseNotesText(String releaseNotesText) {
		this.releaseNotesText = releaseNotesText;
		view.setActiveReleaseNotes(releaseNotesText != null);
	}

	public void showAboutDialog() {
		if (aboutText != null) {
			new AboutDialog().showDialog(null, configuration.getName(), configuration.getLogo(), aboutText);
		}
	}

	public void showReleaseNotes() {
		if (releaseNotesText != null) {
			new ReleaseNotesDialog().showDialog(null, configuration.getVersion(), releaseNotesText);
		}
	}

	public void setHelpActions(Action[] helpActions) {
		this.helpActions = helpActions;
		view.setHelpActions(helpActions);
	}

	public void applyPerspective(Perspective perspective) {
		view.showPerspective(perspective);
	}

	public void addDefaultPerspective(Perspective perspective) {
		defaultPerspectiveList.add(perspective);
		view.updateDefaultPerspectives(defaultPerspectiveList);
	}

	private void addPersonalPerspective(String desktopName, String workspaceXml, String statsXml, String binding) {
		Perspective perspective = new Perspective();
		perspective.setName(desktopName);
		perspective.setWorkspaceXml(workspaceXml);
		perspective.setWorkspaceStatsXml(statsXml);
		perspective.setBinding(binding);

		personalPerspectiveList.add(perspective);
		view.updatePersonalPerspectives(personalPerspectiveList);
	}

	public void addPersonalPerspective() {
		String desktopName = JOptionPane.showInputDialog(GUIWindow.getActiveWindow(), CoreMessages.getString("CoreController.35"), CoreMessages.getString("CoreController.36"), //$NON-NLS-1$ //$NON-NLS-2$
				JOptionPane.QUESTION_MESSAGE);
		if (desktopName != null && !desktopName.trim().isEmpty()) {
			String workspaceXml = view.getCurrentWorkspaceFromXml(desktopName);
			String statsXml = view.getCurrentWorkspaceStatsFromXml();
			System.out.println("Workspace xml : \n" + workspaceXml); //$NON-NLS-1$
			System.out.println("Workspace stats wml : \n" + statsXml); //$NON-NLS-1$
			if (workspaceXml != null) {
				addPersonalPerspective(desktopName, workspaceXml, statsXml, null);
			}
		}
	}

	public void deletePerspective() {
		List<String> names = new ArrayList<String>();
		for (int i = 0; i < personalPerspectiveList.size(); i++) {
			Perspective perspective = personalPerspectiveList.get(i);
			names.add(perspective.getName());
		}

		String desktopName = (String) JOptionPane.showInputDialog(GUIWindow.getActiveWindow(), CoreMessages.getString("CoreController.39"), //$NON-NLS-1$
				CoreMessages.getString("CoreController.40"), JOptionPane.QUESTION_MESSAGE, null, //$NON-NLS-1$
				names.toArray(), names.get(0));
		if (desktopName != null) {
			int i = names.indexOf(desktopName);

			personalPerspectiveList.remove(i);

			view.updatePersonalPerspectives(personalPerspectiveList);
		}
	}

	public void editBindingPerspectives() {
		EditBindingPerspectivesDialog dialog = new EditBindingPerspectivesDialog();
		if (dialog.showDialog(null, personalPerspectiveList) == EditBindingPerspectivesDialog.ACCEPT_OPTION) {
			personalPerspectiveList = dialog.getPerspectives();

			view.updatePersonalPerspectives(personalPerspectiveList);
		}
	}

	public void showConsoleDialog() {
		outputConsole.showDialog(null);
	}

	public IViewOption findViewOptionById(String id) {
		IViewOption res = null;
		int i = 0;
		while (i < viewOptionList.size() && res == null) {
			IViewOption v = viewOptionList.get(i);
			if (v.getId().equals(id)) {
				res = v;
			}
			i++;
		}
		return res;
	}

	private void loadOptions() {
		SyPreferences prefs = SyPreferences.getPreferences();

		String name = "CoreController"; //$NON-NLS-1$

		String optionsXml = prefs.get(name + "_options", null); //$NON-NLS-1$

		if (optionsXml != null && !optionsXml.isEmpty()) {
			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(optionsXml.getBytes());

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(bais);
				Element root = doc.getDocumentElement();

				NodeList nodeList = root.getElementsByTagName("OptionStats"); //$NON-NLS-1$
				if (nodeList != null && nodeList.getLength() > 0) {
					for (int i = 0; i < nodeList.getLength(); i++) {
						Node node = nodeList.item(i);
						String id = node.getAttributes().getNamedItem("id") //$NON-NLS-1$
								.getTextContent();

						IViewOption viewOption = findViewOptionById(id);
						if (viewOption != null) {
							XmlStatsInput statsInput = new XmlStatsInput((Element) node.getFirstChild());
							viewOption.readStats(statsInput);
						}
					}
				}
			} catch (Exception e) {
				ErrorViewManager.getInstance().showErrorDialog(null, e);
			}
		}
	}

	private void saveOptions() {
		SyPreferences prefs = SyPreferences.getPreferences();

		String name = "CoreController"; //$NON-NLS-1$

		StringBuilder sb = new StringBuilder();
		sb.append("<OptionsStats>"); //$NON-NLS-1$
		for (IViewOption viewOption : viewOptionList) {
			XmlStatsOutput statsOutput = new XmlStatsOutput();
			viewOption.writeStats(statsOutput);

			sb.append("<OptionStats id=\""); //$NON-NLS-1$
			sb.append(viewOption.getId());
			sb.append("\">"); //$NON-NLS-1$
			sb.append(statsOutput.getXml());
			sb.append("</OptionStats>"); //$NON-NLS-1$
		}
		sb.append("</OptionsStats>"); //$NON-NLS-1$

		prefs.put(name + "_options", sb.toString()); //$NON-NLS-1$
	}

	public void showOptions() {
		showOptions(null);
	}

	public void showOptions(String id) {
		OptionsDialog dialog = new OptionsDialog();
		if (dialog.showDialog(null, viewOptionList, id) == OptionsDialog.ACCEPT_OPTION) {
			for (IViewOption viewOption : viewOptionList) {
				viewOption.apply();
			}
		} else {
			for (IViewOption viewOption : viewOptionList) {
				viewOption.cancelled();
			}
		}
	}

	public void titleChanged() {
		StringBuilder sb = new StringBuilder();

		sb.append(configuration.getName());
		sb.append(" "); //$NON-NLS-1$
		sb.append(configuration.getVersion());
		sb.append(" "); //$NON-NLS-1$
		sb.append(configuration.getOtherTitle());

		view.setTitle(sb.toString());
	}

	public void addCoreControllerListener(CoreControllerListener l) {
		listenerList.add(CoreControllerListener.class, l);
	}

	public void removeCoreControllerListener(CoreControllerListener l) {
		listenerList.remove(CoreControllerListener.class, l);
	}

	protected void fireClosedCoreController() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CoreControllerListener.class) {
				((CoreControllerListener) listeners[i + 1]).closedCoreController();
			}
		}
	}

	public void setDirectionComponent(DirectionComponent direction, Component component) {
		view.setDirectionComponent(direction, component);
	}

	public void showInfoMessage(Icon icon, String message, boolean authorizeClose, InfoMessageListener l) {
		view.showInfoMessage(icon, message, authorizeClose, l);
	}

	public void hideInfoMessage() {
		view.hideInfoMessage();
	}

	private final class ViewMenuComparator implements Comparator<IViewMenu> {

		public int compare(IViewMenu o1, IViewMenu o2) {
			return Integer.valueOf(o1.getPosition()).compareTo(Integer.valueOf(o2.getPosition()));
		}
	}
}
