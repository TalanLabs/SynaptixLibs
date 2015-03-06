package com.synaptix.swing.plaf.basic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.plaf.RoulementTimelineDayDatesHeaderUI;
import com.synaptix.swing.roulement.JRoulementTimeline;
import com.synaptix.swing.roulement.JRoulementTimelineDayDatesHeader;
import com.synaptix.swing.roulement.RoulementTimelineDayDatesHeaderRenderer;

public class BasicRoulementTimelineDayDatesHeaderUI extends
		RoulementTimelineDayDatesHeaderUI {

	private static final Color dayNowBackgroundColor = new Color(0xE8EEF7);

	private static final Color dayBackgroundColor = new Color(0xBBCCDD);

	private static final Color dayBorderColor = new Color(0xCCDDEE);

	private static final Color nameDayForegroundColor = new Color(0x6A6A6B);

	private JRoulementTimelineDayDatesHeader roulementTimelineDatesHeader;

	private Font hourNameFont = new Font("arial", Font.BOLD, 12); //$NON-NLS-1$

	protected MouseListener mouseInputListener;

	protected MouseListener createMouseInputListener() {
		return new MouseInputHandler();
	}

	public static ComponentUI createUI(JComponent h) {
		return new BasicRoulementTimelineDayDatesHeaderUI();
	}

	public void installUI(JComponent c) {
		roulementTimelineDatesHeader = (JRoulementTimelineDayDatesHeader) c;

		installDefaults();
		installListeners();
	}

	protected void installDefaults() {
	}

	protected void installListeners() {
		mouseInputListener = createMouseInputListener();

		roulementTimelineDatesHeader.addMouseListener(mouseInputListener);
	}

	public void uninstallUI(JComponent c) {
		uninstallDefaults();
		uninstallListeners();

		roulementTimelineDatesHeader = null;
	}

	protected void uninstallDefaults() {
	}

	protected void uninstallListeners() {
		roulementTimelineDatesHeader.removeMouseListener(mouseInputListener);

		mouseInputListener = null;
	}

	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;

		Rectangle clip = g2.getClipBounds();
		Point left = clip.getLocation();
		Point right = new Point(clip.x + clip.width - 1, clip.y + clip.height
				- 1);

		JRoulementTimeline timeline = roulementTimelineDatesHeader
				.getRoulementTimeline();

		int width = roulementTimelineDatesHeader.getWidth();
		int height = roulementTimelineDatesHeader.getHeight();

		JRoulementTimeline roulementTimeline = roulementTimelineDatesHeader
				.getRoulementTimeline();
		if (roulementTimeline != null) {
			int nb = roulementTimeline.getNbDays();

			int dayWidth = roulementTimelineDatesHeader.getDayWidth();

			DayDate dayDate1 = timeline.dayDateAtPoint(left);
			DayDate dayDate2 = timeline.dayDateAtPoint(right);

			DayDate calendar = new DayDate(dayDate1.getDay());

			for (int i = dayDate1.getDay(); i <= dayDate2.getDay(); i++) {
				int x = i * dayWidth;

				boolean isCurrent = i % 2 == 0;

				Rectangle rect = new Rectangle(x, 0, dayWidth, height);
				paintDay(g2, rect, calendar, isCurrent, roulementTimeline);

				calendar.addDay(1);
			}

			int x = nb * dayWidth;
			if (x < width) {
				g2
						.setColor(roulementTimeline
								.getDateHeaderBackgroundImpairColor() != null ? roulementTimeline
								.getDateHeaderBackgroundImpairColor()
								: dayBackgroundColor);
				g2.fillRect(x, 0, width - x, height - 1);
				g2.setColor(dayBorderColor);
				g2.drawRect(x, 0, width - x - 1, height - 1);
			}
		}
	}

	private void paintDay(Graphics g, Rectangle rect, DayDate day,
			boolean isCurrent, JRoulementTimeline roulementTimeline) {
		Graphics2D g2 = (Graphics2D) g;

		if (isCurrent) {
			g2
					.setColor(roulementTimeline
							.getDateHeaderBackgroundPairColor() != null ? roulementTimeline
							.getDateHeaderBackgroundPairColor()
							: dayNowBackgroundColor);
		} else {
			g2
					.setColor(roulementTimeline
							.getDateHeaderBackgroundImpairColor() != null ? roulementTimeline
							.getDateHeaderBackgroundImpairColor()
							: dayBackgroundColor);
		}
		g2.fillRect(rect.x, rect.y, rect.width, rect.height - 1);

		FontMetrics fm2 = g2.getFontMetrics(hourNameFont);

		RoulementTimelineDayDatesHeaderRenderer renderer = roulementTimelineDatesHeader
				.getHeaderRenderer();
		if (renderer != null) {
			Rectangle rect2 = new Rectangle(rect.x, rect.y, rect.width,
					rect.height - (fm2.getHeight()));
			Rectangle clipBounds = g2.getClipBounds();
			g2.clipRect(rect2.x, rect2.y, rect2.width, rect2.height);
			renderer.paintHeader(g2, roulementTimelineDatesHeader, day, rect2);
			g2.setClip(clipBounds.x, clipBounds.y, clipBounds.width,
					clipBounds.height);
		}

		g2.setColor(dayBorderColor);
		g2.drawRect(rect.x, rect.y, rect.width, rect.height - 1);

		if (roulementTimelineDatesHeader.getDayWidth() >= 400) {
			if (isCurrent) {
				g2
						.setColor(roulementTimeline
								.getDateHeaderForegroundPairColor() != null ? roulementTimeline
								.getDateHeaderForegroundPairColor()
								: nameDayForegroundColor);
			} else {
				g2
						.setColor(roulementTimeline
								.getDateHeaderForegroundImpairColor() != null ? roulementTimeline
								.getDateHeaderForegroundImpairColor()
								: nameDayForegroundColor);
			}
			g2.setFont(hourNameFont);

			double width3 = (double) rect.width / 24.0;
			for (int i = 0; i < 24; i++) {
				int x = (int) ((double) i * width3 + (double) rect.x);
				String text = String.valueOf(i);
				int l = fm2.stringWidth(text);
				g2.drawString(String.valueOf(i), x - l / 2, rect.y
						+ rect.height - fm2.getDescent());
			}
		}
	}

	private Dimension createSize(long height) {
		int dayWidth = roulementTimelineDatesHeader.getDayWidth();

		int pixels = dayWidth;

		JRoulementTimeline roulementTimeline = roulementTimelineDatesHeader
				.getRoulementTimeline();
		if (roulementTimeline != null) {
			int nb = roulementTimeline.getNbDays();
			pixels = dayWidth * nb;
		}

		return new Dimension(pixels, (int) height);
	}

	public Dimension getMinimumSize(JComponent c) {
		long height = JRoulementTimelineDayDatesHeader.DEFAULT_DAY_HEIGHT;
		return createSize(height);
	}

	public Dimension getPreferredSize(JComponent c) {
		long height = JRoulementTimelineDayDatesHeader.DEFAULT_DAY_HEIGHT;
		return createSize(height);
	}

	public Dimension getMaximumSize(JComponent c) {
		long height = JRoulementTimelineDayDatesHeader.DEFAULT_DAY_HEIGHT;
		return createSize(height);
	}

	private final class MouseInputHandler extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			JRoulementTimeline roulementTimeline = roulementTimelineDatesHeader
					.getRoulementTimeline();
			roulementTimeline.requestFocus();
		}
	}
}
