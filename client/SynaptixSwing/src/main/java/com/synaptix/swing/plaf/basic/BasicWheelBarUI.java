package com.synaptix.swing.plaf.basic;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;

import com.synaptix.swing.JWheelBar;

public class BasicWheelBarUI extends ScrollBarUI {

	private static Cursor nResizeCursor = Cursor
			.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);

	private static Cursor wResizeCursor = Cursor
			.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);

	private static final Color gradient1Color = new Color(0xCCE9FF);

	private static final Color gradient2Color = new Color(0x91ACC1);

	private static final Color trait1Color = new Color(0xC8E3F8);

	private static final Color trait2Color = new Color(0x9FBCD2);

	private static final Color borderColor = new Color(0x808080);

	private static final int gradientStep = 2;

	private static final int scrollSpeedThrottle = 60;

	private static final int spaceTrait = 10;

	protected JWheelBar wheelBar;

	protected CellRendererPane rendererPane;

	protected MouseInputListener mouseInputListener;

	private int scrollBarWidth;

	protected ScrollListener scrollListener;

	protected Timer scrollTimer;

	public class MouseInputHandler implements MouseInputListener {

		protected int currentMouseX, currentMouseY;

		private boolean isDragging;
		
		private Cursor otherCursor = null;

		private void swapCursor() {
			if (otherCursor == null) {
				if (wheelBar.getOrientation() == JWheelBar.HORIZONTAL) {
					otherCursor = wResizeCursor;
				} else {
					otherCursor = nResizeCursor;
				}
			}
			
			Cursor tmp = wheelBar.getCursor();
			wheelBar.setCursor(otherCursor);
			otherCursor = tmp;
		}
		
		public void mouseClicked(MouseEvent e) {
			// if (!scrollTimer.isRunning()) {
			// scrollTimer.start();
			// }
		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			wheelBar.setValueIsAdjusting(true);

			currentMouseX = e.getX();
			currentMouseY = e.getY();
			isDragging = true;
		}

		public void mouseReleased(MouseEvent e) {
			scrollTimer.stop();
			wheelBar.setValueIsAdjusting(false);
			isDragging = false;
		}

		public void mouseDragged(MouseEvent e) {
			if (isDragging) {
				setValueFrom(e);
			} else {
				currentMouseX = e.getX();
				currentMouseY = e.getY();
			}
		}

		private void setValueFrom(MouseEvent e) {
			int d = 0;
			if (wheelBar.getOrientation() == JWheelBar.HORIZONTAL) {
				d = e.getX() - currentMouseX;
			} else {
				d = e.getY() - currentMouseY;
			}
			//scrollByUnit(d / 2);
			
			d = d / 2;
			wheelBar.setValue(d);
			
			wheelBar.repaint();
		}

		public void mouseMoved(MouseEvent e) {
			if (wheelBar.getOrientation() == JWheelBar.HORIZONTAL) {
				if (wheelBar.getCursor() != wResizeCursor) {
					swapCursor();
				}
			} else {
				if (wheelBar.getCursor() != nResizeCursor) {
					swapCursor();
				}
			}
		}
	}

	protected MouseInputListener createMouseInputListener() {
		return new MouseInputHandler();
	}

	public static ComponentUI createUI(JComponent h) {
		return new BasicWheelBarUI();
	}

	public void installUI(JComponent c) {
		wheelBar = (JWheelBar) c;

		installDefaults();
		installListeners();
	}

	protected void installDefaults() {
		scrollBarWidth = UIManager.getInt("ScrollBar.width"); //$NON-NLS-1$
		if (scrollBarWidth <= 0) {
			scrollBarWidth = 16;
		}
		/*
		 * LookAndFeel.installColorsAndFont(header, "TableHeader.background",
		 * "TableHeader.foreground", "TableHeader.font");
		 * LookAndFeel.installProperty(header, "opaque", Boolean.TRUE);
		 */
	}

	protected void installListeners() {
		mouseInputListener = createMouseInputListener();

		wheelBar.addMouseListener(mouseInputListener);
		wheelBar.addMouseMotionListener(mouseInputListener);
		// header.addFocusListener(focusListener);

		scrollListener = new ScrollListener();
		scrollTimer = new Timer(scrollSpeedThrottle, scrollListener);
		scrollTimer.setInitialDelay(300); // default InitialDelay?
	}

	public void uninstallUI(JComponent c) {
		uninstallDefaults();
		uninstallListeners();

		wheelBar.remove(rendererPane);
		rendererPane = null;
		wheelBar = null;
	}

	protected void uninstallDefaults() {
	}

	protected void uninstallListeners() {
		scrollTimer.stop();
		scrollTimer = null;

		wheelBar.removeMouseListener(mouseInputListener);
		wheelBar.removeMouseMotionListener(mouseInputListener);

		mouseInputListener = null;
	}

	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g.create();

		int width = wheelBar.getWidth();
		int height = wheelBar.getHeight();

		int decalage = wheelBar.getValue() % 10;

		g2.setColor(gradient1Color);
		g2.fillRect(0, 0, width, height);

		if (wheelBar.getOrientation() == JWheelBar.HORIZONTAL) {
			int nbTrait = width / spaceTrait;
			int gradientWidth = width / gradientStep;
			g2.setPaint(new GradientPaint(0, 0, gradient2Color, gradientWidth,
					0, gradient1Color));
			g2.fillRect(0, 0, gradientWidth, height);

			g2.setPaint(new GradientPaint(width - gradientWidth, 0,
					gradient1Color, width, 0, gradient2Color));
			g2.fillRect(width - gradientWidth, 0, gradientWidth, height);

			for (int i = 0; i < nbTrait; i++) {
				// double x = Math.abs(Math.sin((((double) i) -
				// halfNbTraitDouble)
				// * Math.PI / nbTraitDouble));
				//
				// if (i <= halfNbTraitDouble) {
				// x = halfWidth - x * halfWidth;
				// } else {
				// x = x * halfWidth + halfWidth;
				// }

				double x = i * spaceTrait;
				x += decalage;

				g2.setColor(trait1Color);
				g2.drawLine((int) x, 0, (int) x, height);

				g2.setColor(trait2Color);
				g2.drawLine((int) x + 1, 1, (int) x + 1, height);
			}
		} else {
			int nbTrait = height / spaceTrait;
			int gradientHeight = height / gradientStep;
			g2.setPaint(new GradientPaint(0, 0, gradient2Color, 0,
					gradientHeight, gradient1Color));
			g2.fillRect(0, 0, width, gradientHeight);

			g2.setPaint(new GradientPaint(0, height - gradientHeight,
					gradient1Color, 0, height, gradient2Color));
			g2.fillRect(0, height - gradientHeight, width, gradientHeight);

			for (int i = 0; i < nbTrait; i++) {
				// double y = Math.abs(Math.sin((((double) i) -
				// halfNbTraitDouble)
				// * Math.PI / nbTraitDouble));
				//
				// if (i <= halfNbTraitDouble) {
				// y = halfHeight - y * halfHeight;
				// } else {
				// y = y * halfHeight + halfHeight;
				// }

				double y = i * spaceTrait;
				y += decalage;

				g2.setColor(trait1Color);
				g2.drawLine(0, (int) y, width, (int) y);

				g2.setColor(trait2Color);
				g2.drawLine(0, (int) y + 1, width, (int) y + 1);
			}
		}

		g2.setColor(borderColor);
		g2.drawRoundRect(0, 0, width - 1, height - 1, 3, 3);

		g2.dispose();
	}

	public Dimension getPreferredSize(JComponent c) {
		return (wheelBar.getOrientation() == JWheelBar.VERTICAL) ? new Dimension(
				scrollBarWidth, 16)
				: new Dimension(16, scrollBarWidth);
	}

	public Dimension getMaximumSize(JComponent c) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	private void scrollByUnit(int unit) {
		int d = wheelBar.getValue();
		d += unit;
		if (d < 0) {
			d = spaceTrait + (d % spaceTrait);
		}
		if (d >= spaceTrait) {
			d = spaceTrait - (d % spaceTrait);
		}
		wheelBar.setValue(d);
	}

	protected final class ScrollListener implements ActionListener {

		private int direction = +1;

		public ScrollListener() {
			direction = +1;
		}

		public ScrollListener(int dir) {
			direction = dir;
		}

		public void setDirection(int direction) {
			this.direction = direction;
		}

		public void actionPerformed(ActionEvent e) {
			scrollByUnit(direction * 2);
		}
	}
}
