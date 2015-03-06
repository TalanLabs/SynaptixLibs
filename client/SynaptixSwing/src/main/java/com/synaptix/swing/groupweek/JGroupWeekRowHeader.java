package com.synaptix.swing.groupweek;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.synaptix.swing.JGroupWeek;
import com.synaptix.swing.plaf.GroupWeekRowHeaderUI;

public class JGroupWeekRowHeader extends JComponent {

	private static final long serialVersionUID = 608988628778077213L;

	private static final String uiClassID = "GroupWeekRowHeaderUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID,
				"com.synaptix.swing.plaf.basic.BasicGroupWeekRowHeaderUI"); //$NON-NLS-1$
	}

	private GroupWeekRowHeaderCellRenderer defaultRenderer;

	private JGroupWeek groupWeek;

	public JGroupWeekRowHeader() {
		super();

		initializeLocalVars();

		updateUI();
	}

	private void initializeLocalVars() {
		this.setOpaque(true);

		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.registerComponent(this);
		defaultRenderer = new DefaultGroupWeekRowHeaderCellRenderer();
	}

	public void setGroupWeek(JGroupWeek groupWeek) {
		this.groupWeek = groupWeek;
	}

	public JGroupWeek getGroupWeek() {
		return groupWeek;
	}

	public void setDefaultRenderer(
			GroupWeekRowHeaderCellRenderer defaultRenderer) {
		if (defaultRenderer != null && this.defaultRenderer != defaultRenderer) {
			this.defaultRenderer = defaultRenderer;
			resizeAndRepaint();
		}
	}

	public GroupWeekRowHeaderCellRenderer getDefaultRenderer() {
		return defaultRenderer;
	}

	public Component prepareGroupWeekRowHeaderCellRendererComponent(int group,
			int row) {
		return defaultRenderer.getGroupWeekRowHeaderCellRendererComponent(this,
				groupWeek.isSelection()
						&& groupWeek.getSelectedGroup() == group
						&& (groupWeek.getSelectedRow() == row || row == -1),
				false, group, row);
	}

	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	public GroupWeekRowHeaderUI getUI() {
		return (GroupWeekRowHeaderUI) ui;
	}

	public void setUI(GroupWeekRowHeaderUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((GroupWeekRowHeaderUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}
}
