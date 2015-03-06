package com.synaptix.widget.grid;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.CellRendererPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.apache.commons.lang.NullArgumentException;
import org.jdesktop.swingx.JXPanel;

import com.synaptix.swing.utils.GraphicsHelper;

public class JGridCell extends JXPanel implements ValueSelectionListener, Scrollable, PropertyChangeListener {

	private static final long serialVersionUID = -8676143706629935647L;

	protected boolean showGrid = true;

	protected Color gridColor = new Color(128, 128, 128, 128);

	protected Stroke gridStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 1, new float[] { 2.0f, 2.0f }, 0);

	protected int cellSize;

	protected int numGridX;

	protected int numGridY;

	protected final Map<Object, Rectangle> cellMap = new HashMap<Object, Rectangle>();

	protected CellRendererPane rendererPane;

	protected GridCellRenderer cellRenderer;

	protected ValueSelectionModel selectionModel;

	protected GridPanningHandler gridPanningHandler;

	protected GridSelectionHandler gridSelectionHandler;

	protected GridDragDropHandler gridDragDropHandler;

	protected GridResizeHandler gridResizeHandler;

	protected boolean showPage = true;

	protected Color pageFillColor = Color.WHITE;

	protected Color pageStrokeColor = Color.WHITE.darker();

	protected Insets pageBorderInsets = new Insets(40, 40, 40, 40);

	public JGridCell(int cellSize, int numGridX, int numGridY) {
		super();

		this.cellSize = cellSize;
		this.numGridX = numGridX;
		this.numGridY = numGridY;

		MyMouseHandler mouseHandler = new MyMouseHandler();
		this.addMouseListener(mouseHandler);
		this.addMouseMotionListener(mouseHandler);

		this.rendererPane = new CellRendererPane();
		this.add(rendererPane);

		this.cellRenderer = createGridCellRenderer();

		setSelectionModel(createValueSelectionModel());

		this.setFocusable(true);
		this.setAutoscrolls(true);

		this.addPropertyChangeListener(this);

		createHandlers();
	}

	public void addGridCellModelListener(GridCellModelListener l) {
		listenerList.add(GridCellModelListener.class, l);
	}

	public void removeGridCellModelListener(GridCellModelListener l) {
		listenerList.remove(GridCellModelListener.class, l);
	}

	protected void fireValueChanged(GridCellModelEvent event) {
		GridCellModelListener[] ls = listenerList.getListeners(GridCellModelListener.class);
		for (GridCellModelListener l : ls) {
			l.valueChanged(event);
		}
	}

	public Insets getPageInsets() {
		Insets insets = this.getInsets();
		return new Insets(insets.top + pageBorderInsets.top, insets.left + pageBorderInsets.left, insets.bottom + pageBorderInsets.bottom, insets.right + pageBorderInsets.right);
	}

	@Override
	public Dimension getPreferredSize() {
		Insets insets = this.getPageInsets();
		return new Dimension(insets.left + cellSize * numGridX + insets.right, insets.top + cellSize * numGridY + insets.bottom);
	}

	protected void createHandlers() {
		this.gridPanningHandler = createGridPanningHandler();
		this.gridSelectionHandler = createGridSelectionHandler();
		this.gridResizeHandler = createGridResizeHandler();
		this.gridDragDropHandler = createGridDragDropHandler();
	}

	protected GridPanningHandler createGridPanningHandler() {
		return new GridPanningHandler(this);
	}

	public GridPanningHandler getGridPanningHandler() {
		return gridPanningHandler;
	}

	protected GridSelectionHandler createGridSelectionHandler() {
		return new GridSelectionHandler(this);
	}

	public GridSelectionHandler getGridSelectionHandler() {
		return gridSelectionHandler;
	}

	protected GridResizeHandler createGridResizeHandler() {
		return new GridResizeHandler(this);
	}

	public GridResizeHandler getGridResizeHandler() {
		return gridResizeHandler;
	}

	protected GridDragDropHandler createGridDragDropHandler() {
		return new GridDragDropHandler(this);
	}

	public GridDragDropHandler getGridDragDropHandler() {
		return gridDragDropHandler;
	}

	public int getCellSize() {
		return cellSize;
	}

	public void setCellSize(int cellSize) {
		int oldValue = this.cellSize;
		this.cellSize = cellSize;
		firePropertyChange("cellSize", oldValue, cellSize);
	}

	public int getNumGridX() {
		return numGridX;
	}

	public void setNumGridX(int numGridX) {
		int oldValue = this.numGridX;
		this.numGridX = numGridX;
		firePropertyChange("numGridX", oldValue, numGridX);
	}

	public int getNumGridY() {
		return numGridY;
	}

	public void setNumGridY(int numGridY) {
		int oldValue = this.numGridY;
		this.numGridY = numGridY;
		firePropertyChange("numGridY", oldValue, numGridY);
	}

	protected GridCellRenderer createGridCellRenderer() {
		return new DefaultGridCellRenderer();
	}

	public GridCellRenderer getCellRenderer() {
		return cellRenderer;
	}

	public void setCellRenderer(GridCellRenderer cellRenderer) {
		GridCellRenderer oldValue = cellRenderer;
		this.cellRenderer = cellRenderer;
		firePropertyChange("cellRenderer", oldValue, cellRenderer);
	}

	protected ValueSelectionModel createValueSelectionModel() {
		return new DefaultValueSelectionModel();
	}

	public void setSelectionModel(ValueSelectionModel selectionModel) {
		if (this.selectionModel != null) {
			selectionModel.removeValueSelectionListener(this);
		}
		ValueSelectionModel oldValue = this.selectionModel;
		this.selectionModel = selectionModel;
		if (this.selectionModel != null) {
			this.selectionModel.addValueSelectionListener(this);
		}
		firePropertyChange("selectionModel", oldValue, selectionModel);
	}

	public ValueSelectionModel getSelectionModel() {
		return selectionModel;
	}

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		boolean oldValue = this.showGrid;
		this.showGrid = showGrid;
		firePropertyChange("showGrid", oldValue, showGrid);
	}

	public boolean isShowPage() {
		return showPage;
	}

	public void setShowPage(boolean showPage) {
		boolean oldValue = this.showPage;
		this.showPage = showPage;
		firePropertyChange("showPage", oldValue, showPage);
	}

	public Color getPageFillColor() {
		return pageFillColor;
	}

	public void setPageFillColor(Color pageFillColor) {
		Color oldValue = this.pageFillColor;
		this.pageFillColor = pageFillColor;
		firePropertyChange("pageFillColor", oldValue, pageFillColor);
	}

	public Color getPageStrokeColor() {
		return pageStrokeColor;
	}

	public void setPageStrokeColor(Color pageStrokeColor) {
		Color oldValue = this.pageFillColor;
		this.pageStrokeColor = pageStrokeColor;
		firePropertyChange("pageStrokeColor", oldValue, pageStrokeColor);
	}

	public Insets getPageBorderInsets() {
		return pageBorderInsets;
	}

	public void setPageBorderInsets(Insets pageBorderInsets) {
		Insets oldValue = this.pageBorderInsets;
		this.pageBorderInsets = pageBorderInsets;
		firePropertyChange("pageBorderInsets", oldValue, pageBorderInsets);
	}

	public void addCell(Object value, Rectangle r) {
		if (value == null) {
			throw new NullArgumentException("value is null");
		}
		if (cellMap.containsKey(value)) {
			throw new IllegalArgumentException("value exists");
		}
		cellMap.put(value, r);

		repaint(getCellRect(r));

		fireValueChanged(new GridCellModelEvent(this, GridCellModelEvent.Type.ADD));
	}

	public void modifyCell(Object value, Rectangle r) {
		if (value == null) {
			throw new NullArgumentException("value is null");
		}
		if (!cellMap.containsKey(value)) {
			throw new IllegalArgumentException("value not exists");
		}
		Rectangle rectOld = _removeCell0(value, false);

		cellMap.put(value, r);

		repaint(getCellRect(r).union(rectOld));

		fireValueChanged(new GridCellModelEvent(this, GridCellModelEvent.Type.MODIFY));
	}

	public void removeCell(Object value) {
		Rectangle rect = _removeCell0(value, true);
		if (rect != null) {
			repaint(rect);
		}

		fireValueChanged(new GridCellModelEvent(this, GridCellModelEvent.Type.REMOVE));
	}

	private Rectangle _removeCell0(Object value, boolean removeSelection) {
		Rectangle rect = getCellRect(value);

		cellMap.remove(value);

		if (removeSelection && selectionModel != null && selectionModel.isSelectedCell(value)) {
			selectionModel.removeValueSelectionListener(this);
			selectionModel.removeSelectionCells(value);
			reset();
			selectionModel.addValueSelectionListener(this);
		}
		return rect;
	}

	public void removeAllCells() {
		cellMap.clear();
		selectionModel.removeValueSelectionListener(this);
		selectionModel.clearSelection();
		reset();
		selectionModel.addValueSelectionListener(this);
		resizeAndRepaint();

		fireValueChanged(new GridCellModelEvent(this, GridCellModelEvent.Type.REMOVE));
	}

	public Set<Object> getCells() {
		return new HashSet<Object>(cellMap.keySet());
	}

	public Map<Object, Rectangle> getCellPlacementMap() {
		return new HashMap<Object, Rectangle>(cellMap);
	}

	public void reset() {
		this.gridPanningHandler.reset();
		this.gridSelectionHandler.reset();
		this.gridDragDropHandler.reset();
		this.gridResizeHandler.reset();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Rectangle clip = g.getClipBounds();
		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width, clip.y + clip.height);

		int cMin = columnAtPoint(upperLeft);
		if (cMin == -1) {
			cMin = 0;
		}
		int cMax = columnAtPoint(lowerRight);
		if (cMax == -1) {
			cMax = numGridX - 1;
		}
		int rMin = rowAtPoint(upperLeft);
		if (rMin == -1) {
			rMin = 0;
		}
		int rMax = rowAtPoint(lowerRight);
		if (rMax == -1) {
			rMax = numGridY - 1;
		}
		Rectangle pClip = new Rectangle(cMin, rMin, cMax - cMin + 1, rMax - rMin + 1);

		if (showPage) {
			paintPage(g, pClip);
		}
		if (showGrid) {
			paintGrid(g, pClip);
		}

		paintCells(g, pClip);
		paintHandlers(g, pClip);

		rendererPane.removeAll();
	}

	protected void paintHandlers(Graphics g, Rectangle pClip) {
		this.gridPanningHandler.paint(g, pClip);
		this.gridSelectionHandler.paint(g, pClip);
		this.gridDragDropHandler.paint(g, pClip);
		this.gridResizeHandler.paint(g, pClip);
	}

	protected void paintPage(Graphics g, Rectangle pClip) {
		Graphics2D g2 = (Graphics2D) g.create();

		GraphicsHelper.activeAntiAliasing(g2);

		Rectangle rect = getCellRect(pClip);
		rect.x -= pageBorderInsets.left;
		rect.y -= pageBorderInsets.top;
		rect.width += pageBorderInsets.left + pageBorderInsets.right;
		rect.height += pageBorderInsets.top + pageBorderInsets.bottom;

		if (pageFillColor != null) {
			g2.setColor(pageFillColor);
			g2.fillRect(rect.x, rect.y, rect.width, rect.height);
		}

		if (pageStrokeColor != null) {
			g2.setColor(pageStrokeColor);
			if (pClip.x == 0) {
				g2.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height - 1);
			}
			if (pClip.x + pClip.width == numGridX) {
				g2.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width - 1, rect.y + rect.height - 1);
			}
			if (pClip.y == 0) {
				g2.drawLine(rect.x, rect.y, rect.x + rect.width - 1, rect.y);
			}
			if (pClip.y + pClip.height == numGridY) {
				g2.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width - 1, rect.y + rect.height - 1);
			}
		}

		g2.dispose();
	}

	protected void paintGrid(Graphics g, Rectangle pClip) {
		Graphics2D g2 = (Graphics2D) g.create();

		GraphicsHelper.activeAntiAliasing(g2);

		Rectangle rect = getCellRect(pClip);

		int xs = rect.x;
		int xf = rect.x + rect.width - 1;
		int ys = rect.y;
		int yf = rect.y + rect.height - 1;

		g2.setColor(gridColor);
		g2.setStroke(gridStroke);

		for (int c = 0; c < pClip.width; c++) {
			int p = rect.x + c * cellSize;
			g2.drawLine(p, ys, p, yf);
		}

		if (pClip.x + pClip.width == numGridX) {
			g2.drawLine(xf, ys, xf, yf);
		}

		for (int r = 0; r < pClip.height; r++) {
			int p = rect.y + r * cellSize;
			g2.drawLine(xs, p, xf, p);
		}

		if (pClip.y + pClip.height == numGridY) {
			g2.drawLine(xs, yf, xf, yf);
		}

		g2.dispose();
	}

	protected void paintCells(Graphics g, Rectangle pClip) {
		if (!cellMap.isEmpty()) {
			for (Entry<Object, Rectangle> cell : cellMap.entrySet()) {
				Object value = cell.getKey();
				Rectangle r = cell.getValue();
				if (pClip.intersects(r)) {
					Rectangle rect = getCellRect(r);
					Component c = cellRenderer.getGridCellRendererComponent(this, value);
					rendererPane.paintComponent(g, c, this, rect.x, rect.y, rect.width, rect.height, true);
				}
			}
		}
	}

	public int columnAtPoint(Point point) {
		Insets insets = this.getPageInsets();
		int w = this.getWidth();
		int x = Math.max(insets.left, (w - (cellSize * numGridX)) / 2);
		int v = point.x - x;
		int c = v / cellSize;
		return (v >= 0 && c >= 0 && c < numGridX) ? c : -1;
	}

	public int rowAtPoint(Point point) {
		Insets insets = this.getPageInsets();
		int h = this.getHeight();
		int y = Math.max(insets.top, (h - (cellSize * numGridY)) / 2);
		int v = point.y - y;
		int r = v / cellSize;
		return (v >= 0 && r >= 0 && r < numGridY) ? r : -1;
	}

	public Rectangle getCellRect(Object value) {
		return getCellRect(getCellPlacement(value));
	}

	public Rectangle getCellRect(Rectangle r) {
		Insets insets = this.getPageInsets();
		int w = this.getWidth();
		int h = this.getHeight();
		int x = Math.max(insets.left, (w - (cellSize * numGridX)) / 2);
		int y = Math.max(insets.top, (h - (cellSize * numGridY)) / 2);
		return new Rectangle(x + r.x * cellSize, y + r.y * cellSize, r.width * cellSize, r.height * cellSize);
	}

	public boolean isFreeCell(Rectangle r, Object ignoreValue) {
		boolean ok = true;
		if (!cellMap.isEmpty()) {
			Iterator<Entry<Object, Rectangle>> it = cellMap.entrySet().iterator();
			while (it.hasNext() && ok) {
				Entry<Object, Rectangle> cell = it.next();
				if (ignoreValue == null || !cell.getKey().equals(ignoreValue)) {
					Rectangle current = cell.getValue();
					ok = !current.intersects(r);
				}
			}
		}
		return ok;
	}

	/**
	 * Get row, column and size for cell (Grid position)
	 * 
	 * @param value
	 * @return
	 */
	public Rectangle getCellPlacement(Object value) {
		return cellMap.get(value);
	}

	/**
	 * Get cell for x, y position
	 * 
	 * @param point
	 * @return
	 */
	public Object getCellAtPoint(Point point) {
		int c = columnAtPoint(point);
		int r = rowAtPoint(point);
		return getCellAtPlacement(c, r);
	}

	/**
	 * Get cell for row and column
	 * 
	 * @param c
	 * @param r
	 * @return
	 */
	public Object getCellAtPlacement(int c, int r) {
		Object res = null;
		if (!cellMap.isEmpty()) {
			Iterator<Entry<Object, Rectangle>> it = cellMap.entrySet().iterator();
			while (it.hasNext() && res == null) {
				Entry<Object, Rectangle> cell = it.next();
				Rectangle current = cell.getValue();
				if (current.contains(c, r)) {
					res = cell.getKey();
				}
			}
		}
		return res;
	}

	@Override
	public void valueChanged(ValueSelectionEvent e) {
		reset();

		Rectangle rect = null;
		if (e.getOldValues() != null) {
			for (Object oldValue : e.getOldValues()) {
				Rectangle r1 = getCellRect(oldValue);
				rect = rect != null ? rect.union(r1) : r1;
			}
		}
		if (e.getNewValues() != null) {
			for (Object newValue : e.getNewValues()) {
				Rectangle r1 = getCellRect(newValue);
				rect = rect != null ? rect.union(r1) : r1;
			}
		}
		if (rect != null) {
			repaint(rect);
		}
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		Insets insets = this.getPageInsets();
		return new Dimension(insets.left + 1 * cellSize + insets.right, insets.top + 1 * cellSize + insets.bottom);
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return getParent() instanceof JViewport && (((JViewport) getParent()).getHeight() >= getPreferredSize().height);
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return getParent() instanceof JViewport && (((JViewport) getParent()).getWidth() >= getPreferredSize().width);
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return getScrollableUnitIncrement(visibleRect, orientation, direction);
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		Insets insets = this.getPageInsets();
		int row = (visibleRect.y - insets.top) / cellSize;
		int column = (visibleRect.x - insets.left) / cellSize;
		int realX = column * cellSize + insets.left;
		int realY = row * cellSize + insets.top;
		if (orientation == SwingConstants.VERTICAL) {
			if (visibleRect.y == realY) {
				return cellSize;
			} else {
				if (direction < 0) {
					return visibleRect.y == 0 ? 0 : visibleRect.y - realY;
				} else {
					return (realY + cellSize) - visibleRect.y;
				}
			}
		} else {
			if (visibleRect.x == realX) {
				return cellSize;
			} else {
				if (direction < 0) {
					return visibleRect.x == 0 ? 0 : visibleRect.x - realX;
				} else {
					return (realX + cellSize) - visibleRect.x;
				}
			}
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		reset();
		setEnabledHandlers(enabled);
		super.setEnabled(enabled);
	}

	protected void setEnabledHandlers(boolean enabled) {
		this.gridDragDropHandler.setEnabled(enabled);
		this.gridResizeHandler.setEnabled(enabled);
	}

	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		resizeAndRepaint();
	}

	private class MyMouseHandler extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			JGridCell.this.requestFocus();
		}
	}
}