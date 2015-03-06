package com.synaptix.swing.timeline;

import java.awt.Container;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;

import com.synaptix.swing.JTimeline;
import com.synaptix.swing.event.TimelineDatesModelListener;
import com.synaptix.swing.plaf.TimelineScaleUI;

public class JTimelineScale extends JComponent implements
		TimelineDatesModelListener {

	private static final long serialVersionUID = -2647475618941932330L;

	private static final String uiClassID = "TimelineScale"; //$NON-NLS-1$

	private JTimeline timeline;

	static {
		UIManager.getDefaults().put(uiClassID,
				"com.synaptix.swing.plaf.basic.BasicTimelineScaleUI"); //$NON-NLS-1$
	}

	public JTimelineScale() {
		super();

		this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		
		updateUI();
	}

	public void addNotify() {
		super.addNotify();
		configureEnclosingScrollPane();
	}
	
	protected void configureEnclosingScrollPane() {
		Container p = getParent();
		if (p instanceof JScrollPane) {
			JScrollPane scrollPane = (JScrollPane) p;

			scrollPane.getHorizontalScrollBar().addAdjustmentListener(
					new AdjustmentListener() {
						public void adjustmentValueChanged(AdjustmentEvent e) {
							repaint();
						}
					});
		}
	}
	
	public JTimeline getTimeline() {
		return timeline;
	}

	public void setTimeline(JTimeline timeline) {
		JTimeline old = this.timeline;
		this.timeline = timeline;

		if (old != null) {
			old.getDatesModel().removeDatesModelListener(this);
		}
		if (timeline != null) {
			timeline.getDatesModel().addDatesModelListener(this);
		}
		
		firePropertyChange("timeline", old, timeline); //$NON-NLS-1$
	}

	public TimelineScaleUI getUI() {
		return (TimelineScaleUI) ui;
	}

	public void setUI(TimelineScaleUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((TimelineScaleUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}
	
	public void datesChanged(ChangeEvent e) {
		repaint();
	}
}
