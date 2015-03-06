package com.synaptix.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.plaf.TimelineDatesHeaderUI;
import com.synaptix.swing.timeline.JTimelineDatesHeader;
import com.synaptix.swing.timeline.TimelineDatesModel;
import com.synaptix.swing.utils.DateTimeUtils;

public class BasicTimelineDatesHeaderUI extends TimelineDatesHeaderUI {

	private static final int HEIGHT = 32;

	private static final DateFormat monthDateFormat;

	private static final DateFormat shortDayDateFormat;

	private static final DateFormat normalDayDateFormat;

	private static final DateFormat mediumDayDateFormat;

	private static final DateFormat longDayDateFormat;

	private static final DateFormat shortDayFormat;

	private static final DateFormat normalDayFormat;

	protected JTimelineDatesHeader timelineDatesHeader;

	protected CellRendererPane rendererPane;

	private JLabel label;
	
	static {
		monthDateFormat = new SimpleDateFormat("MMMMMM yyyy"); //$NON-NLS-1$
		shortDayDateFormat = new SimpleDateFormat("EEE dd/MM/yy"); //$NON-NLS-1$
		normalDayDateFormat = new SimpleDateFormat("EEE dd/MM/yyyy"); //$NON-NLS-1$
		mediumDayDateFormat = new SimpleDateFormat("EEE dd MMM yyyy"); //$NON-NLS-1$
		longDayDateFormat = new SimpleDateFormat("EEEE dd MMMMMM yyyy"); //$NON-NLS-1$
		shortDayFormat = new SimpleDateFormat("EEE"); //$NON-NLS-1$
		normalDayFormat = new SimpleDateFormat("EEEE"); //$NON-NLS-1$
	}

	public static ComponentUI createUI(JComponent h) {
		return new BasicTimelineDatesHeaderUI();
	}

	public void installUI(JComponent c) {
		timelineDatesHeader = (JTimelineDatesHeader) c;

		label = new JLabel("", JLabel.CENTER); //$NON-NLS-1$
		label.setOpaque(true);

		rendererPane = new CellRendererPane();
		timelineDatesHeader.add(rendererPane);
	}

	public void uninstallUI(JComponent c) {
		timelineDatesHeader.remove(rendererPane);
		rendererPane = null;
		timelineDatesHeader = null;
	}

	public boolean getIsDayFormat(){
		return timelineDatesHeader.isDayFormat();
	}
	
	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;
		Rectangle clip = g2.getClipBounds();

		TimelineDatesModel dm = timelineDatesHeader.getDatesModel();

		g2.setColor(new Color(238, 237, 229));
		g2.fillRect(clip.x, 0, clip.width, timelineDatesHeader.getHeight());

		Calendar cMin = Calendar.getInstance();
		cMin.setTime(dm.getDateInX(clip.x));

		Calendar cMax = Calendar.getInstance();
		cMax.setTime(dm.getDateInX(clip.x + clip.width));

		g2.setColor(Color.BLACK);

		Calendar cCur = (Calendar) cMin.clone();
		cCur.clear();
		cCur.set(cMin.get(Calendar.YEAR), cMin.get(Calendar.MONTH), cMin
				.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cCur.set(Calendar.MILLISECOND, 0);

		if (dm.getWidth() < 50) {
			boolean newMonth = true;
			int oldMonth = cCur.get(Calendar.MONTH);

			int x = (int) (clip.x / dm.getWidth()) * dm.getWidth();
			int days = DateTimeUtils.getNumberOfDays(cMin.getTime(), cMax
					.getTime());
			int hh = timelineDatesHeader.getHeight() / 2;
			for (int d = 0; d < days; d++) {
				if(!getIsDayFormat()){
					int day = cCur.get(Calendar.DAY_OF_MONTH);
					if (newMonth) {
						int dayMax = cCur.getActualMaximum(Calendar.DAY_OF_MONTH);
	
						int xm = x - (day - 1) * dm.getWidth();
						int wm = dayMax * dm.getWidth();
						paintMonth(g2, new Rectangle(xm, 0, wm, hh), cCur.getTime());
					}
					paintHour(g2, new Rectangle(x, 0 + hh, dm.getWidth(), hh), day);
				}else{
					paintDay(g2, new Rectangle(x, 0, dm.getWidth(),
							timelineDatesHeader.getHeight()), cCur.getTime());
				}
				cCur.add(Calendar.DAY_OF_MONTH, 1);
				if (oldMonth != cCur.get(Calendar.MONTH)) {
					newMonth = true;
					oldMonth = cCur.get(Calendar.MONTH);
				} else {
					newMonth = false;
				}
				x += dm.getWidth();
			}
		} else {
			int x = (int) (clip.x / dm.getWidth()) * dm.getWidth();
			int days = DateTimeUtils.getNumberOfDays(cMin.getTime(), cMax
					.getTime());
			for (int d = 0; d < days; d++) {
				paintDay(g2, new Rectangle(x, 0, dm.getWidth(),
						timelineDatesHeader.getHeight()), cCur.getTime());
				cCur.add(Calendar.DAY_OF_MONTH, 1);
				x += dm.getWidth();
			}
		}

		rendererPane.removeAll();
	}

	private Component createLabel(String text, boolean border) {
		label.setText(text);
		if (border) {
			label.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		} else {
			label.setBorder(null);
		}
		return label;
	}

	private void paintHour(Graphics g, Rectangle cellRect, int hour) {
		Component label = createLabel(String.valueOf(hour), true);
		rendererPane.paintComponent(g, label, timelineDatesHeader, cellRect.x,
				cellRect.y, cellRect.width, cellRect.height, true);
	}

	private void paintMonth(Graphics g, Rectangle cellRect, Date month) {
		Component label = createLabel(monthDateFormat.format(month), true);
		rendererPane.paintComponent(g, label, timelineDatesHeader, cellRect.x,
				cellRect.y, cellRect.width, cellRect.height, true);
	}

	private void paintDay(Graphics g, Rectangle cellRect, Date day) {
		int w = cellRect.width;
		if ((w >= 50 || getIsDayFormat()) && w < 400) {
			DateFormat dateFormat = (getIsDayFormat())?normalDayFormat:normalDayDateFormat;
			if (w < 75) {
				dateFormat = (getIsDayFormat())?shortDayFormat:shortDayDateFormat;
			} else if (w > 200 && w < 300) {
				dateFormat = (getIsDayFormat())?normalDayFormat:mediumDayDateFormat;
			} else if (w >= 300) {
				dateFormat = (getIsDayFormat())?normalDayFormat:longDayDateFormat;
			}
			Component label = createLabel(dateFormat.format(day), true);
			rendererPane.paintComponent(g, label, timelineDatesHeader,
					cellRect.x, cellRect.y, cellRect.width, cellRect.height,
					true);
		} else if (w >= 400) {
			Component labelDay = createLabel(((getIsDayFormat())?normalDayFormat:longDayDateFormat).format(day), true);
			rendererPane.paintComponent(g, labelDay, timelineDatesHeader,
					cellRect.x, cellRect.y, cellRect.width,
					cellRect.height / 2, true);

			double wh = w / 24.0;
			int hh = cellRect.height / 2;
			int y = cellRect.y + hh;
			for (int hour = 0; hour < 24; hour++) {
				int x = cellRect.x + (int) (wh * hour);
				paintHour(g, new Rectangle(x, y, (int) wh, hh), hour);
			}
		}
	}

	private Dimension createTimelineSize(long height) {
		TimelineDatesModel dm = timelineDatesHeader.getDatesModel();
		int pixels = DateTimeUtils.getNumberOfDays(dm.getDateMin(), dm
				.getDateMax())
				* dm.getWidth();

		return new Dimension(pixels, (int) height);
	}

	public Dimension getMinimumSize(JComponent c) {
		long height = HEIGHT;

		return createTimelineSize(height);
	}

	public Dimension getPreferredSize(JComponent c) {
		long height = HEIGHT;

		return createTimelineSize(height);
	}

	public Dimension getMaximumSize(JComponent c) {
		long height = HEIGHT;
		return createTimelineSize(height);
	}
}
