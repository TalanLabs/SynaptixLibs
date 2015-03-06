package com.synaptix.swing.roulement;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class DefaultRoulementTimelineResourceRenderer implements
		RoulementTimelineResourceRenderer {

	private static final long serialVersionUID = -856984961470657433L;

	private static final Color resourceSelectionBackgroundColor = new Color(
			0x498CFF);

	private static final Color resourceBackgroundColor = new Color(0xC3D9FF);

	private static final Color resourceBorderColor = new Color(0xEEEEEE);

	private static final Color resourceSelectionForegroundColor = new Color(
			0xFFFFFF);

	private static final Color resourceForegroundColor = new Color(0x6A6A6B);

	private static final Font resourceFont = new Font("arial", Font.BOLD, 15); //$NON-NLS-1$

	private MyLeftComponent leftComponent;

	private MyRightComponent rightComponent;

	public DefaultRoulementTimelineResourceRenderer() {
		super();

		leftComponent = new MyLeftComponent();
		leftComponent.setPreferredSize(new Dimension(100, 100));

		rightComponent = new MyRightComponent();
		rightComponent.setPreferredSize(new Dimension(200, 100));
	}

	public Component getRoulementTimelineLeftResourceRendererComponent(
			JRoulementTimeline roulementTimeline,
			RoulementTimelineResource resource, int index, boolean isSelected,
			boolean hasFocus, boolean isDrag) {
		leftComponent.isDrag = isDrag;
		leftComponent.isSelected = isSelected;

		leftComponent.setValue(resource.getHeaderValue());

		return leftComponent;
	}

	public Component getRoulementTimelineRightResourceRendererComponent(
			JRoulementTimeline roulementTimeline,
			RoulementTimelineResource resource, int index, boolean isSelected,
			boolean hasFocus, boolean isDrag) {
		rightComponent.isDrag = isDrag;
		rightComponent.isSelected = isSelected;

		rightComponent.setValue(resource.getHeaderValue());

		return rightComponent;
	}

	private final class MyLeftComponent extends JComponent {

		private static final long serialVersionUID = 8697538737554544928L;

		private String text;

		private boolean isDrag;

		private boolean isSelected;

		public MyLeftComponent() {
			super();
			this.setOpaque(true);
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

	private final class MyRightComponent extends JComponent {

		private static final long serialVersionUID = 8697538737554544928L;

		private String text;

		private boolean isDrag;

		private boolean isSelected;

		public MyRightComponent() {
			super();
			this.setOpaque(true);
		}

		protected void setValue(Object value) {
			text = (value == null) ? "" : "A droite de " + value.toString(); //$NON-NLS-1$
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
}
