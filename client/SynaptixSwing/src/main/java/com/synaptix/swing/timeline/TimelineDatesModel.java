package com.synaptix.swing.timeline;

import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;

import com.synaptix.swing.event.TimelineDatesModelListener;
import com.synaptix.swing.utils.DateTimeUtils;

public class TimelineDatesModel {

	public static final int DEFAULT_WIDTH = 100;

	public static final int MIN_WIDTH = 20;
	
	public static final int MAX_WIDTH = 1200;
	
	private Date dateMin;

	private Date dateMax;

	private int width;

	transient protected ChangeEvent changeEvent = null;

	protected EventListenerList listenerList = new EventListenerList();

	public TimelineDatesModel() {
		this(new Date(), new Date());
	}

	public TimelineDatesModel(Date dateMin, Date dateMax) {
		this.dateMax = dateMax;
		this.dateMin = dateMin;
		width = DEFAULT_WIDTH;
	}

	public Date getDateMax() {
		return dateMax;
	}

	public void setDateMax(Date dateMax) {
		this.dateMax = dateMax;
		fireDatesChanged();
	}

	public Date getDateMin() {
		return dateMin;
	}

	public void setDateMin(Date dateMin) {
		this.dateMin = dateMin;
		fireDatesChanged();
	}

	public int getPixels(Date min, Date max) {
		return (int) (DateTimeUtils.getNumberOfMinutes(min, max) * getWidth() / (60 * 24));
	}

	public int getPixels(Date date) {
		return getPixels(DateTimeUtils.clearHourForDate(getDateMin()), date);
	}

	public Date getDateInX(int x) {
		Calendar cMin = Calendar.getInstance();
		cMin.setTime(DateTimeUtils.clearHourForDate(getDateMin()));
		
		Calendar c = (Calendar)cMin.clone();
		
		int nbDays = x / getWidth();
		int minutes = ((x - nbDays * getWidth()) * 24 * 60) / getWidth();
		
		c.add(Calendar.DAY_OF_YEAR, nbDays);
		c.add(Calendar.MINUTE, minutes);
		
		long deltaOffset = c.get(Calendar.DST_OFFSET) - cMin.get(Calendar.DST_OFFSET);
		
		long milliseconds = (long) ((double) x * (60.0 * 24.0 * 1000.0 * 60.0) / (double) getWidth());

		c.setTimeInMillis(cMin.getTimeInMillis() + milliseconds - deltaOffset);

		Calendar cMax = Calendar.getInstance();
		cMax.setTime(getDateMax());
		cMax.add(Calendar.DAY_OF_YEAR, 1);
		cMax.add(Calendar.SECOND, -1);
		if (c.after(cMax)) {
			return cMax.getTime();
		}
		
		return c.getTime();
	}

	public void addDatesModelListener(TimelineDatesModelListener x) {
		listenerList.add(TimelineDatesModelListener.class, x);
	}

	public void removeDatesModelListener(TimelineDatesModelListener x) {
		listenerList.remove(TimelineDatesModelListener.class, x);
	}

	public TimelineDatesModelListener[] getDatesModelListeners() {
		return (TimelineDatesModelListener[]) listenerList
				.getListeners(TimelineDatesModelListener.class);
	}

	protected void fireDatesChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TimelineDatesModelListener.class) {
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((TimelineDatesModelListener) listeners[i + 1])
						.datesChanged(changeEvent);
			}
		}
	}

	public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
		return listenerList.getListeners(listenerType);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		fireDatesChanged();
	}
}
