package com.synaptix.swing.path;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.UIManager;

import com.synaptix.swing.plaf.PathUI;

public class JPath extends JComponent implements PathModelListener, PathSelectionListener, Scrollable {

	private static final long serialVersionUID = -2832664370948044217L;

	private static final String uiClassID = "PathUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID, "com.synaptix.swing.plaf.basic.BasicPathUI"); //$NON-NLS-1$
	}

	private PathModel pathModel;

	private PathRenderer pathRenderer;

	private Color selectionNodeColor = new Color(50, 64, 255, 128);

	private PathSelectionModel pathSelectionModel;

	public JPath(PathModel pathModel) {
		this(pathModel, new DefaultPathRenderer());
	}

	public JPath(PathModel pathModel, PathRenderer pathRenderer) {
		super();

		this.setFocusable(true);

		this.pathModel = pathModel;
		this.pathRenderer = pathRenderer;

		pathSelectionModel = createPathSelectionModel();

		pathModel.addPathModelListener(this);
		pathSelectionModel.addPathSelectionListener(this);

		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		updateUI();
	}

	public PathModel getPathModel() {
		return pathModel;
	}

	public void setPathModel(PathModel pathModel) {
		if (this.pathModel != null) {
			this.pathModel.removePathModelListener(this);
		}
		this.pathModel = pathModel;

		pathModel.addPathModelListener(this);

		getUI().clearRotateImageCache();
		resizeAndRepaint();
	}

	protected PathSelectionModel createPathSelectionModel() {
		return new PathSelectionModel();
	}

	public void setPathSelectionModel(PathSelectionModel pathSelectionModel) {
		if (this.pathSelectionModel != null) {
			this.pathSelectionModel.removePathSelectionListener(this);
		}
		this.pathSelectionModel = pathSelectionModel;

		pathSelectionModel.addPathSelectionListener(this);

		getUI().clearRotateImageCache();
		resizeAndRepaint();
	}

	public PathSelectionModel getPathSelectionModel() {
		return pathSelectionModel;
	}

	public PathUI getUI() {
		return (PathUI) ui;
	}

	public void setUI(PathUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((PathUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public PathRenderer getPathRenderer() {
		return pathRenderer;
	}

	public void setPathRenderer(PathRenderer pathRenderer) {
		this.pathRenderer = pathRenderer;

		getUI().clearRotateImageCache();
		resizeAndRepaint();
	}

	public Color getSelectionNodeColor() {
		return selectionNodeColor;
	}

	public void setSelectionNodeColor(Color selectionNodeColor) {
		this.selectionNodeColor = selectionNodeColor;
		resizeAndRepaint();
	}

	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	public void pathModelChanged(PathModelEvent e) {
		pathSelectionModel.clearSelection();

		getUI().clearRotateImageCache();
		resizeAndRepaint();
	}

	public void selectionChanged() {
		getUI().clearRotateImageCache();
		resizeAndRepaint();
	}

	public void paintNodeRenderer(Graphics g, PathRenderer renderer, int index) {
		boolean isSelected = false;
		if (!isPaintingForPrint()) {
			isSelected = pathSelectionModel.isSelectionNode(index);
		}
		renderer.paintNode(g, this, isSelected, index);
	}

	public void paintLineRenderer(Graphics g, PathRenderer renderer, int index1, int index2) {
		boolean isSelected1 = false;
		boolean isSelected2 = false;
		if (!isPaintingForPrint()) {
			isSelected1 = pathSelectionModel.isSelectionNode(index1);
			isSelected2 = pathSelectionModel.isSelectionNode(index2);
		}
		renderer.paintLine(g, this, isSelected1, index1, isSelected2, index2);
	}

	public Dimension prepareNodeDimension(PathRenderer renderer, int index) {
		boolean isSelected = false;
		if (!isPaintingForPrint()) {
			isSelected = pathSelectionModel.isSelectionNode(index);
		}
		return renderer.getNodeDimension(this, isSelected, index);
	}

	public Dimension prepareLineDimension(PathRenderer renderer, int index1, int index2) {
		boolean isSelected1 = false;
		boolean isSelected2 = false;
		if (!isPaintingForPrint()) {
			isSelected1 = pathSelectionModel.isSelectionNode(index1);
			isSelected2 = pathSelectionModel.isSelectionNode(index2);
		}
		return renderer.getLineDimension(this, isSelected1, index1, isSelected2, index2);
	}

	public Color prepareNodeColorText(PathRenderer renderer, int index) {
		boolean isSelected = false;
		if (!isPaintingForPrint()) {
			isSelected = pathSelectionModel.isSelectionNode(index);
		}
		return renderer.getNodeColorText(this, isSelected, index);
	}

	public Font prepareNodeFontText(PathRenderer renderer, int index) {
		boolean isSelected = false;
		if (!isPaintingForPrint()) {
			isSelected = pathSelectionModel.isSelectionNode(index);
		}
		return renderer.getNodeFont(this, isSelected, index);
	}

	public Color prepareLineColorText(PathRenderer renderer, int index1, int index2) {
		boolean isSelected = false;
		if (!isPaintingForPrint()) {
			isSelected = pathSelectionModel.isSelectionLine(index1, index2);
		}
		return renderer.getLineColorText(this, isSelected, index1, index2);
	}

	public Font prepareLineFontText(PathRenderer renderer, int index1, int index2) {
		boolean isSelected = false;
		if (!isPaintingForPrint()) {
			isSelected = pathSelectionModel.isSelectionLine(index1, index2);
		}
		return renderer.getLineFont(this, isSelected, index1, index2);
	}

	public Dimension getPreferredScrollableViewportSize() {
		return this.getPreferredSize();
	}

	public boolean getScrollableTracksViewportWidth() {
		return getParent() instanceof JViewport && (((JViewport) getParent()).getWidth() > getPreferredSize().width);
	}

	public boolean getScrollableTracksViewportHeight() {
		return getParent() instanceof JViewport && (((JViewport) getParent()).getHeight() > getPreferredSize().height);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return getScrollableUnitIncrement(visibleRect, orientation, direction);
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}
}
