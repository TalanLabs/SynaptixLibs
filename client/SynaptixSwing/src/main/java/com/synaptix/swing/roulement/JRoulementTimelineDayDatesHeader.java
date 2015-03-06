package com.synaptix.swing.roulement;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.plaf.RoulementTimelineDayDatesHeaderUI;
import com.synaptix.swing.roulement.event.RoulementTimelineWeekDatesListener;

public class JRoulementTimelineDayDatesHeader extends JComponent {

	private static final long serialVersionUID = -7863759892137733142L;

	public static final int DEFAULT_DAY_HEIGHT = 40;

	public static final int DEFAULT_DAY_WIDTH = 400;

	public static final int MIN_DAY_WIDTH = 100;

	public static final int MAX_DAY_WIDTH = 2000;

	private static final String uiClassID = "RoulementTimelineDayDatesHeaderUI"; //$NON-NLS-1$

	private EventListenerList listenerList;

	private JRoulementTimeline roulementTimeline;

	private RoulementTimelineDayDatesHeaderRenderer headerRenderer;

	private int dayWidth;

	private int minDayWidth;

	private int maxDayWidth;

	static {
		UIManager
				.getDefaults()
				.put(uiClassID,
						"com.synaptix.swing.plaf.basic.BasicRoulementTimelineDayDatesHeaderUI"); //$NON-NLS-1$
	}

	public JRoulementTimelineDayDatesHeader() {
		super();

		this.setOpaque(true);

		minDayWidth = MIN_DAY_WIDTH;
		maxDayWidth = MAX_DAY_WIDTH;

		roulementTimeline = null;

		dayWidth = DEFAULT_DAY_WIDTH;

		listenerList = new EventListenerList();

		headerRenderer = new DefaultRoulementTimelineDayDatesHeaderRenderer();

		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.registerComponent(this);

		updateUI();
	}

	public RoulementTimelineDayDatesHeaderUI getUI() {
		return (RoulementTimelineDayDatesHeaderUI) ui;
	}

	public void setUI(RoulementTimelineDayDatesHeaderUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((RoulementTimelineDayDatesHeaderUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public void setRoulementTimeline(JRoulementTimeline roulementTimeline) {
		JRoulementTimeline old = this.roulementTimeline;
		this.roulementTimeline = roulementTimeline;
		firePropertyChange("timeline", old, roulementTimeline); //$NON-NLS-1$
	}

	public JRoulementTimeline getRoulementTimeline() {
		return roulementTimeline;
	}

	public void setHeaderRenderer(
			RoulementTimelineDayDatesHeaderRenderer headerRenderer) {
		this.headerRenderer = headerRenderer;
	}

	public RoulementTimelineDayDatesHeaderRenderer getHeaderRenderer() {
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

	public void addDatesListener(RoulementTimelineWeekDatesListener x) {
		listenerList.add(RoulementTimelineWeekDatesListener.class, x);
	}

	public void removeDatesListener(RoulementTimelineWeekDatesListener x) {
		listenerList.remove(RoulementTimelineWeekDatesListener.class, x);
	}

	protected void fireDayWidthChanged(int oldValue, int newValue) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == RoulementTimelineWeekDatesListener.class) {
				((RoulementTimelineWeekDatesListener) listeners[i + 1])
						.datesDayWidthChanged(oldValue, newValue);
			}
		}
	}

	public String getToolTipText(MouseEvent event) {
		String tip = null;
		Point p = event.getPoint();

		DayDate dayDate = roulementTimeline.dayDateAtPoint(p);
		if (dayDate != null) {
			if (headerRenderer != null) {
				tip = headerRenderer.getToolTipText(dayDate);
			}
		}

		return tip;
	}
}
