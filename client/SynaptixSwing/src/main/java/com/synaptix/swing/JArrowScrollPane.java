package com.synaptix.swing;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Arc2D;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.synaptix.swing.utils.Utils;

public class JArrowScrollPane extends JLayeredPane {

	private static final long serialVersionUID = -37336182880146488L;

	private static final int CONTENT_LAYER = 1;

	private static final int FEEDBACK_LAYER = 2;

	public static final Color defaultMouseOutDarkColor = new Color(128, 128,
			128, 100);

	public static final Color defaultMouseOutLightColor = new Color(0, 0, 0, 0);

	public static final Color defaultMouseInDarkColor = new Color(50, 64, 255,
			128);

	public static final Color defaultMouseInLightColor = new Color(0, 0, 0, 0);

	public static final Color defaultArrowFillColor = new Color(255, 255, 255,
			127);

	public static final Color defaultArrowSelectedFillColor = new Color(255,
			255, 255, 240);

	public static final Color defaultArrowStrockColor = new Color(127, 127,
			127, 196);

	public static final Color defaultArrowSelectedStrockColor = new Color(255,
			180, 7, 240);

	public static final Color defaultBarColor = new Color(0, 0, 0, 128);

	private int maxSizeX = 50;

	private int maxSizeY = 50;

	private int vitesseMoveX = 50;

	private int vitesseMoveY = 50;

	private int barSize = 5;

	private Color mouseOutDarkColor = defaultMouseOutDarkColor;

	private Color mouseOutLightColor = defaultMouseOutLightColor;

	private Color mouseInDarkColor = defaultMouseInDarkColor;

	private Color mouseInLightColor = defaultMouseInLightColor;

	private Color arrowFillColor = defaultArrowFillColor;

	private Color arrowSelectedFillColor = defaultArrowSelectedFillColor;

	private Color arrowStrockColor = defaultArrowStrockColor;

	private Color arrowSelectedStrockColor = defaultArrowSelectedStrockColor;

	private Color barColor = defaultBarColor;

	private Component view;

	private JScrollPane scrollPane;

	private MyFeedback feedback;

	private Timer timer;

	public JArrowScrollPane(Component view) {
		super();

		this.setLayout(new SimpleLayout());
		this.view = view;

		timer = new Timer(75, new MyActionListener());

		initComponents();

		add(scrollPane, CONTENT_LAYER);
		add(feedback, new Integer(FEEDBACK_LAYER));
	}

	private void createComponents() {
		scrollPane = new JScrollPane(view);
		feedback = new MyFeedback();
	}

	private void initComponents() {
		createComponents();

		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		MyAdjustmentListener adjustmentListener = new MyAdjustmentListener();
		scrollPane.getHorizontalScrollBar().addAdjustmentListener(
				adjustmentListener);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(
				adjustmentListener);

		MyMouseAdapter mouseAdapter = new MyMouseAdapter();
		scrollPane.addMouseMotionListener(mouseAdapter);
		scrollPane.addMouseWheelListener(mouseAdapter);
		scrollPane.addMouseListener(mouseAdapter);
		Toolkit.getDefaultToolkit().addAWTEventListener(
				new MyAWTEventListener(),
				AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK
						| AWTEvent.MOUSE_WHEEL_EVENT_MASK);

		verifyScrollBars();
	}
	
	public JScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public Component getView() {
		return view;
	}

	public Color getMouseOutDarkColor() {
		return mouseOutDarkColor;
	}

	public void setMouseOutDarkColor(Color mouseOutDarkColor) {
		this.mouseOutDarkColor = mouseOutDarkColor;
		feedback.repaint();
	}

	public Color getMouseOutLightColor() {
		return mouseOutLightColor;
	}

	public void setMouseOutLightColor(Color mouseOutLightColor) {
		this.mouseOutLightColor = mouseOutLightColor;
		feedback.repaint();
	}

	public Color getMouseInDarkColor() {
		return mouseInDarkColor;
	}

	public void setMouseInDarkColor(Color mouseInDarkColor) {
		this.mouseInDarkColor = mouseInDarkColor;
		feedback.repaint();
	}

	public Color getMouseInLightColor() {
		return mouseInLightColor;
	}

	public void setMouseInLightColor(Color mouseInLightColor) {
		this.mouseInLightColor = mouseInLightColor;
		feedback.repaint();
	}

	public Color getArrowFillColor() {
		return arrowFillColor;
	}

	public void setArrowFillColor(Color arrowFillColor) {
		this.arrowFillColor = arrowFillColor;
		feedback.repaint();
	}

	public Color getArrowSelectedFillColor() {
		return arrowSelectedFillColor;
	}

	public void setArrowSelectedFillColor(Color arrowSelectedFillColor) {
		this.arrowSelectedFillColor = arrowSelectedFillColor;
		feedback.repaint();
	}

	public Color getArrowStrockColor() {
		return arrowStrockColor;
	}

	public void setArrowStrockColor(Color arrowStrockColor) {
		this.arrowStrockColor = arrowStrockColor;
		feedback.repaint();
	}

	public Color getArrowSelectedStrockColor() {
		return arrowSelectedStrockColor;
	}

	public void setArrowSelectedStrockColor(Color arrowSelectedStrockColor) {
		this.arrowSelectedStrockColor = arrowSelectedStrockColor;
		feedback.repaint();
	}

	public int getMaxSizeX() {
		return maxSizeX;
	}

	public void setMaxSizeX(int maxSizeX) {
		this.maxSizeX = maxSizeX;
		feedback.repaint();
	}

	public int getMaxSizeY() {
		return maxSizeY;
	}

	public void setMaxSizeY(int maxSizeY) {
		this.maxSizeY = maxSizeY;
		feedback.repaint();
	}

	public int getVitesseMoveX() {
		return vitesseMoveX;
	}

	public void setVitesseMoveX(int vitesseMoveX) {
		this.vitesseMoveX = vitesseMoveX;
		feedback.repaint();
	}

	public int getVitesseMoveY() {
		return vitesseMoveY;
	}

	public void setVitesseMoveY(int vitesseMoveY) {
		this.vitesseMoveY = vitesseMoveY;
		feedback.repaint();
	}

	public int getBarSize() {
		return barSize;
	}

	public void setBarSize(int barSize) {
		this.barSize = barSize;
		feedback.repaint();
	}

	public Color getBarColor() {
		return barColor;
	}

	public void setBarColor(Color barColor) {
		this.barColor = barColor;
		feedback.repaint();
	}

	private void verifyScrollBars() {
		verifyHorizontalScrollBar();
		verifyVerticalScrollBar();

		verifyTimer();
	}

	private void verifyVerticalScrollBar() {
		JScrollBar bar = scrollPane.getVerticalScrollBar();

		boolean showHaut = bar.getValue() > 0
				&& bar.getMaximum() > bar.getVisibleAmount();
		boolean showBas = bar.getValue() + bar.getVisibleAmount() < bar
				.getMaximum()
				&& bar.getMaximum() > bar.getVisibleAmount();

		if (showHaut && !feedback.showHaut) {
			feedback.setShowHaut(true);
			feedback.setMouseInHaut(false);
		} else if (!showHaut && feedback.showHaut) {
			feedback.setShowHaut(false);
			feedback.setMouseInHaut(false);
		}
		if (showBas && !feedback.showBas) {
			feedback.setShowBas(true);
			feedback.setMouseInBas(false);
		} else if (!showBas && feedback.showBas) {
			feedback.setShowBas(false);
			feedback.setMouseInBas(false);
		}
	}

	private void verifyHorizontalScrollBar() {
		JScrollBar bar = scrollPane.getHorizontalScrollBar();

		boolean showGauche = bar.getValue() > 0
				&& bar.getMaximum() > bar.getVisibleAmount();
		boolean showDroite = bar.getValue() + bar.getVisibleAmount() < bar
				.getMaximum()
				&& bar.getMaximum() > bar.getVisibleAmount();

		if (showGauche && !feedback.showGauche) {
			feedback.setShowGauche(true);
			feedback.setMouseInGauche(false);
		} else if (!showGauche && feedback.showGauche) {
			feedback.setShowGauche(false);
			feedback.setMouseInGauche(false);
		}
		if (showDroite && !feedback.showDroite) {
			feedback.setShowDroite(true);
			feedback.setMouseInDroite(false);
		} else if (!showDroite && feedback.showDroite) {
			feedback.setShowDroite(false);
			feedback.setMouseInDroite(false);
		}
	}

	private void verifyTimer() {
		if ((feedback.showHaut && feedback.mouseInHaut)
				|| (feedback.showBas && feedback.mouseInBas)
				|| (feedback.showGauche && feedback.mouseInGauche)
				|| (feedback.showDroite && feedback.mouseInDroite)) {
			if (!timer.isRunning()) {
				timer.start();
			}
		} else if (timer.isRunning()) {
			timer.stop();
		}
	}

	private final int getScrollBarWidth() {
		Rectangle bounds = scrollPane.getViewport().getBounds();
		return bounds.width < maxSizeX ? bounds.width / 4 : maxSizeX;
	}

	private final int getScrollBarHeight() {
		Rectangle bounds = scrollPane.getViewport().getBounds();
		return bounds.height < maxSizeY ? bounds.height / 4 : maxSizeY;
	}

	private void mouseMovedInScrollPane(Point point) {
		Rectangle bounds = scrollPane.getViewport().getBounds();

		int tw = getScrollBarWidth();
		int th = getScrollBarHeight();

		Point p = new Point(point.x - bounds.x, point.y - bounds.y);

		boolean dansHaut = p.y >= 0 && p.y < th;
		boolean dansBas = p.y >= scrollPane.getHeight() - th
				&& p.y < scrollPane.getHeight();
		boolean dansGauche = p.x >= 0 && p.x < tw;
		boolean dansDroite = p.x >= scrollPane.getWidth() - tw
				&& p.x < scrollPane.getWidth();

		if (feedback.showHaut) {
			feedback.setMouseInHaut(dansHaut);
			feedback.distanceHaut = p.y;
		}
		if (feedback.showBas) {
			feedback.setMouseInBas(dansBas);
			feedback.distanceBas = scrollPane.getHeight() - p.y;
		}
		if (feedback.showGauche) {
			feedback.setMouseInGauche(dansGauche);
			feedback.distanceGauche = p.x;
		}
		if (feedback.showDroite) {
			feedback.setMouseInDroite(dansDroite);
			feedback.distanceDroite = scrollPane.getWidth() - p.x;
		}
		verifyTimer();
	}

	private void mouseExitedInScrollPane() {
		mouseMovedInScrollPane(new Point(-1, -1));
	}

	// Code pris dans BasicScrollPaneUI
	private void mouseWheel(int scrollType, int wheelRotation, int unitsToScroll) {
		if (scrollPane.isWheelScrollingEnabled() && wheelRotation != 0) {
			JScrollBar toScroll = scrollPane.getVerticalScrollBar();
			int direction = wheelRotation < 0 ? -1 : 1;
			int orientation = SwingConstants.VERTICAL;

			// find which scrollbar to scroll, or return if none
			if (toScroll == null || (!feedback.showHaut && !feedback.showBas)) {
				toScroll = scrollPane.getHorizontalScrollBar();
				if (toScroll == null
						|| (!feedback.showGauche && !feedback.showDroite)) {
					return;
				}
				orientation = SwingConstants.HORIZONTAL;
			}

			if (scrollType == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
				JViewport vp = scrollPane.getViewport();
				if (vp == null) {
					return;
				}
				Component comp = vp.getView();
				int units = Math.abs(unitsToScroll);

				// When the scrolling speed is set to maximum, it's possible
				// for a single wheel click to scroll by more units than
				// will fit in the visible area. This makes it
				// hard/impossible to get to certain parts of the scrolling
				// Component with the wheel. To make for more accurate
				// low-speed scrolling, we limit scrolling to the block
				// increment if the wheel was only rotated one click.
				boolean limitScroll = Math.abs(wheelRotation) == 1;

				// Check if we should use the visibleRect trick
				Object fastWheelScroll = toScroll
						.getClientProperty("JScrollBar.fastWheelScrolling");
				if (Boolean.TRUE == fastWheelScroll
						&& comp instanceof Scrollable) {
					// 5078454: Under maximum acceleration, we may scroll
					// by many 100s of units in ~1 second.
					//
					// BasicScrollBarUI.scrollByUnits() can bog down the EDT
					// with repaints in this situation. However, the
					// Scrollable interface allows us to pass in an
					// arbitrary visibleRect. This allows us to accurately
					// calculate the total scroll amount, and then update
					// the GUI once. This technique provides much faster
					// accelerated wheel scrolling.
					Scrollable scrollComp = (Scrollable) comp;
					Rectangle viewRect = vp.getViewRect();
					int startingX = viewRect.x;
					boolean leftToRight = comp.getComponentOrientation()
							.isLeftToRight();
					int scrollMin = toScroll.getMinimum();
					int scrollMax = toScroll.getMaximum()
							- toScroll.getModel().getExtent();

					if (limitScroll) {
						int blockIncr = scrollComp.getScrollableBlockIncrement(
								viewRect, orientation, direction);
						if (direction < 0) {
							scrollMin = Math.max(scrollMin, toScroll.getValue()
									- blockIncr);
						} else {
							scrollMax = Math.min(scrollMax, toScroll.getValue()
									+ blockIncr);
						}
					}

					for (int i = 0; i < units; i++) {
						int unitIncr = scrollComp.getScrollableUnitIncrement(
								viewRect, orientation, direction);
						// Modify the visible rect for the next unit, and
						// check to see if we're at the end already.
						if (orientation == SwingConstants.VERTICAL) {
							if (direction < 0) {
								viewRect.y -= unitIncr;
								if (viewRect.y <= scrollMin) {
									viewRect.y = scrollMin;
									break;
								}
							} else { // (direction > 0
								viewRect.y += unitIncr;
								if (viewRect.y >= scrollMax) {
									viewRect.y = scrollMax;
									break;
								}
							}
						} else {
							// Scroll left
							if ((leftToRight && direction < 0)
									|| (!leftToRight && direction > 0)) {
								viewRect.x -= unitIncr;
								if (leftToRight) {
									if (viewRect.x < scrollMin) {
										viewRect.x = scrollMin;
										break;
									}
								}
							}
							// Scroll right
							else if ((leftToRight && direction > 0)
									|| (!leftToRight && direction < 0)) {
								viewRect.x += unitIncr;
								if (leftToRight) {
									if (viewRect.x > scrollMax) {
										viewRect.x = scrollMax;
										break;
									}
								}
							} else {
								assert false : "Non-sensical ComponentOrientation / scroll direction";
							}
						}
					}
					// Set the final view position on the ScrollBar
					if (orientation == SwingConstants.VERTICAL) {
						toScroll.setValue(viewRect.y);
					} else {
						if (leftToRight) {
							toScroll.setValue(viewRect.x);
						} else {
							// rightToLeft scrollbars are oriented with
							// minValue on the right and maxValue on the
							// left.
							int newPos = toScroll.getValue()
									- (viewRect.x - startingX);
							if (newPos < scrollMin) {
								newPos = scrollMin;
							} else if (newPos > scrollMax) {
								newPos = scrollMax;
							}
							toScroll.setValue(newPos);
						}
					}
				} else {
					// Viewport's view is not a Scrollable, or fast wheel
					// scrolling is not enabled.
					Utils
							.scrollByUnits(toScroll, direction, units,
									limitScroll);
				}
			} else if (scrollType == MouseWheelEvent.WHEEL_BLOCK_SCROLL) {
				Utils.scrollByBlock(toScroll, direction);
			}
		}
	}

	private void moveVerticalScrollBar() {
		JScrollBar vBar = scrollPane.getVerticalScrollBar();
		if (feedback.showHaut && feedback.mouseInHaut) {
			if (vBar.getValue() > 0) {
				int d = getScrollBarHeight() - feedback.distanceHaut;
				int i = vitesseMoveY * d / getScrollBarHeight();
				int v = vBar.getValue() - i;// vBar.getUnitIncrement(-1);
				vBar.setValue(v);
			}
		} else if (feedback.showBas && feedback.mouseInBas) {
			if ((vBar.getValue() + vBar.getVisibleAmount() < vBar.getMaximum())) {
				int d = getScrollBarHeight() - feedback.distanceBas;
				int i = vitesseMoveY * d / getScrollBarHeight();
				int v = vBar.getValue() + i;// vBar.getUnitIncrement(1);
				vBar.setValue(v);
			}
		}
	}

	private void moveHorizontalScrollBar() {
		JScrollBar hBar = scrollPane.getHorizontalScrollBar();
		if (feedback.showGauche && feedback.mouseInGauche) {
			if (hBar.getValue() > 0) {
				int d = getScrollBarWidth() - feedback.distanceGauche;
				int i = vitesseMoveX * d / getScrollBarWidth();
				int v = hBar.getValue() - i;// hBar.getUnitIncrement(-1);
				hBar.setValue(v);
			}
		} else if (feedback.showDroite && feedback.mouseInDroite) {
			if ((hBar.getValue() + hBar.getVisibleAmount() < hBar.getMaximum())) {
				int d = getScrollBarWidth() - feedback.distanceDroite;
				int i = vitesseMoveX * d / getScrollBarWidth();

				int v = hBar.getValue() + i;// hBar.getUnitIncrement(1);
				hBar.setValue(v);
			}
		}
	}

	private final class MyActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			moveVerticalScrollBar();
			moveHorizontalScrollBar();

			verifyScrollBars();
		}
	}

	private final class MyAdjustmentListener implements AdjustmentListener {

		public void adjustmentValueChanged(AdjustmentEvent e) {
			verifyScrollBars();
		}
	}

	private final class MyAWTEventListener implements AWTEventListener {

		public void eventDispatched(AWTEvent event) {
			MouseEvent m = (MouseEvent) event;
			if (SwingUtilities.isDescendingFrom(m.getComponent(), scrollPane)
					&& m.getComponent() != scrollPane) {
				if (m.getID() == MouseEvent.MOUSE_MOVED) {
					Point p = SwingUtilities.convertPoint(m.getComponent(), m
							.getPoint(), scrollPane);

					mouseMovedInScrollPane(p);
				} else if (m.getID() == MouseEvent.MOUSE_WHEEL) {
					MouseWheelEvent mw = (MouseWheelEvent) m;

					mouseWheel(mw.getScrollType(), mw.getWheelRotation(), mw
							.getUnitsToScroll());
				} else if (m.getID() == MouseEvent.MOUSE_EXITED) {
					Point p = SwingUtilities.convertPoint(m.getComponent(), m
							.getPoint(), scrollPane);
					if (!scrollPane.contains(p)) {
						mouseExitedInScrollPane();
					}
				}
			}
		}
	}

	private final class MyMouseAdapter extends MouseAdapter {

		public void mouseMoved(MouseEvent e) {
			mouseMovedInScrollPane(e.getPoint());
		}

		public void mouseWheelMoved(MouseWheelEvent e) {
			mouseWheel(e.getScrollType(), e.getWheelRotation(), e
					.getUnitsToScroll());
		}

		public void mouseExited(MouseEvent e) {
			mouseExitedInScrollPane();
		}
	}

	private final class MyFeedback extends JComponent {

		private static final long serialVersionUID = -5174462618443755752L;

		private boolean showHaut;

		private boolean mouseInHaut;

		private int distanceHaut;

		private boolean showBas;

		private boolean mouseInBas;

		private int distanceBas;

		private boolean showGauche;

		private boolean mouseInGauche;

		private int distanceGauche;

		private boolean showDroite;

		private boolean mouseInDroite;

		private int distanceDroite;

		public MyFeedback() {
			super();

			showHaut = false;
			mouseInHaut = false;
			showBas = false;
			mouseInBas = false;
			showGauche = false;
			mouseInGauche = false;
			showDroite = false;
			mouseInDroite = false;
		}

		public void setShowHaut(boolean showHaut) {
			this.showHaut = showHaut;
			repaint();
		}

		public void setMouseInHaut(boolean mouseInHaut) {
			this.mouseInHaut = mouseInHaut;
			repaint();
		}

		public void setShowBas(boolean showBas) {
			this.showBas = showBas;
			repaint();
		}

		public void setMouseInBas(boolean mouseInBas) {
			this.mouseInBas = mouseInBas;
			repaint();
		}

		public void setShowGauche(boolean showGauche) {
			this.showGauche = showGauche;
			repaint();
		}

		public void setMouseInGauche(boolean mouseInGauche) {
			this.mouseInGauche = mouseInGauche;
			repaint();
		}

		public void setShowDroite(boolean showDroite) {
			this.showDroite = showDroite;
			repaint();
		}

		public void setMouseInDroite(boolean mouseInDroite) {
			this.mouseInDroite = mouseInDroite;
			repaint();
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			int tw = getScrollBarWidth();
			int th = getScrollBarHeight();

			paintVerticalBar(g2);
			paintHorizontalBar(g2);

			paintHaut(g2, tw, th);
			paintBas(g2, tw, th);
			paintGauche(g2, tw, th);
			paintDroite(g2, tw, th);

			g2.dispose();
		}

		private void paintVerticalBar(Graphics2D g2) {
			if (showHaut || showBas) {
				Rectangle bounds = scrollPane.getViewport().getBounds();

				Rectangle viewRect = scrollPane.getViewport().getViewRect();
				Dimension size = view.getSize();

				int y = bounds.y + viewRect.y * bounds.height / size.height;
				int h = viewRect.height * bounds.height / size.height;

				g2.setColor(barColor);
				g2.fillRoundRect(bounds.x + bounds.width - barSize, y, barSize,
						h, barSize, barSize);
			}
		}

		private void paintHorizontalBar(Graphics2D g2) {
			if (showGauche || showDroite) {
				Rectangle bounds = scrollPane.getViewport().getBounds();

				Rectangle viewRect = scrollPane.getViewport().getViewRect();
				Dimension size = view.getSize();

				int x = bounds.x + viewRect.x * bounds.width / size.width;
				int w = viewRect.width * bounds.width / size.width;

				g2.setColor(barColor);
				g2.fillRoundRect(x, bounds.y + bounds.height - barSize, w,
						barSize, barSize, barSize);
			}
		}

		private void paintHaut(Graphics2D g2, int tw, int th) {
			if (showHaut) {
				Rectangle bounds = scrollPane.getViewport().getBounds();

				int x1 = bounds.x + tw;
				int x2 = bounds.x + bounds.width - tw;
				int y = bounds.y;

				GradientPaint gp = new GradientPaint(0, y + th / 4,
						mouseInHaut ? mouseInLightColor : mouseOutLightColor,
						0, y, mouseInHaut ? mouseInDarkColor
								: mouseOutDarkColor);

				g2.setPaint(gp);

				Shape losange = new Arc2D.Float(bounds.x, y - th / 4,
						bounds.width, th / 2, 180, 180, Arc2D.PIE);

				g2.fill(losange);

				int mx = (x2 - x1) / 2 + x1;
				int my = th / 4;
				Shape fleche1 = new Polygon(new int[] { mx, mx - tw / 2, mx,
						mx + tw / 2 }, new int[] { y, y + my * 3 / 2, y + my,
						y + my * 3 / 2 }, 4);
				Shape fleche2 = new Polygon(new int[] { mx, mx - tw / 2, mx,
						mx + tw / 2 }, new int[] { y + my, y + my * 5 / 2,
						y + my * 2, y + my * 5 / 2 }, 4);

				g2.setColor(mouseInHaut ? arrowSelectedFillColor
						: arrowFillColor);
				g2.fill(fleche1);
				g2.fill(fleche2);
				g2.setColor(mouseInHaut ? arrowSelectedStrockColor
						: arrowStrockColor);
				g2.draw(fleche1);
				g2.draw(fleche2);
			}
		}

		private void paintBas(Graphics2D g2, int tw, int th) {
			if (showBas) {
				Rectangle bounds = scrollPane.getViewport().getBounds();

				int x1 = bounds.x + tw;
				int x2 = bounds.x + bounds.width - tw;
				int y = bounds.y + bounds.height;

				GradientPaint gp = new GradientPaint(0, y - th / 4,
						mouseInBas ? mouseInLightColor : mouseOutLightColor, 0,
						y, mouseInBas ? mouseInDarkColor : mouseOutDarkColor);

				g2.setPaint(gp);
				Shape losange = new Arc2D.Float(bounds.x, y - th / 4,
						bounds.width, th / 2, 0, 180, Arc2D.PIE);
				g2.fill(losange);

				int mx = (x2 - x1) / 2 + x1;
				int my = th / 4;
				Shape fleche1 = new Polygon(new int[] { mx, mx - tw / 2, mx,
						mx + tw / 2 }, new int[] { y, y - my * 3 / 2, y - my,
						y - my * 3 / 2 }, 4);
				Shape fleche2 = new Polygon(new int[] { mx, mx - tw / 2, mx,
						mx + tw / 2 }, new int[] { y - my, y - my * 5 / 2,
						y - my * 2, y - my * 5 / 2 }, 4);

				g2.setColor(mouseInBas ? arrowSelectedFillColor
						: arrowFillColor);
				g2.fill(fleche1);
				g2.fill(fleche2);
				g2.setColor(mouseInBas ? arrowSelectedStrockColor
						: arrowStrockColor);
				g2.draw(fleche1);
				g2.draw(fleche2);
			}
		}

		private void paintGauche(Graphics2D g2, int tw, int th) {
			if (showGauche) {
				Rectangle bounds = scrollPane.getViewport().getBounds();

				int y1 = bounds.y + th;
				int y2 = bounds.y + bounds.height - th;
				int x = bounds.x;

				GradientPaint gp = new GradientPaint(x + tw / 4, 0,
						mouseInGauche ? mouseInLightColor : mouseOutLightColor,
						x, 0, mouseInGauche ? mouseInDarkColor
								: mouseOutDarkColor);

				g2.setPaint(gp);
				Shape losange = new Arc2D.Float(x - tw / 4, bounds.y, tw / 2,
						bounds.height, 270, 180, Arc2D.PIE);

				g2.fill(losange);

				int mx = tw / 4;
				int my = (y2 - y1) / 2 + y1;
				Shape fleche1 = new Polygon(new int[] { x, x + mx * 3 / 2,
						x + mx, x + mx * 3 / 2 }, new int[] { my, my - th / 2,
						my, my + th / 2 }, 4);
				Shape fleche2 = new Polygon(new int[] { x + mx, x + mx * 5 / 2,
						x + mx * 2, x + mx * 5 / 2 }, new int[] { my,
						my - th / 2, my, my + th / 2 }, 4);

				g2.setColor(mouseInGauche ? arrowSelectedFillColor
						: arrowFillColor);
				g2.fill(fleche1);
				g2.fill(fleche2);
				g2.setColor(mouseInGauche ? arrowSelectedStrockColor
						: arrowStrockColor);
				g2.draw(fleche1);
				g2.draw(fleche2);
			}
		}

		private void paintDroite(Graphics2D g2, int tw, int th) {
			if (showDroite) {
				Rectangle bounds = scrollPane.getViewport().getBounds();

				int y1 = bounds.y + th;
				int y2 = bounds.y + bounds.height - th;
				int x = bounds.x + bounds.width;

				GradientPaint gp = new GradientPaint(x - tw / 4, 0,
						mouseInDroite ? mouseInLightColor : mouseOutLightColor,
						x, 0, mouseInDroite ? mouseInDarkColor
								: mouseOutDarkColor);
				g2.setPaint(gp);
				Shape losange = new Arc2D.Float(x - tw / 4, bounds.y, tw / 2,
						bounds.height, 90, 180, Arc2D.PIE);

				g2.fill(losange);

				int mx = tw / 4;
				int my = (y2 - y1) / 2 + y1;
				Shape fleche1 = new Polygon(new int[] { x, x - mx * 3 / 2,
						x - mx, x - mx * 3 / 2 }, new int[] { my, my - th / 2,
						my, my + th / 2 }, 4);
				Shape fleche2 = new Polygon(new int[] { x - mx, x - mx * 5 / 2,
						x - mx * 2, x - mx * 5 / 2 }, new int[] { my,
						my - th / 2, my, my + th / 2 }, 4);

				g2.setColor(mouseInDroite ? arrowSelectedFillColor
						: arrowFillColor);
				g2.fill(fleche1);
				g2.fill(fleche2);
				g2.setColor(mouseInDroite ? arrowSelectedStrockColor
						: arrowStrockColor);
				g2.draw(fleche1);
				g2.draw(fleche2);
			}
		}
	}

	private final class SimpleLayout implements LayoutManager {

		public void addLayoutComponent(String name, Component comp) {
		}

		public void removeLayoutComponent(Component comp) {
		}

		public Dimension preferredLayoutSize(Container parent) {
			if (scrollPane != null) {
				return scrollPane.getPreferredSize();
			}
			return new Dimension();
		}

		public Dimension minimumLayoutSize(Container parent) {
			if (scrollPane != null) {
				return scrollPane.getMinimumSize();
			}
			return new Dimension();
		}

		public void layoutContainer(Container parent) {
			if (scrollPane != null) {
				Dimension size = parent.getSize();
				scrollPane.setBounds(0, 0, size.width, size.height);
				feedback.setBounds(0, 0, size.width, size.height);
			}
		}
	}
}
