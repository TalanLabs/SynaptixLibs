package com.synaptix.core.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.synaptix.client.view.Perspective;
import com.synaptix.core.CoreMessages;
import com.synaptix.core.controller.ICoreController;
import com.synaptix.core.controller.ICoreController.DirectionComponent;
import com.synaptix.core.controller.InfoMessageListener;
import com.synaptix.core.dock.DefaultViewDockable;
import com.synaptix.core.dock.IViewDockable;
import com.synaptix.core.event.ViewDockableStateEvent;
import com.synaptix.core.event.ViewDockableStateEvent.State;
import com.synaptix.core.io.XmlStatsInput;
import com.synaptix.core.io.XmlStatsOutput;
import com.synaptix.core.menu.IViewMenu;
import com.synaptix.core.menu.IViewMenuItem;
import com.synaptix.swing.docking.SyDockableContainerFactory;
import com.synaptix.view.swing.error.ErrorViewManager;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockableContainerFactory;
import com.vlsolutions.swing.docking.DockableResolver;
import com.vlsolutions.swing.docking.DockableState;
import com.vlsolutions.swing.docking.DockingContext;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.TabbedDockableContainer;
import com.vlsolutions.swing.docking.event.DockableSelectionEvent;
import com.vlsolutions.swing.docking.event.DockableSelectionListener;
import com.vlsolutions.swing.docking.event.DockableStateChangeEvent;
import com.vlsolutions.swing.docking.event.DockableStateChangeListener;
import com.vlsolutions.swing.docking.ws.Workspace;

public class CoreView extends JFrame {

	private static final long serialVersionUID = 5774046567154890105L;

	private final static int MAX_VIEW_MENUITEMS = 30;

	private DockingDesktop dockingDesktop;

	private DockingContext dockingContext;

	private ICoreController controller;

	private Map<String, Dockable> dockableMap;

	private Map<String, IViewDockable> viewDockableMap;

	private List<Perspective> defaultPerspectiveList;

	private List<Perspective> personalPerspectiveList;

	private List<IViewMenu> viewMenus;

	private List<IViewMenuItem> viewMenuItems;

	private Map<String, Boolean> menuEnabledMap;

	private Map<String, Boolean> menuItemsEnabledMap;

	private Map<String, JMenu> menusMap;

	private Map<String, JMenuItem> menuItemsMap;

	private List<KeyStroke> keyStrokeList;

	private Map<String, List<JMenu>> showViewsMenuMap;

	private JMenu fileMenu;

	private JMenu windowMenu;

	private JMenu helpMenu;

	private JMenu openPerspectivesMenu;

	private JMenu showViewsMenu;

	private Action editBindingPerspectivesAction;

	private Action deletePerspectiveAction;

	private Action showOptionsAction;

	private Action quitAction;

	private Action savePerspectiveAction;

	private Action showConsoleAction;

	private Action showReleaseNotesAction;

	private Action showAboutAction;

	private Action[] helpActions;

	private Action emptyDefaultPerspectiveAction;

	private Action emptyPersonalPerspectiveAction;

	// private JRibbon ribbon;

	private boolean askQuit;

	private boolean showViewsMenuItems;

	private JPanel centralPanel;

	private InfoMessagePanel infoMessagePanel;

	public CoreView(ICoreController controller) {
		super();

		askQuit = true;
		showViewsMenuItems = true;

		this.controller = controller;

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		this.addWindowListener(new MyWindowListener());

		dockableMap = new HashMap<String, Dockable>();
		viewDockableMap = new HashMap<String, IViewDockable>();
		keyStrokeList = new ArrayList<KeyStroke>();

		showViewsMenuMap = new HashMap<String, List<JMenu>>();

		menusMap = new HashMap<String, JMenu>();
		menuItemsMap = new HashMap<String, JMenuItem>();

		menuEnabledMap = new HashMap<String, Boolean>();
		menuItemsEnabledMap = new HashMap<String, Boolean>();

		initActions();
		initComponents();

		buildMenuBar();

		this.getContentPane().add(buildContents(), BorderLayout.CENTER);
	}

	public void setDirectionComponent(DirectionComponent direction, Component component) {
		switch (direction) {
		case North:
			centralPanel.add(component, BorderLayout.NORTH);
			break;
		case East:
			centralPanel.add(component, BorderLayout.EAST);
			break;
		case South:
			centralPanel.add(component, BorderLayout.SOUTH);
			break;
		case West:
			centralPanel.add(component, BorderLayout.WEST);
			break;
		}
	}

	private void initActions() {
		editBindingPerspectivesAction = new EditBindingPerspectivesAction();
		editBindingPerspectivesAction.setEnabled(false);

		deletePerspectiveAction = new DeletePerspectiveAction();
		deletePerspectiveAction.setEnabled(false);

		showOptionsAction = new ShowOptionsAction();
		showOptionsAction.setEnabled(false);

		quitAction = new QuitAction();
		savePerspectiveAction = new SavePerspectiveAction();

		showConsoleAction = new ShowConsoleAction();

		showReleaseNotesAction = new ShowReleaseNotesAction();
		showReleaseNotesAction.setEnabled(false);

		showAboutAction = new ShowAboutAction();
		showAboutAction.setEnabled(false);

		emptyDefaultPerspectiveAction = new EmptyDefaultPerspectiveAction();
		emptyDefaultPerspectiveAction.setEnabled(false);

		emptyPersonalPerspectiveAction = new EmptyPersonalPerspectiveAction();
		emptyPersonalPerspectiveAction.setEnabled(false);
	}

	private void createComponents() {
		DockableContainerFactory.setFactory(new SyDockableContainerFactory());
		dockingContext = new DockingContext();

		// ribbon = new JRibbon();

		dockingDesktop = new DockingDesktop("normal", dockingContext); //$NON-NLS-1$

		fileMenu = new JMenu(CoreMessages.getString("CoreView.1")); //$NON-NLS-1$
		windowMenu = new JMenu(CoreMessages.getString("CoreView.2")); //$NON-NLS-1$
		showViewsMenu = new JMenu(CoreMessages.getString("CoreView.3")); //$NON-NLS-1$
		openPerspectivesMenu = new JMenu(CoreMessages.getString("CoreView.4")); //$NON-NLS-1$
		helpMenu = new JMenu(CoreMessages.getString("CoreView.5")); //$NON-NLS-1$

		centralPanel = new JPanel(new BorderLayout());

		infoMessagePanel = new InfoMessagePanel();
	}

	private void initComponents() {
		createComponents();

		dockingContext.setDockableResolver(new MyDockableResolver());
		dockingContext.addDockableStateChangeListener(new MyDockableStateChangeListener());
		dockingContext.addDockableSelectionListener(new MyDockableSelectionListener());

		// ribbon.setEnabledHiddenBands(false);
		// ribbon.setAlignmentTaskbar(SwingConstants.LEFT);
		// ribbon.setAlignmentBands(SwingConstants.LEFT);

		fileMenu.add(quitAction);

		showViewsMenu.setEnabled(false);

		windowMenu.add(showViewsMenu);
		windowMenu.addSeparator();
		windowMenu.add(openPerspectivesMenu);
		windowMenu.addSeparator();
		windowMenu.add(savePerspectiveAction);
		windowMenu.add(editBindingPerspectivesAction);
		windowMenu.addSeparator();
		windowMenu.add(deletePerspectiveAction);
		windowMenu.addSeparator();
		windowMenu.add(showOptionsAction);

		helpMenu.add(showReleaseNotesAction);
		helpMenu.addSeparator();
		helpMenu.add(showAboutAction);

		centralPanel.add(dockingDesktop, BorderLayout.CENTER);
	}

	private JComponent buildContents() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(infoMessagePanel, BorderLayout.NORTH);
		panel.add(centralPanel, BorderLayout.CENTER);
		return panel;
	}

	public void showInfoMessage(Icon icon, String message, boolean authorizeClose, InfoMessageListener l) {
		infoMessagePanel.showInfoMessage(icon, message, authorizeClose, l);
	}

	public void hideInfoMessage() {
		infoMessagePanel.hideInfoMessage();
	}

	public void setActiveReleaseNotes(boolean active) {
		showReleaseNotesAction.setEnabled(active);
	}

	public void setActiveAbout(boolean active) {
		showAboutAction.setEnabled(active);
	}

	public void setAskQuit(boolean askQuit) {
		this.askQuit = askQuit;
	}

	public boolean isAskQuit() {
		return askQuit;
	}

	private boolean isQuit() {
		return !askQuit || JOptionPane.showConfirmDialog(this, CoreMessages.getString("CoreView.6"), //$NON-NLS-1$
				CoreMessages.getString("CoreView.7"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION; //$NON-NLS-1$
	}

	private IViewMenu findViewMenuById(String id) {
		for (IViewMenu viewMenu : viewMenus) {
			if (viewMenu.getId().equals(id)) {
				return viewMenu;
			}
		}
		return null;
	}

	private int findSeparatorIndexByName(String[] separators, String name) {
		for (int i = 0; i < separators.length; i++) {
			if (separators[i].equals(name)) {
				return i;
			}
		}
		return -1;
	}

	private int getLastMenuItemFromGroup(IViewMenu viewMenu, JMenu menu, String g) {
		int i = findSeparatorIndexByName(viewMenu.getSeparators(), g);
		int count = 0;
		for (int n = 0; n < menu.getPopupMenu().getComponentCount(); n++) {
			Component c = menu.getPopupMenu().getComponent(n);
			if (c instanceof JPopupMenu.Separator) {
				if (count == i) {
					return n;
				}
				count++;
			}
		}
		return menu.getPopupMenu().getComponentCount();
	}

	private List<JMenu> createJMenus() {
		List<JMenu> jmenus = new ArrayList<JMenu>();

		menusMap.clear();
		menuItemsMap.clear();

		if (viewMenus != null && !viewMenus.isEmpty() && viewMenuItems != null && !viewMenuItems.isEmpty()) {
			for (IViewMenu viewMenu : viewMenus) {
				JMenu menu = new JMenu(viewMenu.getName());
				for (int i = 0; i < viewMenu.getSeparators().length - 1; i++) {
					menu.addSeparator();
				}

				if (menuEnabledMap.containsKey(viewMenu.getId())) {
					menu.setEnabled(menuEnabledMap.get(viewMenu.getId()));
				}
				menusMap.put(viewMenu.getId(), menu);
			}

			for (IViewMenu viewMenu : viewMenus) {
				JMenu menu = menusMap.get(viewMenu.getId());
				if (viewMenu.getPath() != null) {
					String[] ns = viewMenu.getPath().split("/"); //$NON-NLS-1$
					String m = ns[ns.length - 2];
					String g = ns[ns.length - 1];

					IViewMenu parentViewMenu = findViewMenuById(m);
					JMenu parentMenu = menusMap.get(m);

					int i = getLastMenuItemFromGroup(parentViewMenu, parentMenu, g);
					parentMenu.insert(menu, i);
				} else {
					jmenus.add(menu);
				}
			}

			for (IViewMenuItem viewMenuItem : viewMenuItems) {
				String[] ns = viewMenuItem.getPath().split("/"); //$NON-NLS-1$
				String m = ns[ns.length - 2];
				String g = ns[ns.length - 1];

				IViewMenu parentViewMenu = findViewMenuById(m);
				JMenu parentMenu = menusMap.get(m);

				JMenuItem menuItem = new JMenuItem(new ViewMenuItemAction(viewMenuItem));

				int i = getLastMenuItemFromGroup(parentViewMenu, parentMenu, g);
				parentMenu.insert(menuItem, i);

				if (menuItemsEnabledMap.containsKey(viewMenuItem.getId())) {
					menuItem.setEnabled(menuItemsEnabledMap.get(viewMenuItem.getId()));
				}

				menuItemsMap.put(viewMenuItem.getId(), menuItem);
			}
		}

		return jmenus;
	}

	private final class ViewMenuItemAction extends AbstractAction {

		private static final long serialVersionUID = -7716022402965530588L;

		private IViewMenuItem viewMenuItem;

		public ViewMenuItemAction(IViewMenuItem viewMenuItem) {
			super(viewMenuItem.getName());

			this.viewMenuItem = viewMenuItem;
		}

		public void actionPerformed(ActionEvent e) {
			viewMenuItem.getMenuItemAction().run(this, viewMenuItem.getId(), viewMenuItem.getName());
		}
	}

	private void buildMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);

		for (JMenu menu : createJMenus()) {
			menuBar.add(menu);
		}

		updateOpenPerspectivesMenu();

		menuBar.add(windowMenu);
		menuBar.add(helpMenu);

		this.setJMenuBar(menuBar);
	}

	private void buildHelpMenu() {
		helpMenu.removeAll();

		if (helpActions != null && helpActions.length > 0) {
			for (Action a : helpActions) {
				if (a != null) {
					helpMenu.add(a);
				} else {
					helpMenu.addSeparator();
				}
			}
		}

		helpMenu.add(showReleaseNotesAction);
		helpMenu.addSeparator();
		helpMenu.add(showAboutAction);
	}

	private void updateOpenPerspectivesMenu() {
		for (KeyStroke keyStroke : keyStrokeList) {
			if (keyStroke != null) {
				Object action = dockingDesktop.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(keyStroke);
				dockingDesktop.getActionMap().remove(action);
				dockingDesktop.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(keyStroke);
			}
		}

		keyStrokeList.clear();
		openPerspectivesMenu.removeAll();

		if (defaultPerspectiveList != null && !defaultPerspectiveList.isEmpty()) {
			for (Perspective perspective : defaultPerspectiveList) {
				KeyStroke keyStroke = KeyStroke.getKeyStroke(perspective.getBinding());

				Action action = new OpenPerspectiveMenuAction(perspective);
				openPerspectivesMenu.add(action);

				if (keyStroke != null) {
					action.putValue(Action.ACCELERATOR_KEY, keyStroke);
					dockingDesktop.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, perspective.getName());
					dockingDesktop.getActionMap().put(perspective.getName(), action);
					keyStrokeList.add(keyStroke);
				}
			}
		} else {
			openPerspectivesMenu.add(emptyDefaultPerspectiveAction);
		}

		openPerspectivesMenu.addSeparator();

		if (personalPerspectiveList != null && !personalPerspectiveList.isEmpty()) {
			int i = 0;
			for (Perspective perspective : personalPerspectiveList) {
				KeyStroke keyStroke = KeyStroke.getKeyStroke(perspective.getBinding());

				Action action = new OpenPerspectiveMenuAction(perspective);
				openPerspectivesMenu.add(action);

				if (keyStroke != null) {
					action.putValue(Action.ACCELERATOR_KEY, keyStroke);
					dockingDesktop.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, perspective.getName());
					dockingDesktop.getActionMap().put(perspective.getName(), action);
					keyStrokeList.add(keyStroke);
				}

				i++;
			}
			editBindingPerspectivesAction.setEnabled(true);
			deletePerspectiveAction.setEnabled(true);
		} else {
			openPerspectivesMenu.add(emptyPersonalPerspectiveAction);

			editBindingPerspectivesAction.setEnabled(false);
			deletePerspectiveAction.setEnabled(false);
		}
	}

	public void updateDefaultPerspectives(List<Perspective> defaultPerspectives) {
		this.defaultPerspectiveList = defaultPerspectives;

		updateOpenPerspectivesMenu();
	}

	public void updatePersonalPerspectives(List<Perspective> personalPerspectives) {
		this.personalPerspectiveList = personalPerspectives;

		updateOpenPerspectivesMenu();
	}

	public void showPerspective(Perspective perspective) {
		setWorkspaceByXml(perspective.getWorkspaceXml());
		setWorkspaceStatsByXml(perspective.getWorkspaceStatsXml());
	}

	private void setWorkspaceByXml(String xml) {
		closeAllDockables();

		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))); //$NON-NLS-1$

			Workspace workspace = new Workspace();
			workspace.readXML(bais);

			// TODO pour afficher le workspace
			// System.out.println(res);

			workspace.apply(dockingContext);
		} catch (Exception e) {
			ErrorViewManager.getInstance().showErrorDialog(this, e);
		}
	}

	private void setWorkspaceStatsByXml(String xml) {
		if (xml != null && !xml.isEmpty()) {
			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(bais);
				Element root = doc.getDocumentElement();

				NodeList nodeList = root.getElementsByTagName("DockableStats"); //$NON-NLS-1$
				if (nodeList != null && nodeList.getLength() > 0) {
					for (int i = 0; i < nodeList.getLength(); i++) {
						Node node = nodeList.item(i);
						String id = node.getAttributes().getNamedItem("id") //$NON-NLS-1$
								.getTextContent();

						IViewDockable viewDockable = viewDockableMap.get(id);
						if (viewDockable != null) {
							XmlStatsInput statsInput = new XmlStatsInput((Element) node.getFirstChild());
							viewDockable.readStats(statsInput);
						}
					}
				}
			} catch (Exception e) {
				ErrorViewManager.getInstance().showErrorDialog(this, e);
			}
		}
	}

	public String getCurrentWorkspaceFromXml(String desktopName) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			((DockingDesktop) dockingContext.getDesktopList().get(0)).setDesktopName(desktopName);
			dockingContext.writeXML(baos);

			baos.close();

			return baos.toString();
		} catch (IOException e) {
			ErrorViewManager.getInstance().showErrorDialog(this, e);
		}
		return null;
	}

	public String getCurrentWorkspaceStatsFromXml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<WorkspaceStats>"); //$NON-NLS-1$
		DockableState[] dockableStates = dockingContext.getDockables();
		if (dockableStates != null && dockableStates.length > 0) {
			for (DockableState dockableState : dockingContext.getDockables()) {
				if (!dockableState.isClosed()) {
					String id = dockableState.getDockable().getDockKey().getKey();
					IViewDockable viewDockable = viewDockableMap.get(id);

					XmlStatsOutput statsOutput = new XmlStatsOutput();
					viewDockable.writeStats(statsOutput);

					sb.append("<DockableStats id=\""); //$NON-NLS-1$
					sb.append(id);
					sb.append("\">"); //$NON-NLS-1$
					sb.append(statsOutput.getXml());
					sb.append("</DockableStats>"); //$NON-NLS-1$
				}
			}
		}
		sb.append("</WorkspaceStats>"); //$NON-NLS-1$
		return sb.toString();
	}

	private void addViewAtMenu(String categorie, JMenuItem menuItem) {
		if (!showViewsMenuMap.containsKey(categorie)) {
			List<JMenu> menus = new ArrayList<JMenu>();
			JMenu menu = new JMenu(categorie);
			menus.add(menu);
			showViewsMenuMap.put(categorie, menus);

			showViewsMenu.add(menu);

			showViewsMenu.setEnabled(showViewsMenuItems);
		}

		List<JMenu> menus = showViewsMenuMap.get(categorie);
		JMenu menu = menus.get(menus.size() - 1);
		if (menu.getMenuComponentCount() > MAX_VIEW_MENUITEMS) {
			JMenu next = new JMenu(CoreMessages.getString("CoreView.18")); //$NON-NLS-1$
			menus.add(next);

			menu.add(next);
			menu = next;
		}

		menu.add(menuItem);
	}

	public void registerDockable(final IViewDockable viewDockable) {
		Dockable dockable = new MyDock(viewDockable.getId(), viewDockable.getName(), viewDockable.getIcon(), viewDockable.getView());
		dockingContext.registerDockable(dockable);

		dockableMap.put(viewDockable.getId(), dockable);

		addViewAtMenu(viewDockable.getCategorie(), new JMenuItem(new DockableMenuAction(viewDockable, dockable)));

		viewDockableMap.put(viewDockable.getId(), viewDockable);
	}

	public void registerDockable(String id, String label, JComponent component) {
		registerDockable(new DefaultViewDockable(id, CoreMessages.getString("CoreView.19"), label, //$NON-NLS-1$
				component));
	}

	public void showDockable(String id) {
		if (dockableMap.containsKey(id)) {
			showDockable(dockableMap.get(id));
		}
	}

	public Dockable getDockable(String id) {
		if (dockableMap.containsKey(id)) {
			return dockableMap.get(id);
		}
		return null;
	}

	public boolean isDockableClosed(String id) {
		DockableState state = dockingContext.getDockableState(getDockable(id));
		if (state != null) {
			return state.isClosed();
		}
		return true;
	}

	private void restoreDockable() {
		DockableState[] dockableStates = dockingContext.getDockables();
		if (dockableStates != null && dockableStates.length > 0) {
			for (DockableState dockableState : dockingContext.getDockables()) {
				if (dockableState.isMaximized()) {
					dockingDesktop.restore(dockableState.getDockable());
				}
			}
		}
	}

	private void showDockable(Dockable dockable) {
		restoreDockable();

		DockableState state = dockingContext.getDockableState(dockable);

		if (state == null) {
			dockingContext.registerDockable(dockable);
			state = dockingContext.getDockableState(dockable);
		}

		if (state.isClosed()) {
			for (DockableState s : dockingContext.getDockables()) {
				if (!s.isClosed()) {
					((DockingDesktop) dockingContext.getDesktopList().get(0)).createTab(s.getDockable(), dockable, 0, true);
					return;
				}
			}
			((DockingDesktop) dockingContext.getDesktopList().get(0)).addDockable(dockable);
		} else {
			Component c = dockable.getComponent();

			if (c != null && c.getParent() != null && c.getParent().getParent() instanceof TabbedDockableContainer) {
				TabbedDockableContainer tab = (TabbedDockableContainer) c.getParent().getParent();
				tab.setSelectedDockable(dockable);
			} else {
				c.requestFocus();
			}
		}
	}

	public void closeAllDockables() {
		for (DockableState ds : dockingContext.getDockables()) {
			if (!ds.getLocation().equals(DockableState.Location.CLOSED)) {
				dockingContext.close(ds.getDockable());
			}
		}
	}

	public void setPreferencesEnabled(boolean enabled) {
		showOptionsAction.setEnabled(enabled);
	}

	public void setHelpActions(Action[] helpActions) {
		this.helpActions = helpActions;

		buildHelpMenu();
	}

	public void updateViewMenusAndViewMenuItems(List<IViewMenu> viewMenus, List<IViewMenuItem> viewMenuItems) {
		this.viewMenus = viewMenus;
		this.viewMenuItems = viewMenuItems;

		buildMenuBar();
	}

	public void setEnabledMenuById(String id, boolean enabled) {
		menuEnabledMap.put(id, enabled);
		menusMap.get(id).setEnabled(enabled);
	}

	public void setEnabledMenuItemById(String id, boolean enabled) {
		menuItemsEnabledMap.put(id, enabled);
		menuItemsMap.get(id).setEnabled(enabled);
	}

	public boolean isEnabledMenuById(String id) {
		return menusMap.get(id).isEnabled();
	}

	public boolean isEnabledMenuItemById(String id) {
		return menuItemsMap.get(id).isEnabled();
	}

	public void setEnabledShowViewsMenuItem(boolean enabled) {
		showViewsMenuItems = enabled;
		showViewsMenu.setEnabled(enabled);
	}

	private final class EmptyDefaultPerspectiveAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EmptyDefaultPerspectiveAction() {
			super(CoreMessages.getString("CoreView.20")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	private final class EmptyPersonalPerspectiveAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EmptyPersonalPerspectiveAction() {
			super(CoreMessages.getString("CoreView.21")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	private final class MyWindowListener extends WindowAdapter {

		public void windowClosing(WindowEvent e) {
			if (isQuit()) {
				controller.quit();
			}

		}
	}

	private final class QuitAction extends AbstractAction {

		private static final long serialVersionUID = -2159857100558638085L;

		public QuitAction() {
			super(CoreMessages.getString("CoreView.22")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			if (isQuit()) {
				controller.quit();
			}
		}
	}

	private final class ShowConsoleAction extends AbstractAction {

		private static final long serialVersionUID = -4372546543499693455L;

		public ShowConsoleAction() {
			super(CoreMessages.getString("CoreView.23")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			controller.showConsoleDialog();
		}
	}

	private final class ShowReleaseNotesAction extends AbstractAction {

		private static final long serialVersionUID = -4372546543499693455L;

		public ShowReleaseNotesAction() {
			super(CoreMessages.getString("CoreView.24")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			controller.showReleaseNotes();
		}
	}

	private final class ShowAboutAction extends AbstractAction {

		private static final long serialVersionUID = 8357334616042353391L;

		public ShowAboutAction() {
			super(CoreMessages.getString("CoreView.25")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			controller.showAboutDialog();
		}
	}

	private final class SavePerspectiveAction extends AbstractAction {

		private static final long serialVersionUID = -4372546543499693455L;

		public SavePerspectiveAction() {
			super(CoreMessages.getString("CoreView.26")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			controller.addPersonalPerspective();
		}
	}

	private final class DeletePerspectiveAction extends AbstractAction {

		private static final long serialVersionUID = -4372546543499693455L;

		public DeletePerspectiveAction() {
			super(CoreMessages.getString("CoreView.27")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			controller.deletePerspective();
		}
	}

	private final class EditBindingPerspectivesAction extends AbstractAction {

		private static final long serialVersionUID = -4372546543499693455L;

		public EditBindingPerspectivesAction() {
			super(CoreMessages.getString("CoreView.28")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			controller.editBindingPerspectives();
		}
	}

	private final class ShowOptionsAction extends AbstractAction {

		private static final long serialVersionUID = -4372546543499693455L;

		public ShowOptionsAction() {
			super(CoreMessages.getString("CoreView.29")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			controller.showOptions();
		}
	}

	private final class DockableMenuAction extends AbstractAction {

		private static final long serialVersionUID = -1848517650204131096L;

		private Dockable dockable;

		public DockableMenuAction(IViewDockable viewDockable, Dockable dockable) {
			super();
			this.dockable = dockable;

			this.putValue(Action.NAME, dockable.getDockKey().getName());
			this.putValue(Action.SMALL_ICON, viewDockable.getIcon());
		}

		public void actionPerformed(ActionEvent e) {
			showDockable(dockable.getDockKey().getKey());
		}
	}

	private final class OpenPerspectiveMenuAction extends AbstractAction {

		private static final long serialVersionUID = -1848517650204131096L;

		private Perspective perspective;

		public OpenPerspectiveMenuAction(Perspective perspective) {
			super();
			this.perspective = perspective;

			this.putValue(Action.NAME, perspective.getName());
		}

		public void actionPerformed(ActionEvent e) {
			showPerspective(perspective);
		}
	}

	private final class MyDock implements Dockable {

		private DockKey dockKey;

		private Component component;

		public MyDock(String id, String name, Component component) {
			this(id, name, null, component);
		}

		public MyDock(String id, String name, Icon icon, Component component) {
			super();
			this.component = component;

			dockKey = new DockKey(id, name);
			dockKey.setFloatEnabled(true);
			dockKey.setAutoHideEnabled(false);
			dockKey.setIcon(icon);
			dockKey.setMaximizeEnabled(true);

			// TODO à utiliser pour faire des menuitems sur les dockables
			// DockableActionCustomizer dac = new DockableActionCustomizer() {
			// public void visitSingleDockableTitleBarPopUp(
			// JPopupMenu popUpMenu, Dockable dockable) {
			//
			// System.out.println("ici");
			//
			// popUpMenu.add(new JMenuItem("test"));
			// }
			//
			// public void visitTabSelectorPopUp(JPopupMenu popUpMenu,
			// Dockable dockable) {
			//
			// System.out.println("ici");
			//
			// popUpMenu.add(new JMenuItem("closeAll"));
			// popUpMenu.add(new JMenuItem("closeOther"));
			// }
			// };
			// dac.setSingleDockableTitleBarPopUpCustomizer(true);
			// dac.setTabSelectorPopUpCustomizer(true);
			// dockKey.setActionCustomizer(dac);
		}

		public Component getComponent() {
			return component;
		}

		public DockKey getDockKey() {
			return dockKey;
		}

		public String toString() {
			return "( MyDock -> id : " + dockKey.getKey() + " name : " //$NON-NLS-1$ //$NON-NLS-2$
					+ dockKey.getName() + " state : " //$NON-NLS-1$
					+ this.getDockKey() + " )"; //$NON-NLS-1$
		}
	}

	private final class MyDockableResolver implements DockableResolver {

		public Dockable resolveDockable(String keyName) {
			if (dockableMap.containsKey(keyName)) {
				return dockableMap.get(keyName);
			}
			return null;
		}
	}

	private final class MyDockableStateChangeListener implements DockableStateChangeListener {
		public void dockableStateChanged(DockableStateChangeEvent event) {
			DockableState state = event.getNewState();
			if (state != null && state.getDockable() != null) {
				String id = state.getDockable().getDockKey().getKey();
				IViewDockable viewDockable = viewDockableMap.get(id);

				DockableState oldState = event.getPreviousState();

				ViewDockableStateEvent e = null;

				switch (state.getLocation()) {
				case CLOSED:
					e = new ViewDockableStateEvent(CoreView.this, State.CLOSED, false);
					break;
				case DOCKED:
				case FLOATING:
					if (oldState != null && oldState.getLocation() == DockableState.Location.MAXIMIZED) {
						e = new ViewDockableStateEvent(CoreView.this, State.RESTORE, false);
					} else if (oldState != null && oldState.getLocation() == DockableState.Location.CLOSED) {
						e = new ViewDockableStateEvent(CoreView.this, State.OPENED, false);
					} else if (oldState == null) {
						e = new ViewDockableStateEvent(CoreView.this, State.OPENED, false);
					} else {
						e = new ViewDockableStateEvent(CoreView.this, State.SHOW, false);
					}
					break;
				case HIDDEN:
					e = new ViewDockableStateEvent(CoreView.this, State.HIDDEN, false);
					break;
				case MAXIMIZED:
					e = new ViewDockableStateEvent(CoreView.this, State.MAXIMIZE, false);
					break;
				}
				viewDockable.viewDockableStateChanged(e);
			}
		}
	}

	private final class MyDockableSelectionListener implements DockableSelectionListener {

		private Dockable old;

		public void selectionChanged(DockableSelectionEvent e) {
			if (old != null) {
				String id = old.getDockKey().getKey();
				IViewDockable viewDockable = viewDockableMap.get(id);
				ViewDockableStateEvent v = new ViewDockableStateEvent(CoreView.this, State.UNSELECTED, false);
				viewDockable.viewDockableStateChanged(v);
			}

			old = e.getSelectedDockable();
			if (e.getSelectedDockable() != null) {
				String id = e.getSelectedDockable().getDockKey().getKey();
				IViewDockable viewDockable = viewDockableMap.get(id);
				ViewDockableStateEvent v = new ViewDockableStateEvent(CoreView.this, State.SELECTED, false);
				viewDockable.viewDockableStateChanged(v);
			}
		}
	}
}
