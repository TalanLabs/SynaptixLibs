package com.synaptix.swing.plaf.basic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.JSimpleTimeline;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.plaf.SimpleTimelineDatesHeaderUI;
import com.synaptix.swing.simpletimeline.JSimpleTimelineDatesHeader;
import com.synaptix.swing.utils.DateTimeUtils;

public class BasicSimpleTimelineDatesHeaderUI extends
		SimpleTimelineDatesHeaderUI {

	private static final DateFormat longDayDateFormat = new SimpleDateFormat(
			"EEEE dd MMMMMM yyyy"); //$NON-NLS-1$

	private static final DateFormat shortDayDateFormat = new SimpleDateFormat(
			"dd/MM/yyyy"); //$NON-NLS-1$

	private static final Color dayNowBackgroundColor = new Color(0xE8EEF7);

	private static final Color dayBackgroundColor = new Color(0xBBCCDD);

	private static final Color dayBorderColor = new Color(0xCCDDEE);

	private static final Color nameDayForegroundColor = new Color(0x6A6A6B);

	private JSimpleTimelineDatesHeader simpleTimelineDatesHeader;

	private Font dayNameFont = new Font("arial", Font.BOLD, 14); //$NON-NLS-1$

	private Font hourNameFont = new Font("arial", Font.BOLD, 12); //$NON-NLS-1$

	private Font shortDayNameFont = new Font("arial", Font.BOLD, 12); //$NON-NLS-1$

	public static ComponentUI createUI(JComponent h) {
		return new BasicSimpleTimelineDatesHeaderUI();
	}

	public void installUI(JComponent c) {
		simpleTimelineDatesHeader = (JSimpleTimelineDatesHeader) c;
	}

	public void uninstallUI(JComponent c) {
		simpleTimelineDatesHeader = null;
	}

	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;

		int width = simpleTimelineDatesHeader.getWidth();
		int height = simpleTimelineDatesHeader.getHeight();

		JSimpleTimeline simpleTimeline = simpleTimelineDatesHeader
				.getSimpleTimeline();
		if (simpleTimeline != null) {
			int nb = simpleTimeline.getNbBeforeDays()
					+ simpleTimeline.getNbAfterDays() + 1;

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(simpleTimeline.getCurrentDate());
			calendar.add(Calendar.DAY_OF_YEAR, -simpleTimeline
					.getNbBeforeDays());

			int dayWidth = simpleTimelineDatesHeader.getDayWidth();

			for (int i = 0; i < nb; i++) {
				int x = i * dayWidth;

				boolean isCurrent = DateTimeUtils.sameDay(simpleTimeline
						.getCurrentDate(), calendar.getTime());

				Rectangle rect = new Rectangle(x, 0, dayWidth, height);
				paintDay(g2, rect, calendar.getTime(), isCurrent);

				calendar.add(Calendar.DAY_OF_YEAR, 1);
			}

			int x = nb * dayWidth;
			if (x < width) {
				g2.setColor(dayBackgroundColor);
				g2.fillRect(x, 0, width - x, height - 1);
				g2.setColor(dayBorderColor);
				g2.drawRect(x, 0, width - x - 1, height - 1);
			}
		}
	}

	private void paintDay(Graphics g, Rectangle rect, Date day,
			boolean isCurrent) {
		Graphics2D g2 = (Graphics2D) g;

		if (isCurrent) {
			g2.setColor(dayNowBackgroundColor);
		} else {
			g2.setColor(dayBackgroundColor);
		}
		g2.fillRect(rect.x, rect.y, rect.width, rect.height - 1);

		FontMetrics fm1;

		String text;
		if (simpleTimelineDatesHeader.getDayWidth() >= 250) {
			text = longDayDateFormat.format(day);
			text = text.substring(0, 1).toUpperCase() + text.substring(1);

			g2.setFont(dayNameFont);
			fm1 = g2.getFontMetrics(dayNameFont);
		} else {
			text = shortDayDateFormat.format(day);

			g2.setFont(shortDayNameFont);
			fm1 = g2.getFontMetrics(shortDayNameFont);
		}

		int l = fm1.stringWidth(text);

		g2.setColor(nameDayForegroundColor);
		g2.drawString(text, rect.x + 2, rect.y + fm1.getAscent() + 2);

		if (DateTimeUtils.isFete(day)) {
			int sw = fm1.stringWidth(SwingMessages
					.getString("BasicSimpleTimelineDatesHeaderUI.5")); //$NON-NLS-1$
			g2
					.drawString(
							SwingMessages
									.getString("BasicSimpleTimelineDatesHeaderUI.5"), rect.x + rect.width - (sw + 2), rect.y //$NON-NLS-1$
									+ fm1.getAscent() + 2);
		}

		g2.setColor(dayBorderColor);
		g2.drawRect(rect.x, rect.y, rect.width, rect.height - 1);
		
		if (simpleTimelineDatesHeader.getDayWidth() >= 400) {
			g2.setColor(nameDayForegroundColor);
			g2.setFont(hourNameFont);
			FontMetrics fm2 = g2.getFontMetrics(hourNameFont);

			double width3 = (double) rect.width / 24.0;
			for (int i = 0; i < 24; i++) {
				int x = (int) ((double) i * width3 + (double) rect.x);
				text = String.valueOf(i);
				l = fm2.stringWidth(text);
				g2.drawString(String.valueOf(i), x - l / 2, rect.y
						+ rect.height - fm2.getDescent());
			}
		}
	}

	private Dimension createSize(long height) {
		int dayWidth = simpleTimelineDatesHeader.getDayWidth();

		int pixels = dayWidth;

		JSimpleTimeline simpleTimeline = simpleTimelineDatesHeader
				.getSimpleTimeline();
		if (simpleTimeline != null) {
			int nb = simpleTimeline.getNbBeforeDays()
					+ simpleTimeline.getNbAfterDays() + 1;
			pixels = dayWidth * nb;
		}

		return new Dimension(pixels, (int) height);
	}

	public Dimension getMinimumSize(JComponent c) {
		long height = JSimpleTimelineDatesHeader.DEFAULT_DAY_HEIGHT;
		return createSize(height);
	}

	public Dimension getPreferredSize(JComponent c) {
		long height = JSimpleTimelineDatesHeader.DEFAULT_DAY_HEIGHT;
		return createSize(height);
	}

	public Dimension getMaximumSize(JComponent c) {
		long height = JSimpleTimelineDatesHeader.DEFAULT_DAY_HEIGHT;
		return createSize(height);
	}
}
