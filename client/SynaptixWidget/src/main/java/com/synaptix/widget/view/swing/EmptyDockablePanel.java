package com.synaptix.widget.view.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;

public class EmptyDockablePanel extends WaitComponentFeedbackPanel implements Dockable, IDockingContextView {

	private static final long serialVersionUID = 2510837817830703483L;

	public static final String ID_DOCKABLE = EmptyDockablePanel.class.getName();

	private static final ImageIcon errorImage = new ImageIcon(JNotificationBadgesPanel.class.getResource("/com/synaptix/widget/view/swing/erreur/bigError.png"));

	private final DockKey dockKey;

	public EmptyDockablePanel(String dockKey) {
		super();

		this.dockKey = new DockKey(dockKey, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().invalidWindow()); // too soon?
		this.dockKey.setFloatEnabled(true);
		this.dockKey.setAutoHideEnabled(false);

		this.addContent(buildContent());
	}

	private Component buildContent() {
		FormLayout layout = new FormLayout("CENTER:DEFAULT:GROW(1.0)", "BOTTOM:DEFAULT:GROW(0.5),3DLU,CENTER:DEFAULT:NONE,3DLU,TOP:DEFAULT:GROW(0.5),BOTTOM:DEFAULT:NONE");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.addLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().errorInvalidWindow(), cc.xy(1, 1));
		builder.add(new JLabel(errorImage), cc.xy(1, 3));
		builder.addLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().youMightHaveClosedTheApplicationWithAWindowThatDoesntExistAnymore(), cc.xy(1, 5));
		JLabel dockLabel = new JLabel(dockKey.getKey());
		dockLabel.setForeground(Color.lightGray);
		dockLabel.setFont(dockLabel.getFont().deriveFont(8f));
		builder.add(dockLabel, cc.xy(1, 6, CellConstraints.LEFT, CellConstraints.BOTTOM));
		return builder.getPanel();
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
	public void initializeDockingContext(SyDockingContext dockingContext) {
		dockingContext.registerDockable(this);
	}
}
