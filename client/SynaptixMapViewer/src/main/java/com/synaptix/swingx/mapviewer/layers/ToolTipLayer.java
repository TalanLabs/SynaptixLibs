package com.synaptix.swingx.mapviewer.layers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.AVKey;
import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.SelectEvent;
import org.jdesktop.swingx.mapviewer.SelectListener;

import com.synaptix.swingx.mapviewer.layers.render.ScreenAnnotation;

public class ToolTipLayer extends RenderableLayer implements SelectListener {

	private JXMapViewer mapViewer;

	private Object lastRolloverObject = null;

	private MyAnnotation annonation = null;

	public ToolTipLayer(JXMapViewer mapViewer) {
		super();

		this.mapViewer = mapViewer;

		this.setPickEnabled(false);
		this.setVisible(false);

		mapViewer.addSelectListener(this);
	}

	public void selected(SelectEvent event) {
		if (event.isRollover()) {
			handleRollover(event);
		}
	}

	protected String getRolloverText(SelectEvent event) {
		return event.getTopObject() != null
				&& event.getTopObject() instanceof ToolTipable ? ((ToolTipable) event
				.getTopObject()).getToolTip() : null;
	}

	protected void handleRollover(SelectEvent event) {
		boolean repaint = false;
		String text = getRolloverText(event);
		if (this.lastRolloverObject != null) {
			if (this.lastRolloverObject == event.getTopObject() && text != null
					&& !text.trim().isEmpty()) {
				return;
			}

			this.hideToolTip();
			this.lastRolloverObject = null;
			repaint = true;
		}

		if (getRolloverText(event) != null) {
			this.lastRolloverObject = event.getTopObject();
			this.showToolTip(event, getRolloverText(event).replace("\\n", "\n"));
			repaint = true;
		}
		if (repaint) {
			mapViewer.repaint();
		}
	}

	protected void hideToolTip() {
		this.setVisible(false);
		this.removeAllRenderables();
		this.annonation = null;
	}

	protected void showToolTip(SelectEvent event, String text) {
		this.annonation = new MyAnnotation(text);
		annonation.setScreenPoint(event.getPickPoint());
		this.addRenderable(annonation);
		this.setVisible(true);
	}

	private static final class MyAnnotation extends ScreenAnnotation {

		private Point tooltipOffset = new Point(5, 5);

		public MyAnnotation(String text) {
			super(text, new Point(0, 0));

			this.getAnnotationAttributes().setCornerRadius(10);
			this.getAnnotationAttributes().setBackgroundColor(
					new Color(1f, 1f, 1f, 0.8f));
			this.getAnnotationAttributes().setBorderColor(new Color(0xababab));
			this.getAnnotationAttributes().setInsets(new Insets(5, 5, 5, 5));
			this.getAnnotationAttributes()
					.setFont(Font.decode("Arial-BOLD-12"));
			this.getAnnotationAttributes().setTextColor(Color.black);
			this.getAnnotationAttributes().setTextAlign(AVKey.ALIGN_CENTER);
		}

		public Point getTooltipOffset() {
			return tooltipOffset;
		}

		public void setTooltipOffset(Point tooltipOffset) {
			this.tooltipOffset = tooltipOffset;
		}

		protected int getOffsetX() {
			return this.tooltipOffset != null ? this.tooltipOffset.x : 0;
		}

		protected int getOffsetY() {
			return this.tooltipOffset != null ? this.tooltipOffset.y : 0;
		}

		@Override
		public boolean isPick(DrawContext dc, Point point) {
			if (dc.getPickPoint() == null) {
				return false;
			}
			setScreenPoint(adjustDrawPointToViewport(dc.getPickPoint(),
					getBounds(dc), dc.getViewportBounds()));
			return super.isPick(dc, point);
		}

		@Override
		public void render(Graphics2D g, DrawContext dc) {
			if (dc.getPickPoint() == null) {
				return;
			}
			Dimension size = this.getPreferredSize(dc);
			this.getAnnotationAttributes().setDrawOffset(
					new Point(size.width / 2 + this.getOffsetX(), size.height
							/ 2 + this.getOffsetY()));
			this.setScreenPoint(adjustDrawPointToViewport(dc.getPickPoint(),
					getBounds(dc), dc.getViewportBounds()));
			super.render(g, dc);
		}

		protected Point adjustDrawPointToViewport(Point point,
				Rectangle bounds, Rectangle viewport) {
			int x = point.x + this.getOffsetX();
			int y = point.y - bounds.height - this.getOffsetY();

			if (x + this.getOffsetX() + bounds.width > viewport.getWidth()) {
				x = (int) (viewport.getWidth() - bounds.width) - 1;
			} else if (x < 0) {
				x = 0;
			}

			if (y + this.getOffsetY() + bounds.height > viewport.getHeight()) {
				y = (int) (viewport.getHeight() - bounds.height) - 1;
			} else if (y < 0) {
				y = 0;
			}
			return new Point(x, y);
		}
	}
}
