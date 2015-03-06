package com.synaptix.swing.plaf.basic;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.JTimeline;
import com.synaptix.swing.plaf.TimelineScaleUI;
import com.synaptix.swing.timeline.JTimelineScale;

public class BasicTimelineScaleUI extends TimelineScaleUI {

	private static Cursor resizeCursorLeft = Cursor
			.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);

	private static Cursor resizeCursorRight = Cursor
			.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);

	private static final int HEIGHT = 16;

	protected JTimelineScale timelineScale;

	protected MouseInputListener mouseInputListener;

	public static ComponentUI createUI(JComponent h) {
		return new BasicTimelineScaleUI();
	}

	public void installUI(JComponent c) {
		timelineScale = (JTimelineScale) c;

		installListeners();
		installDefaults();
	}

	protected void installListeners() {
		mouseInputListener = createMouseInputListener();

		timelineScale.addMouseListener(mouseInputListener);
		timelineScale.addMouseMotionListener(mouseInputListener);
	}

	protected void installDefaults() {
		LookAndFeel.installColorsAndFont(timelineScale, "Label.background", //$NON-NLS-1$
				"Label.foreground", "Label.font"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void uninstallUI(JComponent c) {
		uninstallListeners();

		timelineScale = null;
	}

	protected void uninstallListeners() {
		timelineScale.removeMouseListener(mouseInputListener);
		timelineScale.removeMouseMotionListener(mouseInputListener);

		mouseInputListener = null;
	}

	protected MouseInputListener createMouseInputListener() {
		return new MouseInputHandler();
	}

	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setPaint(timelineScale.getBackground());
		g2.fillRect(0, 0, timelineScale.getWidth(), timelineScale.getHeight());

		JTimeline timeline = timelineScale.getTimeline();
		if (timeline != null) {
			double sw = (double) (timelineScale.getWidth() - (timelineScale
					.getInsets().left + timelineScale.getInsets().right))
					/ (double) timeline.getWidth();

			JViewport viewport = (JViewport) timeline.getParent();
			Rectangle rect = viewport.getViewRect();

			int x = (int) (rect.x * sw);
			int width = (int) (rect.width * sw);

			int hh = (timelineScale.getHeight()
					- timelineScale.getInsets().bottom - timelineScale
					.getInsets().top) / 2;

			paintTriangles(g2, x + timelineScale.getInsets().left, x
					+ timelineScale.getInsets().left + width - 1);

			g2.translate(x + timelineScale.getInsets().left, hh - 1);

			int w = width;
			int h = hh * 2 - 3;

			Color colorBackground = UIManager.getColor("Table.selectionBackground"); //$NON-NLS-1$
			Color colorForeground = UIManager.getColor("Table.selectionForeground"); //$NON-NLS-1$

			g2.setPaint(new GradientPaint(0,0,colorBackground,0,h - 1,colorForeground));
			g2.fillRect(0, 0, w - 1, h - 1);
			
			g2.setPaint(colorBackground);
			g2.drawRect(0, 0, w - 1, h - 1);

			g2.translate(-(x + timelineScale.getInsets().left), -(h - 1));
		}
	}

	public void paintTriangles(Graphics2D g2, int x1, int x2) {
		int h = timelineScale.getHeight() - timelineScale.getInsets().bottom
				- 1;
		Polygon p1 = new Polygon(new int[] { x1 - 5, x1, x1 }, new int[] { h,
				h, timelineScale.getInsets().top }, 3);
		Polygon p2 = new Polygon(new int[] { x2 + 5, x2, x2 }, new int[] { h,
				h, timelineScale.getInsets().top }, 3);

		g2.setPaint(Color.BLACK);
		g2.draw(p1);
		g2.draw(p2);
	}

	public Dimension getMinimumSize(JComponent c) {
		int width = timelineScale.getTimeline().getMinimumSize().width;
		return new Dimension(width, HEIGHT);
	}

	public Dimension getPreferredSize(JComponent c) {
		int width = timelineScale.getTimeline().getPreferredSize().width;
		return new Dimension(width, HEIGHT);
	}

	public Dimension getMaximumSize(JComponent c) {
		int width = timelineScale.getTimeline().getMaximumSize().width;
		return new Dimension(width, HEIGHT);
	}

	public class MouseInputHandler implements MouseInputListener {

		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}
}
