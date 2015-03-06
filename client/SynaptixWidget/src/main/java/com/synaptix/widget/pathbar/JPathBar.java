package com.synaptix.widget.pathbar;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.synaptix.widget.pathbar.plaf.BasicPathBarUI;
import com.synaptix.widget.pathbar.plaf.PathBarUI;

public class JPathBar extends JComponent implements ListDataListener, ListSelectionListener {

	private static final long serialVersionUID = 364362291521871875L;

	private static final String uiClassID = "PathBarUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID, BasicPathBarUI.class.getName());
	}

	private int fixedItemWidth = -1;

	private int fixedItemHeight = -1;

	private ListModel model;

	private PathBarCellRenderer cellRenderer;

	private ListSelectionModel selectionModel;

	private Color gridColor = Color.DARK_GRAY;

	private boolean showGrid = true;

	public JPathBar() {
		this(new DefaultListModel());
	}

	public JPathBar(ListModel model) {
		super();

		this.cellRenderer = createCellRenderer();
		setSelectionModel(createSelectionModel());

		setModel(model);
		updateUI();
	}

	@Override
	public void updateUI() {
		super.updateUI();

		setUI(UIManager.getUI(this));
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	public PathBarUI getUI() {
		return (PathBarUI) ui;
	}

	public int getFixedItemWidth() {
		return fixedItemWidth;
	}

	public void setFixedItemWidth(int fixedItemWidth) {
		int oldValue = this.fixedItemWidth;
		this.fixedItemWidth = fixedItemWidth;
		firePropertyChange("fixedItemWidth", oldValue, fixedItemWidth);
	}

	public int getFixedItemHeight() {
		return fixedItemHeight;
	}

	public void setFixedItemHeight(int fixedItemHeight) {
		int oldValue = this.fixedItemHeight;
		this.fixedItemHeight = fixedItemHeight;
		firePropertyChange("fixedItemHeight", oldValue, fixedItemHeight);
	}

	public ListModel getModel() {
		return model;
	}

	public void setModel(ListModel model) {
		ListModel oldValue = this.model;
		if (this.model != null) {
			this.model.removeListDataListener(this);
		}
		this.model = model;
		this.model.addListDataListener(this);

		firePropertyChange("model", oldValue, model);
	}

	protected void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	protected PathBarCellRenderer createCellRenderer() {
		return new DefaultPathBarCellRenderer();
	}

	public PathBarCellRenderer getCellRenderer() {
		return cellRenderer;
	}

	public void setCellRenderer(PathBarCellRenderer cellRenderer) {
		PathBarCellRenderer oldValue = this.cellRenderer;
		this.cellRenderer = cellRenderer;
		firePropertyChange("cellRenderer", oldValue, cellRenderer);
	}

	protected ListSelectionModel createSelectionModel() {
		return new DefaultListSelectionModel();
	}

	public ListSelectionModel getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(ListSelectionModel selectionModel) {
		ListSelectionModel oldValue = this.selectionModel;
		if (this.selectionModel != null) {
			this.selectionModel.removeListSelectionListener(this);
		}
		this.selectionModel = selectionModel;
		this.selectionModel.addListSelectionListener(this);

		firePropertyChange("selectionModel", oldValue, selectionModel);
	}

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		boolean oldValue = this.showGrid;
		this.showGrid = showGrid;

		firePropertyChange("showGrid", oldValue, showGrid);
	}

	public Color getGridColor() {
		return gridColor;
	}

	public void setGridColor(Color gridColor) {
		Color oldValue = this.gridColor;
		this.gridColor = gridColor;
		firePropertyChange("gridColor", oldValue, showGrid);
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		resizeAndRepaint();
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		resizeAndRepaint();
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		resizeAndRepaint();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int size = model.getSize();
		int firstIndex = limit(e.getFirstIndex(), 0, size - 1);
		int lastIndex = limit(e.getLastIndex(), 0, size - 1);
		Rectangle firstRowRect = getCellRect(firstIndex);
		Rectangle lastRowRect = getCellRect(lastIndex);
		Rectangle dirtyRegion = firstRowRect.union(lastRowRect);
		repaint(dirtyRegion);
	}

	private int limit(int i, int a, int b) {
		return Math.min(b, Math.max(i, a));
	}

	public int indexAtPoint(Point p) {
		return getUI().indexAtPoint(this, p);
	}

	public Rectangle getCellRect(int index) {
		return getUI().getCellRect(this, index);
	}
}
