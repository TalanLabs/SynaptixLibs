package com.synaptix.swing.simpledaystimeline;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import com.synaptix.swing.JSimpleDaysTimeline;

public class DefaultSimpleDaysTimelineResourceRenderer extends JComponent
		implements SimpleDaysTimelineResourceRenderer {

	private static final long serialVersionUID = -856984961470657433L;

	private static final Color resourceSelectionBackgroundColor = new Color(
			0x498CFF);

	private static final Color resourceBackgroundColor = new Color(0xC3D9FF);

	private static final Color resourceBorderColor = new Color(0xEEEEEE);

	private static final Color resourceSelectionForegroundColor = new Color(
			0xFFFFFF);

	private static final Color resourceForegroundColor = new Color(0x6A6A6B);

	private static final Font resourceFont = new Font("arial", Font.BOLD, 15); //$NON-NLS-1$

	private String text;

	private boolean isDrag;

	private boolean isSelected;

	public DefaultSimpleDaysTimelineResourceRenderer() {
		super();
		this.setOpaque(true);
	}

	public Component getSimpleDaysTimelineResourceRendererComponent(
			JSimpleDaysTimeline simpleDaysTimeline,
			SimpleDaysTimelineResource resource, int index, boolean isSelected,
			boolean hasFocus, boolean isDrag) {
		this.isDrag = isDrag;
		this.isSelected = isSelected;

		setValue(resource.getHeaderValue());

		return this;
	}

	protected void setValue(Object value) {
		text = (value == null) ? "" : value.toString(); //$NON-NLS-1$
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();

		FontMetrics fm = g2.getFontMetrics(resourceFont);

		g2.setFont(resourceFont);

		int w = this.getWidth();
		int h = this.getHeight();
		if (isSelected) {
			g2.setColor(resourceSelectionBackgroundColor);
		} else {
			g2.setColor(resourceBackgroundColor);
		}
		g2.fillRect(0, 0, w, h);

		g2.setColor(resourceBorderColor);
		if (isDrag) {
			g2.drawRect(0, 0, w - 1, h - 1);
		} else {
			g2.drawRect(0, 0, w - 1, h);
		}

		int l = fm.stringWidth(text);
		if (isSelected) {
			g2.setColor(resourceSelectionForegroundColor);
		} else {
			g2.setColor(resourceForegroundColor);
		}
		g2.drawString(text, (w - l) - 2, h / 2 + fm.getDescent());

		g2.dispose();
	}
}
