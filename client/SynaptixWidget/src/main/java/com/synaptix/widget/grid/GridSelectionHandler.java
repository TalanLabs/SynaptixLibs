package com.synaptix.widget.grid;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.synaptix.swing.utils.GraphicsHelper;

public class GridSelectionHandler implements MouseListener, MouseMotionListener {

	protected final JGridCell gridPanel;

	protected Color selectColor = new Color(0, 0, 0, 255);

	protected Stroke selectStroke = new BasicStroke(1.0f);

	protected boolean enabled = true;

	public GridSelectionHandler(JGridCell gridPanel) {
		super();

		this.gridPanel = gridPanel;

		this.gridPanel.addMouseListener(this);
		this.gridPanel.addMouseMotionListener(this);
	}

	public Color getSelectColor() {
		return selectColor;
	}

	public void setSelectColor(Color selectColor) {
		this.selectColor = selectColor;
	}

	public Stroke getSelectStroke() {
		return selectStroke;
	}

	public void setSelectStroke(Stroke selectStroke) {
		this.selectStroke = selectStroke;
	}

	public void paint(Graphics g, Rectangle pClip) {
		paintSelectionCells(g, pClip);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	protected void paintSelectionCells(Graphics g, Rectangle pClip) {
		ValueSelectionModel selectionModel = this.gridPanel.getSelectionModel();
		if (selectionModel != null && !selectionModel.isSelectionEmpty()) {
			for (Object value : selectionModel.getSelectionCells()) {
				Rectangle r = this.gridPanel.getCellPlacement(value);
				if (r.intersects(pClip)) {
					Rectangle rect = this.gridPanel.getCellRect(r);
					paintSelectionCell(g, value, rect);
				}
			}
		}
	}

	protected void paintSelectionCell(Graphics g, Object value, Rectangle rect) {
		Graphics2D g2 = (Graphics2D) g.create();
		GraphicsHelper.activeAntiAliasing(g2);
		g2.setColor(selectColor);
		g2.setStroke(selectStroke);

		g2.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);

		g2.dispose();
	}

	public void reset() {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!e.isConsumed() && enabled) {
			ValueSelectionModel selectionModel = this.gridPanel.getSelectionModel();
			if (selectionModel != null) {
				Object value = this.gridPanel.getCellAtPoint(e.getPoint());
				if (value != null) {
					if (!selectionModel.isSelectedCell(value)) {
						if (e.isControlDown()) {
							selectionModel.addSelectionCells(value);
						} else {
							selectionModel.setSelectionCells(value);
						}
					} else {
						if (e.isControlDown()) {
							selectionModel.removeSelectionCells(value);
						} else if (selectionModel.getSelectionCount() > 1) {
							selectionModel.setSelectionCells(value);
						}
					}
				} else if (!selectionModel.isSelectionEmpty()) {
					selectionModel.clearSelection();
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
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
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
