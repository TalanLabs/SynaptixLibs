package com.synaptix.swing.simpletimeline;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;

import com.synaptix.swing.JSimpleTimeline;
import com.synaptix.swing.event.SimpleTimelineDatesListener;
import com.synaptix.swing.plaf.SimpleTimelineDatesHeaderUI;

public class JSimpleTimelineDatesHeader extends JComponent {

	private static final long serialVersionUID = -7863759892137733142L;

	public static final int DEFAULT_DAY_HEIGHT = 40;

	public static final int DEFAULT_DAY_WIDTH = 400;

	public static final int MIN_DAY_WIDTH = 100;

	public static final int MAX_DAY_WIDTH = 2000;

	private static final String uiClassID = "SimpleTimelineDatesHeaderUI"; //$NON-NLS-1$

	private EventListenerList listenerList;

	private JSimpleTimeline simpleTimeline;

	private int dayWidth;

	static {
		UIManager
				.getDefaults()
				.put(uiClassID,
						"com.synaptix.swing.plaf.basic.BasicSimpleTimelineDatesHeaderUI"); //$NON-NLS-1$
	}

	public JSimpleTimelineDatesHeader() {
		super();
		setOpaque(true);

		simpleTimeline = null;

		dayWidth = DEFAULT_DAY_WIDTH;

		listenerList = new EventListenerList();

		updateUI();
	}

	public SimpleTimelineDatesHeaderUI getUI() {
		return (SimpleTimelineDatesHeaderUI) ui;
	}

	public void setUI(SimpleTimelineDatesHeaderUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((SimpleTimelineDatesHeaderUI) UIManager.getUI(this));
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

	public int getDayWidth() {
		return dayWidth;
	}

	public void setDayWidth(int dayWidth) {
		if (dayWidth < MIN_DAY_WIDTH) {
			dayWidth = MIN_DAY_WIDTH;
		}
		if (dayWidth > MAX_DAY_WIDTH) {
			dayWidth = MAX_DAY_WIDTH;
		}
		
		int old = this.dayWidth;
		this.dayWidth = dayWidth;
		firePropertyChange("dayWidth", old, dayWidth); //$NON-NLS-1$

		fireDayWidthChanged();		
	}

	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}
	
	public void addDatesListener(SimpleTimelineDatesListener x) {
		listenerList.add(SimpleTimelineDatesListener.class, x);
	}

	public void removeDatesListener(SimpleTimelineDatesListener x) {
		listenerList.remove(SimpleTimelineDatesListener.class, x);
	}

	protected void fireDayWidthChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleTimelineDatesListener.class) {
				((SimpleTimelineDatesListener) listeners[i + 1])
						.datesDayWidthChanged();
			}
		}
	}
}
