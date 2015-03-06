package com.synaptix.widget.grid;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JViewport;

import org.apache.commons.lang3.tuple.Pair;

import com.synaptix.swing.utils.GraphicsHelper;

public class GridResizeHandler implements MouseListener, MouseMotionListener {

	protected static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

	protected static final Cursor N_RESIZE_CURSOR = new Cursor(Cursor.N_RESIZE_CURSOR);

	protected static final Cursor S_RESIZE_CURSOR = new Cursor(Cursor.S_RESIZE_CURSOR);

	protected static final Cursor E_RESIZE_CURSOR = new Cursor(Cursor.E_RESIZE_CURSOR);

	protected static final Cursor W_RESIZE_CURSOR = new Cursor(Cursor.W_RESIZE_CURSOR);

	protected static final Cursor NE_RESIZE_CURSOR = new Cursor(Cursor.NE_RESIZE_CURSOR);

	protected static final Cursor NW_RESIZE_CURSOR = new Cursor(Cursor.NW_RESIZE_CURSOR);

	protected static final Cursor SE_RESIZE_CURSOR = new Cursor(Cursor.SE_RESIZE_CURSOR);

	protected static final Cursor SW_RESIZE_CURSOR = new Cursor(Cursor.SW_RESIZE_CURSOR);

	protected Color forbidenFillColor = new Color(255, 0, 0, 128);

	protected Color forbidenStrokeColor = new Color(128, 0, 0, 128);

	protected Color resizeStrokeColor = new Color(0, 0, 0, 255);

	protected Stroke resizeStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 1, new float[] { 4.0f, 4.0f }, 0);

	protected Color resizeSquareColor = new Color(0, 0, 0, 255);

	protected final JGridCell gridPanel;

	protected Pair<Object, Rectangle> resizeCell;

	protected boolean freeResizeCell;

	protected Object valueResize;

	protected Rectangle startPlacementResize;

	protected int dirResize;

	protected int detectionSize = 10;

	protected Rectangle oldResizeRect;

	protected boolean enabled = true;

	public GridResizeHandler(JGridCell gridPanel) {
		super();

		this.gridPanel = gridPanel;

		this.gridPanel.addMouseListener(this);
		this.gridPanel.addMouseMotionListener(this);
	}

	public int getDetectionSize() {
		return detectionSize;
	}

	public void setDetectionSize(int detectionSize) {
		this.detectionSize = detectionSize;
	}

	public Color getForbidenFillColor() {
		return forbidenFillColor;
	}

	public void setForbidenFillColor(Color forbidenFillColor) {
		this.forbidenFillColor = forbidenFillColor;
	}

	public Color getForbidenStrokeColor() {
		return forbidenStrokeColor;
	}

	public void setForbidenStrokeColor(Color forbidenStrokeColor) {
		this.forbidenStrokeColor = forbidenStrokeColor;
	}

	public Color getResizeSquareColor() {
		return resizeSquareColor;
	}

	public void setResizeSquareColor(Color resizeSquareColor) {
		this.resizeSquareColor = resizeSquareColor;
	}

	public Color getResizeStrokeColor() {
		return resizeStrokeColor;
	}

	public void setResizeStrokeColor(Color resizeStrokeColor) {
		this.resizeStrokeColor = resizeStrokeColor;
	}

	public Stroke getResizeStroke() {
		return resizeStroke;
	}

	public void setResizeStroke(Stroke resizeStroke) {
		this.resizeStroke = resizeStroke;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void paint(Graphics g, Rectangle pClip) {
		paintHelpResizeCell(g, pClip);

		if (resizeCell != null) {
			Rectangle r = resizeCell.getRight();
			if (pClip.intersects(r)) {
				Rectangle rect = this.gridPanel.getCellRect(r);
				paintResizeCell(g, rect);
			}
		}
	}

	protected void paintHelpResizeCell(Graphics g, Rectangle pClip) {
		if (enabled && this.gridPanel.getSelectionModel() != null && this.gridPanel.getSelectionModel().getSelectionCount() == 1) {
			Object value = this.gridPanel.getSelectionModel().getSelectionCell();
			Rectangle r = this.gridPanel.getCellPlacement(value);
			if (r.intersects(pClip)) {
				Rectangle rect = this.gridPanel.getCellRect(r);
				Graphics2D g2 = (Graphics2D) g.create();
				GraphicsHelper.activeAntiAliasing(g2);

				g2.setColor(resizeSquareColor);
				g2.fillRect(rect.x, rect.y, detectionSize, detectionSize);
				g2.fillRect(rect.x + (rect.width - detectionSize) / 2, rect.y, detectionSize, detectionSize);
				g2.fillRect(rect.x + rect.width - detectionSize, rect.y, detectionSize, detectionSize);
				g2.fillRect(rect.x + rect.width - detectionSize, rect.y + (rect.height - detectionSize) / 2, detectionSize, detectionSize);

				g2.fillRect(rect.x, rect.y + (rect.height - detectionSize) / 2, detectionSize, detectionSize);
				g2.fillRect(rect.x, rect.y + rect.height - detectionSize, detectionSize, detectionSize);
				g2.fillRect(rect.x + (rect.width - detectionSize) / 2, rect.y + rect.height - detectionSize, detectionSize, detectionSize);
				g2.fillRect(rect.x + rect.width - detectionSize, rect.y + rect.height - detectionSize, detectionSize, detectionSize);

				g2.dispose();
			}
		}
	}

	protected void paintResizeCell(Graphics g, Rectangle rect) {
		Graphics2D g2 = (Graphics2D) g.create();
		GraphicsHelper.activeAntiAliasing(g2);

		if (freeResizeCell) {
			g2.setColor(resizeStrokeColor);
			g2.setStroke(resizeStroke);
			g2.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);

			g2.fillRect(rect.x, rect.y, detectionSize, detectionSize);
			g2.fillRect(rect.x + (rect.width - detectionSize) / 2, rect.y, detectionSize, detectionSize);
			g2.fillRect(rect.x + rect.width - detectionSize, rect.y, detectionSize, detectionSize);
			g2.fillRect(rect.x + rect.width - detectionSize, rect.y + (rect.height - detectionSize) / 2, detectionSize, detectionSize);

			g2.fillRect(rect.x, rect.y + (rect.height - detectionSize) / 2, detectionSize, detectionSize);
			g2.fillRect(rect.x, rect.y + rect.height - detectionSize, detectionSize, detectionSize);
			g2.fillRect(rect.x + (rect.width - detectionSize) / 2, rect.y + rect.height - detectionSize, detectionSize, detectionSize);
			g2.fillRect(rect.x + rect.width - detectionSize, rect.y + rect.height - detectionSize, detectionSize, detectionSize);
		} else {
			g2.setColor(forbidenFillColor);
			g2.fillRect(rect.x, rect.y, rect.width, rect.height);

			g2.setColor(forbidenStrokeColor);
			g2.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
			g2.drawLine(rect.x, rect.y, rect.x + rect.width - 1, rect.y + rect.height - 1);
			g2.drawLine(rect.x + rect.width - 1, rect.y, rect.x, rect.y + rect.height - 1);

		}

		g2.dispose();
	}

	public void setResizeCell(Object value, Rectangle r, boolean free) {
		Pair<Object, Rectangle> oldResizeCell = this.resizeCell;
		this.resizeCell = value != null && r != null ? Pair.of(value, r) : null;
		this.freeResizeCell = free;

		if (oldResizeCell != null && resizeCell != null) {
			if (!oldResizeCell.equals(r)) {
				Rectangle oldR = oldResizeCell.getRight();
				Rectangle rect1 = this.gridPanel.getCellRect(oldR);
				Rectangle rect2 = this.gridPanel.getCellRect(r);

				this.gridPanel.repaint(rect1.union(rect2));
			}
		} else if (oldResizeCell != null) {
			Rectangle oldR = oldResizeCell.getRight();
			this.gridPanel.repaint(this.gridPanel.getCellRect(oldR));
		} else if (resizeCell != null) {
			Rectangle rect2 = this.gridPanel.getCellRect(r);
			this.gridPanel.repaint(rect2);
		}
	}

	public void reset() {
		this.gridPanel.setCursor(DEFAULT_CURSOR);
		this.resizeCell = null;
		this.freeResizeCell = true;
	}

	protected Rectangle validateResize(Object valueResize, Rectangle r) {
		return r;
	}

	protected void resize(Object valueResize, Rectangle r) {
		this.gridPanel.modifyCell(valueResize, r);
	}

	protected int getHitCell(Object value, int x, int y) {
		int res = 0;
		Rectangle rect = this.gridPanel.getCellRect(value);
		if (rect.contains(x, y)) {
			x = x - rect.x;
			y = y - rect.y;
			if (x >= 0 && x < detectionSize) {
				res = res | 1;
			}
			if (y >= 0 && y < detectionSize) {
				res = res | 2;
			}
			if (x >= rect.width - detectionSize && x < rect.width) {
				res = res | 4;
			}
			if (y >= rect.height - detectionSize && y < rect.height) {
				res = res | 8;
			}
		}
		return res;
	}

	protected Cursor getCursor(int c) {
		Cursor cursor = null;
		if (c == 1) {
			cursor = W_RESIZE_CURSOR;
		} else if (c == 3) {
			cursor = NW_RESIZE_CURSOR;
		} else if (c == 2) {
			cursor = N_RESIZE_CURSOR;
		} else if (c == 6) {
			cursor = NE_RESIZE_CURSOR;
		} else if (c == 4) {
			cursor = E_RESIZE_CURSOR;
		} else if (c == 12) {
			cursor = SE_RESIZE_CURSOR;
		} else if (c == 8) {
			cursor = S_RESIZE_CURSOR;
		} else if (c == 9) {
			cursor = SW_RESIZE_CURSOR;
		}
		return cursor;
	}

	protected Rectangle getNewRect(Point p) {
		int c = this.gridPanel.columnAtPoint(p);
		int r = this.gridPanel.rowAtPoint(p);

		if (c != -1 && r != -1) {
			Rectangle re = new Rectangle(startPlacementResize);

			int cf = startPlacementResize.x + startPlacementResize.width - 1;
			int rf = startPlacementResize.y + startPlacementResize.height - 1;
			if (dirResize == 1 || dirResize == 3 || dirResize == 9) {
				if (c <= cf) {
					re.x = c;
					re.width = cf - c + 1;
				} else {
					re.x = cf;
					re.width = 1;
				}
			}
			if (dirResize == 2 || dirResize == 3 || dirResize == 6) {
				if (r <= rf) {
					re.y = r;
					re.height = rf - r + 1;
				} else {
					re.y = rf;
					re.height = 1;
				}
			}
			if (dirResize == 4 || dirResize == 6 || dirResize == 12) {
				if (c >= startPlacementResize.x) {
					re.width = c - startPlacementResize.x + 1;
				} else {
					re.width = 1;
				}
			}
			if (dirResize == 12 || dirResize == 8 || dirResize == 9) {
				if (r >= startPlacementResize.y) {
					re.height = r - startPlacementResize.y + 1;
				} else {
					re.height = 1;
				}
			}
			oldResizeRect = re;
		}
		return validateResize(valueResize, oldResizeRect);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!e.isConsumed() && enabled && e.getButton() == MouseEvent.BUTTON1) {
			Object value = this.gridPanel.getCellAtPoint(e.getPoint());
			Cursor cursor = DEFAULT_CURSOR;
			if (value != null && this.gridPanel.getSelectionModel() != null && this.gridPanel.getSelectionModel().getSelectionCount() == 1 && this.gridPanel.getSelectionModel().isSelectedCell(value)) {
				int i = getHitCell(value, e.getX(), e.getY());
				Cursor cur = getCursor(i);
				if (cur != null) {
					cursor = cur;
					valueResize = value;
					startPlacementResize = new Rectangle(this.gridPanel.getCellPlacement(value));
					oldResizeRect = startPlacementResize;
					dirResize = i;
					e.consume();
				}
			}
			this.gridPanel.setCursor(cursor);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (valueResize != null) {
			Rectangle re = getNewRect(e.getPoint());

			if (this.gridPanel.isFreeCell(re, valueResize)) {
				resize(valueResize, re);
			}

			e.consume();

			valueResize = null;
			startPlacementResize = null;
			oldResizeRect = null;
			dirResize = 0;
			this.setResizeCell(null, null, true);
		}
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

	protected boolean isParentViewport() {
		return this.gridPanel.getParent() instanceof JViewport;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (valueResize != null) {
			Rectangle re = getNewRect(e.getPoint());

			if (this.gridPanel.isFreeCell(re, valueResize)) {
				this.setResizeCell(valueResize, re, true);
			} else {
				this.setResizeCell(valueResize, re, false);
			}

			Rectangle rect = null;
			int dec = this.gridPanel.getCellSize() / 8;
			if (dirResize == 1) { // WEST
				rect = this.gridPanel.getCellRect(new Rectangle(re.x, re.y + re.height / 2, 1, 1));
				rect.grow(dec, 0);
			} else if (dirResize == 4) { // EAST
				rect = this.gridPanel.getCellRect(new Rectangle(re.x + re.width - 1, re.y + re.height / 2, 1, 1));
				rect.grow(dec, 0);
			} else if (dirResize == 2) { // NORTH
				rect = this.gridPanel.getCellRect(new Rectangle(re.x + re.width / 2, re.y, 1, 1));
				rect.grow(0, dec);
			} else if (dirResize == 8) { // SOUTH
				rect = this.gridPanel.getCellRect(new Rectangle(re.x + re.width / 2, re.y + re.height - 1, 1, 1));
				rect.grow(0, dec);
			} else if (dirResize == 3) { // NORTH-WEST
				rect = this.gridPanel.getCellRect(new Rectangle(re.x, re.y, 1, 1));
				rect.grow(dec, dec);
			} else if (dirResize == 9) { // SOUTH-WEST
				rect = this.gridPanel.getCellRect(new Rectangle(re.x, re.y + re.height - 1, 1, 1));
				rect.grow(dec, dec);
			} else if (dirResize == 6) { // NORTH-EAST
				rect = this.gridPanel.getCellRect(new Rectangle(re.x + re.width - 1, re.y, 1, 1));
				rect.grow(dec, dec);
			} else if (dirResize == 12) { // SOUTH-EAST
				rect = this.gridPanel.getCellRect(new Rectangle(re.x + re.width - 1, re.y + re.height - 1, 1, 1));
				rect.grow(dec, dec);
			}
			if (rect != null) {
				this.gridPanel.scrollRectToVisible(rect);
			}

			e.consume();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!e.isConsumed() && enabled) {
			Object value = this.gridPanel.getCellAtPoint(e.getPoint());
			Cursor cursor = DEFAULT_CURSOR;
			if (value != null && this.gridPanel.getSelectionModel() != null && this.gridPanel.getSelectionModel().getSelectionCount() == 1 && this.gridPanel.getSelectionModel().isSelectedCell(value)) {
				int c = getHitCell(value, e.getX(), e.getY());
				Cursor cur = getCursor(c);
				if (cur != null) {
					cursor = cur;
					e.consume();
				}
			}
			this.gridPanel.setCursor(cursor);
		}
	}
}
