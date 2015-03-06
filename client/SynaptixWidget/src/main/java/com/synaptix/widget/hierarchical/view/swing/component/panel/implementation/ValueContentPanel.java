package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.HierarchicalPanelKind;
import com.synaptix.widget.hierarchical.view.swing.component.panel.ContentPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.writer.CellInformation;

public class ValueContentPanel<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends ContentPanel<F, E, F, L> {

	private static final long serialVersionUID = 7492262773113270333L;

	public ValueContentPanel(final ConfigurationContext<E, F, L> configurationProvider, final HierarchicalPanel<F, E, F, L> parent) {
		super(configurationProvider, parent);
	}

	@Override
	protected HierarchicalPanelKind getPanelKind() {
		return HierarchicalPanelKind.VALUE;
	}

	@Override
	protected void paintContents(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);

		final Rectangle visibleRectangle = getVisibleRect();
		int firstColumn = super.parent.getColumnIndexAtAbsciss(visibleRectangle.x);
		int lastColumn = super.parent.getColumnIndexAtAbsciss(visibleRectangle.x + visibleRectangle.width);
		int nextColumnAbsciss = super.parent.getColumnAbscissAt(firstColumn);

		for (int i = firstColumn; i <= lastColumn; i++) {
			nextColumnAbsciss = paintColumnAndReturnEndAbsciss(i, g2, nextColumnAbsciss);
		}

		g2.dispose();
	}

	private int paintColumnAndReturnEndAbsciss(final int columnIndex, final Graphics2D graphics, final int absciss) {
		final int columnWidth = super.parent.getColumnSizeAt(columnIndex);

		final Rectangle visibleRectangle = getVisibleRect();
		int firstLine = super.parent.getRowIndexAtOrdinate(visibleRectangle.y);
		int lastLine = super.parent.getRowIndexAtOrdinate(visibleRectangle.y + visibleRectangle.height - 1);
		int nextRowOrdinate = super.parent.getLineOrdinateAt(firstLine);

		for (int rowIndex = firstLine; rowIndex <= lastLine; rowIndex++) {
			nextRowOrdinate = paintCell(rowIndex, columnIndex, graphics, absciss, nextRowOrdinate);
		}
		return absciss + columnWidth;
	}

	private int paintCell(final int rowIndex, final int columnIndex, final Graphics2D graphics, final int absciss, final int nextRowOrdinate) {
		final Rectangle rectangleToPaint = buildRectangleToPaint(rowIndex, columnIndex, absciss, nextRowOrdinate);
		if (super.parent.isCellSelected(rowIndex, columnIndex)) {
			paintSelectedCellBackground(graphics, rectangleToPaint);
			paintSelectedCellBorder(graphics, rectangleToPaint);
		} else {
			paintCellBorder(graphics, rectangleToPaint);
			paintCellBackground(graphics, rectangleToPaint);
		}
		paintCellValue(graphics, rectangleToPaint, rowIndex, columnIndex);
		return nextRowOrdinate + super.configurationContext.getCellHeight();
	}

	@Override
	protected void setSelectionAtPoint(Point point) {
		super.parent.setSelectionIsAdjusting(true);
		super.parent.unselectAll();
		addOrRemoveSelectionAtPoint(point);
		super.parent.setSelectionIsAdjusting(false);
	}

	@Override
	protected void selectAtPointIfNotSelected(Point point) {
		final int lineIndex = super.parent.getRowIndexAtOrdinate(point.y);
		final int columnIndex = super.parent.getColumnIndexAtAbsciss(point.x);
		if (!super.parent.isCellSelected(lineIndex, columnIndex)) {
			setSelectionAtPoint(point);
		}
	}

	@Override
	protected void addOrRemoveSelectionAtPoint(Point point) {
		final int lineIndex = super.parent.getRowIndexAtOrdinate(point.y);
		final int columnIndex = super.parent.getColumnIndexAtAbsciss(point.x);
		super.parent.changeSelectionStatusAt(lineIndex, columnIndex);
	}

	@Override
	protected void setSelectedValueAtPoint(Point point) {
		final int lineIndex = super.parent.getRowIndexAtOrdinate(point.y);
		final int columnIndex = super.parent.getColumnIndexAtAbsciss(point.x);
		final String cellLabel = getCellLabel(lineIndex, columnIndex);
		super.parent.setSelectedValue(cellLabel);
	}

	@Override
	public final CellInformation getCellInformation(int rowIndex, int columnIndex) {
		final F fieldForCurrentColumn = this.parent.getColumnDefinitionAt(columnIndex);
		final Serializable cellObject = this.parent.getModel().get(rowIndex).getValuesMap().get(fieldForCurrentColumn);
		return new CellInformation(cellObject);
	}
}
