package com.synaptix.swing.simpledaystimeline;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.JViewport;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.utils.Utils;

public class DefaultSimpleDaysTimelineDayDatesHeaderRenderer implements
		SimpleDaysTimelineDayDatesHeaderRenderer {

	public enum Position {
		Left, Center, Right
	};

	protected static final Color nameDayForegroundColor = new Color(0x6A6A6B);

	protected static final Font dayNameFont = new Font("arial", Font.BOLD, 14); //$NON-NLS-1$

	protected static final Font shortDayNameFont = new Font("arial", Font.BOLD, //$NON-NLS-1$
			12);

	protected Position position;

	public DefaultSimpleDaysTimelineDayDatesHeaderRenderer() {
		this(Position.Left);
	}

	public DefaultSimpleDaysTimelineDayDatesHeaderRenderer(Position position) {
		this.position = position;
	}

	protected String toDayDateString(DayDate dayDate, int zoom) {
		return dayDate.toString();
	}

	public String getToolTipText(DayDate dayDate) {
		return null;
	}

	public void paintHeader(Graphics2D g,
			JSimpleDaysTimelineDayDatesHeader header, DayDate dayDate,
			Rectangle rect) {
		JSimpleDaysTimeline simpleDaysTimeline = header.getSimpleDaysTimeline();

		Graphics2D g2 = (Graphics2D) g.create();

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		FontMetrics fm1 = null;

		String text = ""; //$NON-NLS-1$
		if (header.getDayWidth() >= 250) {
			text = toDayDateString(dayDate, header.getDayWidth());

			g2.setFont(dayNameFont);
			fm1 = g2.getFontMetrics(dayNameFont);
		} else {
			text = toDayDateString(dayDate, header.getDayWidth());

			g2.setFont(shortDayNameFont);
			fm1 = g2.getFontMetrics(shortDayNameFont);
		}

		if (dayDate.getDay() % 2 == 0) {
			g2
					.setColor(simpleDaysTimeline
							.getDateHeaderForegroundPairColor() != null ? simpleDaysTimeline
							.getDateHeaderForegroundPairColor()
							: nameDayForegroundColor);
		} else {
			g2
					.setColor(simpleDaysTimeline
							.getDateHeaderForegroundImpairColor() != null ? simpleDaysTimeline
							.getDateHeaderForegroundImpairColor()
							: nameDayForegroundColor);
		}

		Rectangle2D rect2 = fm1.getStringBounds(text, g2);
		int x = rect.x;
		switch (position) {
		case Left:
			x += 2;
			break;
		case Center:
			x += (rect.width - rect2.getWidth()) / 2;

			break;
		case Right:
			x += rect.width - rect2.getWidth();
			break;
		}

		if (header.getSimpleDaysTimeline() != null
				&& header.getSimpleDaysTimeline().isDynamicDayDatesHeader()) {
			JViewport viewport = (JViewport) header.getParent();
			Rectangle viewRect = viewport.getViewRect();

			if (x + rect2.getWidth() > viewRect.x + viewRect.width) {
				x = (int) (viewRect.x + viewRect.width - rect2.getWidth());
			}
			if (x < viewRect.x) {
				x = viewRect.x;
			}
			if (x < rect.x) {
				x = rect.x;
			}
		}
		text = Utils.getClippedText(text, g2.getFontMetrics(), rect.width
				- (x - rect.x));

		g2.drawString(text, x, rect.y + fm1.getAscent() + 2);

		g2.dispose();
	}

}
