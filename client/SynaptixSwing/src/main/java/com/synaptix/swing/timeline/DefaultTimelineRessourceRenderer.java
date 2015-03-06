package com.synaptix.swing.timeline;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.synaptix.swing.JTimeline;

public class DefaultTimelineRessourceRenderer extends JLabel implements
		TimelineRessourceRenderer {

	private static final long serialVersionUID = -856984961470657433L;

	private Color background;

	private Color foreground;

	public DefaultTimelineRessourceRenderer() {
		super();
		this.setOpaque(false);
		this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

		background = this.getBackground();
		foreground = this.getForeground();
	}

	public Component getTimelineRessourceRendererComponent(JTimeline timeline,
			TimelineRessource ressource, int index, boolean isSelected,
			boolean hasFocus) {

		// if (isSelected) {
		// this.setForeground(timeline.getSelectionForeground());
		// this.setBackground(timeline.getSelectionBackground());
		// } else {
		this.setForeground(foreground);
		this.setBackground(background);
		// }

		setValue(ressource.getHeaderValue());

		return this;
	}

	protected void setValue(Object value) {
		setText((value == null) ? "" : value.toString()); //$NON-NLS-1$
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		int w = this.getWidth();
		int h = this.getHeight();

		Color c1 = this.getBackground();
		Color c2 = c1.brighter().brighter();

		GradientPaint gp = new GradientPaint(0, 0, c2, w, 0, c1);
		g2.setPaint(gp);
		g2.fillRect(0, 0, w, h);

		super.paintComponent(g);
	}
}
