package com.synaptix.widget.grid;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JViewport;

public class GridPanningHandler implements MouseListener, MouseMotionListener {

	protected final JGridCell gridPanel;

	protected Point start;

	protected int tolerance = 4;

	protected boolean enabled = true;

	public GridPanningHandler(JGridCell gridPanel) {
		super();

		this.gridPanel = gridPanel;

		this.gridPanel.addMouseListener(this);
		this.gridPanel.addMouseMotionListener(this);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void paint(Graphics g, Rectangle pClip) {
	}

	public void reset() {
	}

	protected boolean isPanningEvent(MouseEvent event) {
		return event != null ? event.isControlDown() : false;
	}

	protected boolean isParentViewport() {
		return this.gridPanel.getParent() instanceof JViewport;
	}

	protected boolean isSignificant(double dx, double dy) {
		return Math.abs(dx) > tolerance || Math.abs(dy) > tolerance;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!e.isConsumed() && enabled && isParentViewport() && isPanningEvent(e) && !e.isPopupTrigger()) {
			start = e.getPoint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!e.isConsumed() && start != null) {
			int dx = Math.abs(start.x - e.getX());
			int dy = Math.abs(start.y - e.getY());

			if (isSignificant(dx, dy)) {
				e.consume();
			}
		}
		start = null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!e.isConsumed() && start != null && isParentViewport()) {
			int dx = e.getX() - start.x;
			int dy = e.getY() - start.y;

			Rectangle r = ((JViewport) this.gridPanel.getParent()).getViewRect();

			int right = r.x + ((dx > 0) ? 0 : r.width) - dx;
			int bottom = r.y + ((dy > 0) ? 0 : r.height) - dy;

			this.gridPanel.scrollRectToVisible(new Rectangle(right, bottom, 0, 0));

			e.consume();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
