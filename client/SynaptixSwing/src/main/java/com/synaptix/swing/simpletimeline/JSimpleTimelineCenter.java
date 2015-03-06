package com.synaptix.swing.simpletimeline;

import javax.swing.JComponent;
import javax.swing.UIManager;

import com.synaptix.swing.JSimpleTimeline;
import com.synaptix.swing.plaf.SimpleTimelineCenterUI;

public class JSimpleTimelineCenter extends JComponent {

	private static final long serialVersionUID = -7863759892137733142L;

	private static final String uiClassID = "SimpleTimelineCenterUI"; //$NON-NLS-1$

	private JSimpleTimeline simpleTimeline;
	
	static {
		UIManager
				.getDefaults()
				.put(uiClassID,
						"com.synaptix.swing.plaf.basic.BasicSimpleTimelineCenterUI"); //$NON-NLS-1$
	}

	public JSimpleTimelineCenter() {
		super();
		setOpaque(true);

		simpleTimeline = null;

		updateUI();
	}

	public SimpleTimelineCenterUI getUI() {
		return (SimpleTimelineCenterUI) ui;
	}

	public void setUI(SimpleTimelineCenterUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((SimpleTimelineCenterUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public void setSimpleTimeline(JSimpleTimeline simpleTimeline) {
		JSimpleTimeline old = this.simpleTimeline;
		this.simpleTimeline = simpleTimeline;
		firePropertyChange("timeline", old, simpleTimeline); //$NON-NLS-1$
	}

	public JSimpleTimeline getSimpleTimeline() {
		return simpleTimeline;
	}
	
	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}
}
