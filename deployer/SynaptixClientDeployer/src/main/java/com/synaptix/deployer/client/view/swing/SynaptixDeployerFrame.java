package com.synaptix.deployer.client.view.swing;

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
import java.util.TooManyListenersException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
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

import com.google.inject.Inject;
import com.synaptix.client.view.IView;
import com.synaptix.deployer.client.core.SynaptixDeployerContext;
import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.client.view.ISynaptixDeployerWindow;
import com.synaptix.swing.docking.SyFloatingFrame;
import com.synaptix.widget.core.view.swing.DefaultRibbonContext;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.IRibbonContextView;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.vlsolutions.swing.docking.DefaultDockableContainerFactory;
import com.vlsolutions.swing.docking.DockableContainerFactory;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.FloatingDockableContainer;
import com.vlsolutions.swing.docking.TabbedDockableContainer;

public class SynaptixDeployerFrame extends JRibbonFrame implements ISynaptixDeployerWindow {

	private static final long serialVersionUID = -8911468986665683166L;

	private static final Log LOG = LogFactory.getLog(SynaptixDeployerFrame.class);

	private static final ImageIcon ICON = new ImageIcon(SynaptixDeployerFrame.class.getResource("/images/deployer/deployer.png"));

	private static final ResizableIcon ARROW_UP_ICON = ImageWrapperResizableIcon.getIcon(SynaptixDeployerFrame.class.getResource("/images/deployer/arrow_up.png"), new Dimension(18, 18));

	private static final ResizableIcon ARROW_DOWN_ICON = ImageWrapperResizableIcon.getIcon(SynaptixDeployerFrame.class.getResource("/images/deployer/arrow_down.png"), new Dimension(18, 18));

	private static final int DRAG_TIMER = 500;

	private final SyDockingContext dockingContext;

	private SynaptixDeployerContext synaptixDeployerContext;

	private RichTooltip minimizeRibbonRichTooltip;

	private RichTooltip maximizeRibbonRichTooltip;

	private JCommandButton minimizeMaximizeRibbonCommandButton;

	private DefaultRibbonContext ribbonContext;

	private String titleSuffix;

	@Inject
	public SynaptixDeployerFrame(SyDockingContext dockingContext) {
		super();

		this.dockingContext = dockingContext;

		initComponents();
	}

	@Override
	public void setSynaptixDeployerContext(SynaptixDeployerContext synaptixDeployerContext) {
		this.synaptixDeployerContext = synaptixDeployerContext;
	}

	private void initComponents() {
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setSize(1280, 720);
		updateTitle();
		this.setApplicationIcon(ImageWrapperResizableIcon.getIcon(ICON.getImage(), new Dimension(32, 32)));

		ribbonContext = new DefaultRibbonContext(getRibbon());

		minimizeRibbonRichTooltip = new RichTooltip();
		minimizeRibbonRichTooltip.setTitle(StaticHelper.getDeployerConstantsBundle().minimizeTheRibbon());
		minimizeRibbonRichTooltip.addDescriptionSection(StaticHelper.getDeployerConstantsBundle().displayOnlyTheNameOfTheTabsOnTheRibbon());

		maximizeRibbonRichTooltip = new RichTooltip();
		maximizeRibbonRichTooltip.setTitle(StaticHelper.getDeployerConstantsBundle().maximizeTheRibbon());
		maximizeRibbonRichTooltip.addDescriptionSection(StaticHelper.getDeployerConstantsBundle().displaysTheRibbonSoItIsAlwaysDevelopedEvenAfterClickingACommand());

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

		DockingDesktop dd = new DockingDesktop("principal", dockingContext);

		this.getContentPane().add(dd, BorderLayout.CENTER);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				synaptixDeployerContext.stop();
			}
		});

		RibbonApplicationMenu menu = new RibbonApplicationMenu();

		RibbonApplicationMenuEntryPrimary exitMenu = new RibbonApplicationMenuEntryPrimary(ImageWrapperResizableIcon.getIcon(SynaptixDeployerFrame.class.getResource("/images/deployer/exit.jpg"),
				new Dimension(18, 18)), StaticHelper.getDeployerConstantsBundle().exit(), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synaptixDeployerContext.stop();
			}
		}, CommandButtonKind.ACTION_ONLY);
		menu.addMenuEntry(exitMenu);

		RichTooltip rt = new RichTooltip();
		rt.setTitle(StaticHelper.getDeployerConstantsBundle().deployer());
		// rt.addDescriptionSection("Coucou");
		getRibbon().setApplicationMenuRichTooltip(rt);

		getRibbon().setApplicationMenu(menu);
		getRibbon().setApplicationIcon(ImageWrapperResizableIcon.getIcon(SynaptixDeployerFrame.class.getResource("/images/deployer/database.png"), new Dimension(18, 18)));
	}

	@Override
	public void setTitleSuffix(String titleSuffix) {
		this.titleSuffix = titleSuffix;
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
	public void registerView(IView view) {
		if (view instanceof IDockingContextView) {
			((IDockingContextView) view).initializeDockingContext(dockingContext);
		}
		if (view instanceof IRibbonContextView) {
			((IRibbonContextView) view).initializeRibbonContext(ribbonContext);
		}
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

	private void updateTitle() {
		if (titleSuffix != null) {
			this.setTitle("Deployer " + titleSuffix);
		} else {
			this.setTitle("Deployer");
		}
	}
}
