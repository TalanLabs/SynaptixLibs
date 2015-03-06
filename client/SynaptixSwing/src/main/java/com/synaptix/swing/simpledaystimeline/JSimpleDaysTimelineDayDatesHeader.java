package com.synaptix.swing.simpledaystimeline;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.event.SimpleDaysTimelineWeekDatesListener;
import com.synaptix.swing.plaf.SimpleDaysTimelineDayDatesHeaderUI;

public class JSimpleDaysTimelineDayDatesHeader extends JComponent {

	private static final long serialVersionUID = -7863759892137733142L;

	public static final int DEFAULT_DAY_HEIGHT = 40;

	public static final int DEFAULT_DAY_WIDTH = 400;

	public static final int MIN_DAY_WIDTH = 100;

	public static final int MAX_DAY_WIDTH = 2000;

	private static final String uiClassID = "SimpleDaysTimelineDayDatesHeaderUI"; //$NON-NLS-1$

	private EventListenerList listenerList;

	private JSimpleDaysTimeline simpleDaysTimeline;

	private SimpleDaysTimelineDayDatesHeaderRenderer headerRenderer;

	private int dayWidth;

	private int minDayWidth;

	private int maxDayWidth;

	static {
		UIManager
				.getDefaults()
				.put(uiClassID,
						"com.synaptix.swing.plaf.basic.BasicSimpleDaysTimelineDayDatesHeaderUI"); //$NON-NLS-1$
	}

	public JSimpleDaysTimelineDayDatesHeader() {
		super();

		this.setOpaque(true);

		minDayWidth = MIN_DAY_WIDTH;
		maxDayWidth = MAX_DAY_WIDTH;

		simpleDaysTimeline = null;

		dayWidth = DEFAULT_DAY_WIDTH;

		listenerList = new EventListenerList();

		headerRenderer = new DefaultSimpleDaysTimelineDayDatesHeaderRenderer();

		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.registerComponent(this);

		updateUI();
	}

	public SimpleDaysTimelineDayDatesHeaderUI getUI() {
		return (SimpleDaysTimelineDayDatesHeaderUI) ui;
	}

	public void setUI(SimpleDaysTimelineDayDatesHeaderUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((SimpleDaysTimelineDayDatesHeaderUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public void setSimpleDaysTimeline(JSimpleDaysTimeline simpleDaysTimeline) {
		JSimpleDaysTimeline old = this.simpleDaysTimeline;
		this.simpleDaysTimeline = simpleDaysTimeline;
		firePropertyChange("timeline", old, simpleDaysTimeline); //$NON-NLS-1$
	}

	public JSimpleDaysTimeline getSimpleDaysTimeline() {
		return simpleDaysTimeline;
	}

	public void setHeaderRenderer(
			SimpleDaysTimelineDayDatesHeaderRenderer headerRenderer) {
		this.headerRenderer = headerRenderer;
	}

	public SimpleDaysTimelineDayDatesHeaderRenderer getHeaderRenderer() {
		return headerRenderer;
	}

	public void setMinDayWidth(int minDayWidth) {
		this.minDayWidth = minDayWidth;

		setDayWidth(dayWidth);
	}

	public void setMaxDayWidth(int maxDayWidth) {
		this.maxDayWidth = maxDayWidth;

		setDayWidth(dayWidth);
	}

	public int getMinDayWidth() {
		return minDayWidth;
	}

	public int getMaxDayWidth() {
		return maxDayWidth;
	}

	public int getDayWidth() {
		return dayWidth;
	}

	public void setDayWidth(int dayWidth) {
		if (dayWidth < getMinDayWidth()) {
			dayWidth = getMinDayWidth();
		}
		if (dayWidth > getMaxDayWidth()) {
			dayWidth = getMaxDayWidth();
		}

		int oldValue = getDayWidth();
		this.dayWidth = dayWidth;
		firePropertyChange("dayWidth", oldValue, dayWidth); //$NON-NLS-1$

		fireDayWidthChanged(oldValue, dayWidth);
	}

	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	public void addDatesListener(SimpleDaysTimelineWeekDatesListener x) {
		listenerList.add(SimpleDaysTimelineWeekDatesListener.class, x);
	}

	public void removeDatesListener(SimpleDaysTimelineWeekDatesListener x) {
		listenerList.remove(SimpleDaysTimelineWeekDatesListener.class, x);
	}

	protected void fireDayWidthChanged(int oldValue, int newValue) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleDaysTimelineWeekDatesListener.class) {
				((SimpleDaysTimelineWeekDatesListener) listeners[i + 1])
						.datesDayWidthChanged(oldValue, newValue);
			}
		}
	}

	public String getToolTipText(MouseEvent event) {
		String tip = null;
		Point p = event.getPoint();

		DayDate dayDate = simpleDaysTimeline.dayDateAtPoint(p);
		if (dayDate != null) {
			if (headerRenderer != null) {
				tip = headerRenderer.getToolTipText(dayDate);
			}
		}

		return tip;
	}
}
