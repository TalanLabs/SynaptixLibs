package com.synaptix.deployer.client.view.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextArea;

import org.jdesktop.swingx.JXHeader;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.deployer.client.controller.WatcherController;
import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.client.view.IWatcherView;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.utils.ToolBarFactory;
import com.synaptix.widget.core.view.swing.IDockable;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.IRibbonContextView;
import com.synaptix.widget.core.view.swing.RibbonContext;
import com.synaptix.widget.core.view.swing.RibbonContext.RibbonBandBuilder;
import com.synaptix.widget.core.view.swing.RibbonContext.RibbonTaskBuilder;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.event.DockableStateChangeEvent;
import com.vlsolutions.swing.docking.event.DockableStateChangeListener;

public class WatcherPanel extends WaitComponentFeedbackPanel implements IRibbonContextView, IDockable, IDockingContextView, IWatcherView {

	private static final long serialVersionUID = -8809674695461015295L;

	private static final ResizableIcon START_ICON = ImageWrapperResizableIcon.getIcon(SynaptixDeployerFrame.class.getResource("/images/deployer/start.png"), new Dimension(18, 18));

	private static final ResizableIcon STOP_ICON = ImageWrapperResizableIcon.getIcon(SynaptixDeployerFrame.class.getResource("/images/deployer/stop.png"), new Dimension(18, 18));

	private WatcherController watcherController;

	private RibbonContext ribbonContext;

	private DockKey dockKey;

	private SyDockingContext dockingContext;

	private JXHeader header;

	private JTextArea textArea;

	private JButton startStopButton;

	private JButton clearButton;

	private JButton downloadButton;

	public WatcherPanel(WatcherController watcherController) {
		super();

		this.watcherController = watcherController;

		initComponents();

		this.addContent(buildContents());
	}

	private void initComponents() {
		header = new JXHeader(StaticHelper.getWatcherConstantsBundle().selectAnEnvironmentAndInstanceToWatch(), null);

		textArea = new JTextArea();
		textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
		textArea.setLineWrap(true);
		textArea.setEditable(false);

		startStopButton = new JButton(StaticHelper.getWatcherConstantsBundle().start(), START_ICON);
		startStopButton.setEnabled(false);
		startStopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				watcherController.switchState();
			}
		});

		clearButton = new JButton(StaticHelper.getWatcherConstantsBundle().clearConsole(), ImageWrapperResizableIcon.getIcon(SynaptixDeployerFrame.class.getResource("/images/deployer/clear.png"),
				new Dimension(18, 18)));
		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setText(null);
			}
		});

		downloadButton = new JButton(StaticHelper.getWatcherConstantsBundle().download());
		downloadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				watcherController.download();
			}
		});
	}

	private Component buildContents() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(header, cc.xy(1, 1));
		builder.add(buildCenterPanel(), cc.xy(1, 3));
		return builder.getPanel();
	}

	private Component buildCenterPanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();

		builder.add(ToolBarFactory.buildToolBar(startStopButton, clearButton, null, downloadButton), cc.xy(1, 1));
		builder.add(new JSyScrollPane(textArea), cc.xy(1, 3));
		return builder.getPanel();
	}

	public String getIdDockable() {
		return this.getClass().getName();
	}

	@Override
	public DockKey getDockKey() {
		return dockKey;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getCategory() {
		return "DEPLOYER";
	}

	@Override
	public void initializeDockingContext(SyDockingContext dockingContext) {
		this.dockingContext = dockingContext;

		dockKey = new DockKey(getIdDockable(), StaticHelper.getWatcherConstantsBundle().watcher());
		dockKey.setFloatEnabled(true);
		dockKey.setAutoHideEnabled(false);

		dockingContext.registerDockable(this);

		dockingContext.addDockableStateChangeListener(this, new DockableStateChangeListener() {
			@Override
			public void dockableStateChanged(DockableStateChangeEvent event) {
			}
		});
	}

	@Override
	public void initializeRibbonContext(RibbonContext ribbonContext) {
		ResizableIcon icon = ImageWrapperResizableIcon.getIcon(WatcherPanel.class.getResource("/images/deployer/sonar.png"), new Dimension(30, 30));
		JCommandButton cb = new JCommandButton(StaticHelper.getWatcherConstantsBundle().watcher(), icon);
		cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dockingContext.showDockable(getIdDockable());
			}
		});

		String ribbonTaskTitle = StaticHelper.getDeployerConstantsBundle().exploitation();
		String ribbonBandTitle = StaticHelper.getDeployerConstantsBundle().exploitation();

		RibbonTaskBuilder ribbonTaskBuilder = ribbonContext.addRibbonTask(ribbonTaskTitle, 0);
		RibbonBandBuilder ribbonBand = ribbonTaskBuilder.addRibbonBand(ribbonBandTitle, 2);
		ribbonBand.addCommandeButton(cb, RibbonElementPriority.TOP);
	}

	@Override
	public void addConsoleLine(String line) {
		textArea.append(line);
		textArea.setCaretPosition(textArea.getText().length());
	}

	@Override
	public void setRunning(boolean running) {
		if (running) {
			startStopButton.setText(StaticHelper.getWatcherConstantsBundle().stop());
			startStopButton.setIcon(STOP_ICON);
		} else {
			startStopButton.setText(StaticHelper.getWatcherConstantsBundle().start());
			startStopButton.setIcon(START_ICON);
		}
	}

	@Override
	public void setTitle(String title, String subtitle) {
		startStopButton.setEnabled(true);
		header.setTitle("<html><b>" + title + "</b></title>");
		header.setDescription(subtitle);
		addConsoleLine("\n" + StaticHelper.getWatcherConstantsBundle().switchedToEnvironmentAndInstance(title, subtitle) + "\n");
	}

	@Override
	public void reveal() {
		dockingContext.showDockable(dockKey.getKey());
	}
}
