package com.synaptix.widget.grid;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.TooManyListenersException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.apache.commons.lang3.tuple.Pair;

import com.synaptix.swing.utils.GraphicsHelper;

public class GridDragDropHandler implements MouseListener, MouseMotionListener, DropTargetListener {

	protected static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

	protected static final Cursor MOVE_CURSOR = new Cursor(Cursor.MOVE_CURSOR);

	protected final JGridCell gridCell;

	protected Color forbidenFillColor = new Color(255, 0, 0, 128);

	protected Color forbidenStrokeColor = new Color(128, 0, 0, 128);

	protected Color dragStrokeColor = new Color(0, 0, 128, 255);

	protected Stroke dragStroke = new BasicStroke(2.0f);

	protected Pair<Object, Rectangle> dragCell;

	protected boolean freeDragCell;

	protected double distanceActiveDrag = 5;

	protected Point first = null;

	protected boolean enabled = true;

	public GridDragDropHandler(JGridCell gridCell) {
		super();

		this.gridCell = gridCell;

		this.gridCell.setTransferHandler(createTransferHandler());
		try {
			this.gridCell.getDropTarget().addDropTargetListener(this);
		} catch (TooManyListenersException e) {
		}

		this.gridCell.addMouseListener(this);
		this.gridCell.addMouseMotionListener(this);
	}

	protected TransferHandler createTransferHandler() {
		return new DragDropTransferHandler(this.gridCell, this);
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

	public Color getDragStrokeColor() {
		return dragStrokeColor;
	}

	public void setDragStrokeColor(Color dragStrokeColor) {
		this.dragStrokeColor = dragStrokeColor;
	}

	public Stroke getDragStroke() {
		return dragStroke;
	}

	public void setDragStroke(Stroke dragStroke) {
		this.dragStroke = dragStroke;
	}

	public double getDistanceActiveDrag() {
		return distanceActiveDrag;
	}

	public void setDistanceActiveDrag(double distanceActiveDrag) {
		this.distanceActiveDrag = distanceActiveDrag;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void paint(Graphics g, Rectangle pClip) {
		if (dragCell != null) {
			Rectangle r = dragCell.getRight();
			if (pClip.intersects(r)) {
				Rectangle rect = this.gridCell.getCellRect(r);

				paintDragCell(g, rect);
			}
		}
	}

	protected void paintDragCell(Graphics g, Rectangle rect) {
		Graphics2D g2 = (Graphics2D) g.create();
		GraphicsHelper.activeAntiAliasing(g2);

		if (freeDragCell) {
			g2.setColor(dragStrokeColor);
			g2.setStroke(dragStroke);
			g2.drawRect(rect.x + 1, rect.y + 1, rect.width - 2 - 1, rect.height - 2 - 1);
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

	public void setDragCell(Object value, Rectangle r, boolean free) {
		Pair<Object, Rectangle> oldDragCell = this.dragCell;
		this.dragCell = value != null && r != null ? Pair.of(value, r) : null;
		this.freeDragCell = free;

		if (oldDragCell != null && dragCell != null) {
			if (!oldDragCell.equals(r)) {
				Rectangle oldR = oldDragCell.getRight();
				Rectangle rect1 = this.gridCell.getCellRect(oldR);
				Rectangle rect2 = this.gridCell.getCellRect(r);

				this.gridCell.repaint(rect1.union(rect2));
			}
		} else if (oldDragCell != null) {
			Rectangle oldR = oldDragCell.getRight();
			this.gridCell.repaint(this.gridCell.getCellRect(oldR));
		} else if (dragCell != null) {
			Rectangle rect2 = this.gridCell.getCellRect(r);
			this.gridCell.repaint(rect2);
		}
	}

	public void reset() {
		this.gridCell.setCursor(DEFAULT_CURSOR);
		this.dragCell = null;
		this.freeDragCell = true;
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		setDragCell(null, null, true);
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		setDragCell(null, null, true);
	}

	private boolean isHitCell(Object value, int x, int y) {
		Rectangle rect = this.gridCell.getCellRect(value);
		return rect.contains(x, y);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!e.isConsumed() && enabled && e.getButton() == MouseEvent.BUTTON1) {
			Object value = this.gridCell.getCellAtPoint(e.getPoint());
			Cursor cursor = DEFAULT_CURSOR;
			if (value != null && this.gridCell.getSelectionModel() != null && this.gridCell.getSelectionModel().getSelectionCount() == 1 && this.gridCell.getSelectionModel().isSelectedCell(value)) {
				if (isHitCell(value, e.getX(), e.getY())) {
					first = e.getPoint();
					cursor = MOVE_CURSOR;
					e.consume();
				}
			}
			this.gridCell.setCursor(cursor);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (first != null) {
			first = null;
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

	protected boolean activeDrag(MouseEvent e, Point first) {
		Point p = e.getPoint();
		double d = first.distance(p);
		return d > distanceActiveDrag;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (first != null) {
			if (activeDrag(e, first)) {
				first = null;
				TransferHandler handler = this.gridCell.getTransferHandler();
				handler.exportAsDrag(this.gridCell, e, TransferHandler.MOVE);
				e.consume();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!e.isConsumed() && enabled) {
			Object value = this.gridCell.getCellAtPoint(e.getPoint());
			Cursor cursor = DEFAULT_CURSOR;
			if (value != null && this.gridCell.getSelectionModel() != null && this.gridCell.getSelectionModel().getSelectionCount() == 1 && this.gridCell.getSelectionModel().isSelectedCell(value)) {
				if (isHitCell(value, e.getX(), e.getY())) {
					cursor = MOVE_CURSOR;
					e.consume();
				}
			}
			this.gridCell.setCursor(cursor);
		}
	}

	public static class DragDropTransferHandler extends TransferHandler {

		private static final long serialVersionUID = -4524318509186448055L;

		protected final JGridCell gridCell;

		protected final GridDragDropHandler gridDragDropHandler;

		protected Point point;

		public DragDropTransferHandler(JGridCell gridCell, GridDragDropHandler gridDragDropHandler) {
			super();
			this.gridCell = gridCell;
			this.gridDragDropHandler = gridDragDropHandler;
		}

		@Override
		public void exportAsDrag(JComponent comp, InputEvent e, int action) {
			point = null;
			if (e instanceof MouseEvent) {
				point = ((MouseEvent) e).getPoint();
			}
			super.exportAsDrag(comp, e, action);
		}

		@Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.MOVE;
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			if (this.gridCell.getSelectionModel() != null && this.gridCell.getSelectionModel().getSelectionCount() == 1) {
				Object value = this.gridCell.getSelectionModel().getSelectionCell();
				Rectangle r = this.gridCell.getCellPlacement(value);
				Point anchor = null;
				if (point != null) {
					int c1 = this.gridCell.columnAtPoint(point);
					int r1 = this.gridCell.rowAtPoint(point);
					if (c1 != -1 && r1 != -1) {
						anchor = new Point(c1 - r.x, r1 - r.y);
					}
				}
				return new GridCellTransferable(value, r.getSize(), anchor, true);
			}
			return super.createTransferable(c);
		}

		@Override
		protected void exportDone(JComponent source, Transferable data, int action) {
			point = null;
		}

		protected Rectangle getDragPlacement(GridCellTransferable.TransferData data, DropLocation dropLocation) {
			Rectangle rect = null;

			Dimension dim = data.dimension;
			int numGridX = this.gridCell.getNumGridX();
			int numGridY = this.gridCell.getNumGridY();
			if (dropLocation != null && dim != null && dim.width <= numGridX && dim.height <= numGridY) {
				int column = this.gridCell.columnAtPoint(dropLocation.getDropPoint());
				int row = this.gridCell.rowAtPoint(dropLocation.getDropPoint());
				if (column != -1 && row != -1) {
					Point anchor = data.anchor;
					column = column - (anchor != null ? anchor.x : 0);
					row = row - (anchor != null ? anchor.y : 0);
					int c = (column + dim.width) < numGridX ? (column >= 0 ? column : 0) : numGridX - dim.width;
					int r = (row + dim.height) < numGridY ? (row >= 0 ? row : 0) : numGridY - dim.height;
					rect = new Rectangle(c, r, dim.width, dim.height);
				}
			}
			return rect;
		}

		@Override
		public boolean canImport(TransferSupport support) {
			if (support.isDataFlavorSupported(GridCellTransferable.GRID_CELL_FLAVOR)) {
				GridCellTransferable.TransferData data = null;
				try {
					data = (GridCellTransferable.TransferData) support.getTransferable().getTransferData(GridCellTransferable.GRID_CELL_FLAVOR);
				} catch (UnsupportedFlavorException e) {
				} catch (IOException e) {
				}
				if (data != null) {
					Rectangle rect = getDragPlacement(data, support.getDropLocation());
					if (rect != null) {
						if (this.gridCell.isFreeCell(rect, data.value)) {
							gridDragDropHandler.setDragCell(data.value, rect, true);
							return true;
						} else {
							gridDragDropHandler.setDragCell(data.value, rect, false);
							return false;
						}
					}
				}
			}

			gridDragDropHandler.setDragCell(null, null, true);
			return super.canImport(support);
		}

		@Override
		public boolean importData(TransferSupport support) {
			if (canImport(support)) {
				GridCellTransferable.TransferData data = null;
				try {
					data = (GridCellTransferable.TransferData) support.getTransferable().getTransferData(GridCellTransferable.GRID_CELL_FLAVOR);
				} catch (UnsupportedFlavorException e) {
				} catch (IOException e) {
				}

				Rectangle rect = getDragPlacement(data, support.getDropLocation());
				if (data.local) {
					this.gridCell.modifyCell(data.value, rect);
				} else {
					this.gridCell.addCell(data.value, rect);
					if (this.gridCell.getSelectionModel() != null) {
						this.gridCell.getSelectionModel().setSelectionCells(data.value);
					}
				}
				return true;
			}
			return super.importData(support);
		}
	}
}
