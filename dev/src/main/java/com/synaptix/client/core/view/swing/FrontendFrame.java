package com.synaptix.client.core.view.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.TransferHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;

import com.synaptix.client.core.controller.FrontendController;
import com.synaptix.client.core.view.IFrontendView;
import com.synaptix.client.view.IView;
import com.synaptix.prefs.SyPreferences;
import com.synaptix.swing.docking.SyFloatingFrame;
import com.synaptix.widget.core.view.swing.DefaultRibbonContext;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.IRibbonContextView;
import com.synaptix.widget.core.view.swing.PerspectivesManager;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.vlsolutions.swing.docking.DefaultDockableContainerFactory;
import com.vlsolutions.swing.docking.DockableContainerFactory;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.FloatingDockableContainer;
import com.vlsolutions.swing.docking.TabbedDockableContainer;

/**
 * FrontendFrame, create JRibbonFrame and SyDockingContext
 * 
 * @author Gaby
 * 
 */
public class FrontendFrame extends JRibbonFrame implements IFrontendView {

	private static final long serialVersionUID = -3890537056538947805L;

	private static final Log LOG = LogFactory.getLog(FrontendFrame.class);

	private static final String SAVE_NAME = "com.synaptix.client.core.view.swing.FrontendFrame";

	private static final ImageIcon ICON = new ImageIcon(FrontendFrame.class.getResource("/images/core/icon.png"));

	private static final ResizableIcon ARROW_UP_ICON = ImageWrapperResizableIcon.getIcon(FrontendFrame.class.getResource("/images/core/arrow_up.png"), new Dimension(18, 18));

	private static final ResizableIcon ARROW_DOWN_ICON = ImageWrapperResizableIcon.getIcon(FrontendFrame.class.getResource("/images/core/arrow_down.png"), new Dimension(18, 18));

	private static final int DRAG_TIMER = 500;

	private SyDockingContext dockingContext;

	private FrontendController frontendController;

	private RichTooltip minimizeRibbonRichTooltip;

	private RichTooltip maximizeRibbonRichTooltip;

	private JCommandButton minimizeMaximizeRibbonCommandButton;

	private DefaultRibbonContext ribbonContext;

	private String titleSuffix;

	private String connectionState;

	public FrontendFrame(FrontendController frontendController) {
		super();

		this.frontendController = frontendController;

		initComponents();
	}

	private void initComponents() {
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setSize(1280, 720);
		updateTitle();
		this.setApplicationIcon(ImageWrapperResizableIcon.getIcon(ICON.getImage(), new Dimension(32, 32)));

		ribbonContext = new DefaultRibbonContext(getRibbon());

		minimizeRibbonRichTooltip = new RichTooltip();
		minimizeRibbonRichTooltip.setTitle("minimizeTheRibbon");
		minimizeRibbonRichTooltip.addDescriptionSection("displayOnlyTheNameOfTheTabsOnTheRibbon");

		maximizeRibbonRichTooltip = new RichTooltip();
		maximizeRibbonRichTooltip.setTitle("maximizeTheRibbon");
		maximizeRibbonRichTooltip.addDescriptionSection("displaysTheTapeSoItIsAlwaysDevelopedEvenAfterClickingACommand");

		minimizeMaximizeRibbonCommandButton = new JCommandButton(ARROW_UP_ICON);
		minimizeMaximizeRibbonCommandButton.setActionRichTooltip(minimizeRibbonRichTooltip);
		minimizeMaximizeRibbonCommandButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getRibbon().isMinimized()) {
					maximizeRibbon();
				} else {
					minimizeRibbon();
				}
			}
		});

		this.getRibbon().addHelpPanelComponent(minimizeMaximizeRibbonCommandButton);

		DockableContainerFactory.setFactory(new MyDockableContainerFactory());

		dockingContext = new SyDockingContext();
		dockingContext.setFrontendContext(frontendController);

		DockingDesktop dd = new DockingDesktop("principal", dockingContext);

		this.getContentPane().add(dd, BorderLayout.CENTER);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frontendController.exit();
			}
		});

		RibbonApplicationMenu menu = new RibbonApplicationMenu();

		RibbonApplicationMenuEntryPrimary logoutMenu = new RibbonApplicationMenuEntryPrimary(ImageWrapperResizableIcon.getIcon(FrontendFrame.class.getResource("/images/core/logout.png"),
				new Dimension(18, 18)), "logout", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frontendController.logout();
			}
		}, CommandButtonKind.ACTION_ONLY);
		menu.addMenuEntry(logoutMenu);

		RibbonApplicationMenuEntryPrimary exitMenu = new RibbonApplicationMenuEntryPrimary(ImageWrapperResizableIcon.getIcon(FrontendFrame.class.getResource("/images/core/exit.jpg"), new Dimension(
				18, 18)), "exit", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frontendController.exit();
			}
		}, CommandButtonKind.ACTION_ONLY);
		menu.addMenuEntry(exitMenu);

		RichTooltip rt = new RichTooltip();
		rt.setTitle("psc");
		// rt.addDescriptionSection("Coucou");
		getRibbon().setApplicationMenuRichTooltip(rt);

		getRibbon().setApplicationMenu(menu);
		getRibbon().setApplicationIcon(ImageWrapperResizableIcon.getIcon(FrontendFrame.class.getResource("/database.png"), new Dimension(18, 18)));
	}

	@Override
	public void start() {
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	@Override
	public void builds() {
		ribbonContext.buildRibbon();
	}

	@Override
	public void loads() {
		try {
			loadPerspectives();
		} catch (Exception e) {
			e.printStackTrace();
		}

		loadRibbon();
	}

	@Override
	public void saves() {
		saveRibbon();

		try {
			savePerspectives();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		this.setVisible(false);
		this.dispose();
	}

	@Override
	public void registerView(IView view) {
		if (view instanceof IDockingContextView) {
			((IDockingContextView) view).initializeDockingContext(dockingContext);
		}
		if (view instanceof IRibbonContextView) {
			((IRibbonContextView) view).initializeRibbonContext(ribbonContext);
		}
	}

	@Override
	public void setMapUrls(String backgroundWmsUrl, String cityWmsUrl, String countryWmsUrl) {
	}

	private void loadRibbon() {
		SyPreferences prefs = SyPreferences.getPreferences();

		if (prefs.getBoolean(SAVE_NAME + "_ribbon_minimized", getRibbon().isMinimized())) {
			minimizeRibbon();
		} else {
			maximizeRibbon();
		}
	}

	private void saveRibbon() {
		SyPreferences prefs = SyPreferences.getPreferences();

		prefs.putBoolean(SAVE_NAME + "_ribbon_minimized", getRibbon().isMinimized());
	}

	private void minimizeRibbon() {
		getRibbon().setMinimized(true);
		minimizeMaximizeRibbonCommandButton.setIcon(ARROW_DOWN_ICON);
		minimizeMaximizeRibbonCommandButton.setActionRichTooltip(maximizeRibbonRichTooltip);
	}

	private void maximizeRibbon() {
		getRibbon().setMinimized(false);
		minimizeMaximizeRibbonCommandButton.setIcon(ARROW_UP_ICON);
		minimizeMaximizeRibbonCommandButton.setActionRichTooltip(minimizeRibbonRichTooltip);
	}

	private void loadPerspectives() throws Exception {
		SyPreferences prefs = SyPreferences.getPreferences();

		PerspectivesManager pm = dockingContext.getPerspectivesManager();

		int nbWorkspaces = prefs.getInt(SAVE_NAME + "_nb_workspaces", 0); //$NON-NLS-1$
		for (int i = 0; i < nbWorkspaces; i++) {
			String id = prefs.get(SAVE_NAME + "_workspace_id_" //$NON-NLS-1$
					+ String.valueOf(i), null);
			String name = prefs.get(SAVE_NAME + "_workspace_name_" //$NON-NLS-1$
					+ String.valueOf(i), null);
			String binding = prefs.get(SAVE_NAME + "_workspace_binding_" //$NON-NLS-1$
					+ String.valueOf(i), null);
			String description = prefs.get(SAVE_NAME + "_workspace_description_" //$NON-NLS-1$
					+ String.valueOf(i), null);
			String xml = prefs.get(SAVE_NAME + "_workspace_" //$NON-NLS-1$
					+ String.valueOf(i), null);
			if (id != null && name != null && xml != null) {
				pm.addPersonalPerspective(id, name, null, binding != null ? KeyStroke.getKeyStroke(binding) : null, description, xml);
			}
		}

		String workspaceXml = prefs.get(SAVE_NAME + "_workspace_current", null); //$NON-NLS-1$

		pm.setCurrentWorkspace(workspaceXml);
	}

	private void savePerspectives() throws Exception {
		SyPreferences prefs = SyPreferences.getPreferences();

		PerspectivesManager pm = dockingContext.getPerspectivesManager();
		List<PerspectivesManager.Perspective> personalPerspectiveList = pm.getPersonalPerspectives();

		int nbWorkspaces = prefs.getInt(SAVE_NAME + "_nb_workspaces", 0); //$NON-NLS-1$
		for (int i = 0; i < nbWorkspaces; i++) {
			prefs.remove(SAVE_NAME + "_workspace_id_" + String.valueOf(i)); //$NON-NLS-1$
			prefs.remove(SAVE_NAME + "_workspace_name_" + String.valueOf(i)); //$NON-NLS-1$
			prefs.remove(SAVE_NAME + "_workspace_binding_" + String.valueOf(i)); //$NON-NLS-1$
			prefs.remove(SAVE_NAME + "_workspace_description_" + String.valueOf(i)); //$NON-NLS-1$
			prefs.remove(SAVE_NAME + "_workspace_" + String.valueOf(i)); //$NON-NLS-1$
		}

		prefs.putInt(SAVE_NAME + "_nb_workspaces", personalPerspectiveList.size()); //$NON-NLS-1$
		for (int i = 0; i < personalPerspectiveList.size(); i++) {
			PerspectivesManager.Perspective perspective = personalPerspectiveList.get(i);
			prefs.put(SAVE_NAME + "_workspace_id_" + String.valueOf(i), //$NON-NLS-1$
					perspective.getId());
			prefs.put(SAVE_NAME + "_workspace_name_" + String.valueOf(i), //$NON-NLS-1$
					perspective.getName());
			if (perspective.getKeyStroke() != null) {
				prefs.put(SAVE_NAME + "_workspace_binding_" + String.valueOf(i), //$NON-NLS-1$
						perspective.getKeyStroke().toString());
			}
			if (perspective.getDescription() != null) {
				prefs.put(SAVE_NAME + "_workspace_description_" + String.valueOf(i), //$NON-NLS-1$
						perspective.getDescription());
			}
			prefs.put(SAVE_NAME + "_workspace_" + String.valueOf(i), perspective //$NON-NLS-1$
					.getXml());
		}

		String workspaceXml = pm.getCurrentWorkspace();
		prefs.put(SAVE_NAME + "_workspace_current", workspaceXml); //$NON-NLS-1$
	}

	private final class MyDockableContainerFactory extends DefaultDockableContainerFactory {

		@Override
		public FloatingDockableContainer createFloatingDockableContainer(Window owner) {
			SyFloatingFrame floatingFrame = null;
			if (owner instanceof Dialog) {
				floatingFrame = new SyFloatingFrame(((Dialog) owner).getTitle());
			} else {
				floatingFrame = new SyFloatingFrame(((Frame) owner).getTitle());
			}
			floatingFrame.setIconImage(ICON.getImage());
			return floatingFrame;
		}

		@Override
		public TabbedDockableContainer createTabbedDockableContainer() {
			TabbedDockableContainer tabbedDockableContainer = super.createTabbedDockableContainer();
			JTabbedPane tabbedPane = (JTabbedPane) tabbedDockableContainer;
			tabbedPane.setTransferHandler(new MyTransferHandler());
			try {
				tabbedPane.getDropTarget().addDropTargetListener(new MyDropTargetListener());
			} catch (TooManyListenersException e) {
				LOG.error(e, e);
			}
			return tabbedDockableContainer;
		}
	}

	private final class MyTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 2711826507171502030L;

	}

	private final class MyDropTargetListener extends DropTargetAdapter implements ActionListener {

		private JTabbedPane tabbedPane;

		private int nextIndex = -1;

		private Timer timer;

		public MyDropTargetListener() {
			super();

			timer = new Timer(DRAG_TIMER, this);
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			tabbedPane = (JTabbedPane) dtde.getDropTargetContext().getComponent();
			int index = tabbedPane.indexAtLocation(dtde.getLocation().x, dtde.getLocation().y);
			if (index != -1) {
				if (tabbedPane.getSelectedIndex() != index && (nextIndex == -1 || nextIndex != index)) {
					nextIndex = index;
					timer.stop();
					timer.start();
				}
			} else {
				stop();
			}
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			stop();
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {
			stop();
		}

		private void stop() {
			tabbedPane = null;
			nextIndex = -1;
			timer.stop();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			tabbedPane.setSelectedIndex(nextIndex);
		}
	}

	@Override
	public void setTitleSuffix(String titleSuffix) {
		this.titleSuffix = titleSuffix;
		updateTitle();
	}

	private void updateTitle() {
		String title = "Test";
		this.setTitle(title);
	}

	@Override
	public void setConnectionState(String connectionState) {
		this.connectionState = connectionState;
		updateTitle();
	}
}
