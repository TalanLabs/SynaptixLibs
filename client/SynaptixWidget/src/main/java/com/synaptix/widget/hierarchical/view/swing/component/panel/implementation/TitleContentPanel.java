package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.component.field.IField;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.HierarchicalPanelKind;
import com.synaptix.widget.hierarchical.view.swing.component.panel.ContentPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.writer.CellInformation;

public class TitleContentPanel<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends ContentPanel<IField, E, F, L> {

	private static final long serialVersionUID = 994189423703773680L;

	private final Map<IHierarchicalCell, Integer> regroupMap;

	public TitleContentPanel(final ConfigurationContext<E, F, L> configurationContext, final HierarchicalPanel<IField, E, F, L> parent) {
		super(configurationContext, parent);

		this.regroupMap = new HashMap<IHierarchicalCell, Integer>();
	}

	@Override
	protected HierarchicalPanelKind getPanelKind() {
		return HierarchicalPanelKind.TITLE;
	}

	@Override
	protected void paintContents(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		g2.setFont(super.configurationContext.getSmallFont());
		g2.setClip(this.getVisibleRect());

		int nextColumnAbsciss = 0;

		for (int i = 0; i < super.parent.getColumnDefinitionList().size(); i++) {
			nextColumnAbsciss = paintColumnAndReturnEndAbsciss(i, g2, nextColumnAbsciss);
		}

		g2.dispose();
	}

	private int paintColumnAndReturnEndAbsciss(final int columnIndex, final Graphics2D graphics, final int absciss) {
		final int columnWidth = getColumnWidth(columnIndex);
		if (super.configurationContext.isShouldRegroupModel()) {
			paintColumnWithRegroupment(columnIndex, graphics, absciss);
		} else {
			paintColumnWithoutRegroupment(columnIndex, graphics, absciss);
		}
		return absciss + columnWidth;
	}

	@Override
	protected void computeContents(List<L> model) {
		super.computeContents(model);

		this.regroupMap.clear();

		if (CollectionHelper.isNotEmpty(model)) {

			int lastLine = model.size();
			for (int columnIndex = 0; columnIndex < super.parent.getColumnDefinitionList().size(); columnIndex++) {
				for (int rowIndex = 0; rowIndex < lastLine - 1; rowIndex++) {
					Integer cellGroupmentSize = 1;
					boolean hasMore = true;
					int initRow = rowIndex;
					IHierarchicalCell cell = new HierarchicalCellBuilder().rowIndex(rowIndex).columnIndex(columnIndex).build();
					while (hasMore && compareCellObjectToNthDeepth(rowIndex, rowIndex + 1, columnIndex)) {
						cellGroupmentSize++;
						if (rowIndex + 1 <= lastLine) {
							rowIndex++;
							hasMore = (rowIndex + 1) <= lastLine - 1;
						}
					}
					for (int x = 1; x < cellGroupmentSize; x++) {
						IHierarchicalCell cell2 = new HierarchicalCellBuilder().rowIndex(initRow + x).columnIndex(columnIndex).build();
						regroupMap.put(cell2, cellGroupmentSize - x);
					}
					regroupMap.put(cell, cellGroupmentSize);
				}
			}
		}
	}

	private void paintColumnWithRegroupment(final int columnIndex, final Graphics2D graphics, final int absciss) {
		final Rectangle visibleRectangle = getVisibleRect();
		int firstLine = super.parent.getRowIndexAtOrdinate(visibleRectangle.y);
		int lastLine = super.parent.getRowIndexAtOrdinate(visibleRectangle.y + visibleRectangle.height - 1);
		int nextRowOrdinate = super.parent.getLineOrdinateAt(firstLine);

		for (int rowIndex = firstLine; rowIndex <= lastLine; rowIndex++) {

			final int firstGroupedLineRowIndex = rowIndex;
			// final IField fieldForCurrentColumn = super.parent.getColumnDefinitionAt(columnIndex);
			// final IComponent titleComponent = super.parent.getModel().get(rowIndex).getTitleComponent();
			// final Object cellObject = titleComponent.straightGetProperty(fieldForCurrentColumn.name());
			// final String cellLabel = super.parent.getStringFromObject(cellObject);

			IHierarchicalCell cell = new HierarchicalCellBuilder().rowIndex(rowIndex).columnIndex(columnIndex).build();
			Integer cellGroupmentSize = regroupMap.get(cell);
			if (cellGroupmentSize == null) {
				cellGroupmentSize = 1;
			}

			final Rectangle rectangleToPaint = buildRectangleToPaintWithGroupment(rowIndex, columnIndex, absciss, nextRowOrdinate, cellGroupmentSize);
			if (rectangleToPaint.y + rectangleToPaint.height > graphics.getClipBounds().height + graphics.getClipBounds().y) {
				rectangleToPaint.height = Math.max(graphics.getClipBounds().height + graphics.getClipBounds().y - rectangleToPaint.y, this.configurationContext.getCellHeight());
			}
			rowIndex += cellGroupmentSize - 1;

			if (isRowRangeSelected(firstGroupedLineRowIndex, cellGroupmentSize)) {
				paintSelectedCellBorder(graphics, rectangleToPaint);
				paintSelectedCellBackground(graphics, rectangleToPaint);
			} else {
				paintCellBorder(graphics, rectangleToPaint);
				paintCellBackground(graphics, rectangleToPaint);
			}

			// graphics.setColor(this.colorScheme.getForegroundColor());
			// GraphicsHelper.paintCenterString(graphics, cellLabel, graphics.getFont(), rectangleToPaint.x, rectangleToPaint.y, rectangleToPaint.width, rectangleToPaint.height);
			//
			// performSpecialRenderingForList(graphics, rectangleToPaint, firstGroupedLineRowIndex, cellGroupmentSize, fieldForCurrentColumn);

			paintCellValueForList(graphics, rectangleToPaint, firstGroupedLineRowIndex, cellGroupmentSize, columnIndex);
			// paintCellValue(graphics, rectangleToPaint, firstGroupedLineRowIndex, columnIndex);

			nextRowOrdinate += cellGroupmentSize * super.configurationContext.getCellHeight();
		}
	}

	@Override
	protected String getCellLabel(final int rowIndex, final int columnIndex) {
		final IField fieldForCurrentColumn = super.parent.getColumnDefinitionAt(columnIndex);
		final IComponent titleComponent = super.parent.getModel().get(rowIndex).getTitleComponent();
		final Object cellObject = ComponentHelper.getValue(titleComponent, fieldForCurrentColumn.name());
		final String cellLabel = super.parent.getStringFromObject(cellObject);
		return cellLabel;
	}

	protected void paintCellValueForList(final Graphics2D graphics, final Rectangle rectangleToPaint, final int firstRowIndex, final int numberOfRows, final int columnIndex) {
		if (this.parent.getCellRenderer() != null) {
			final IField fieldForCurrentColumn = super.parent.getColumnDefinitionAt(columnIndex);
			final Graphics2D restrictedGraphics = (Graphics2D) graphics.create(rectangleToPaint.x, rectangleToPaint.y, rectangleToPaint.width, rectangleToPaint.height);
			final List<L> rowList = getModelElementsWithinBounds(firstRowIndex, numberOfRows);
			super.parent.getCellRenderer().renderForList(restrictedGraphics, rowList, fieldForCurrentColumn);
		} else {
			performDefaultRendering(graphics, rectangleToPaint, firstRowIndex, columnIndex);
		}
	}

	// private void performSpecialRenderingForList(final Graphics2D graphics, final Rectangle rectangleToPaint, final int firstRowIndex, final int numberOfRows, final Object column) {
	// if (super.parent.getCellRenderer() != null) {
	// final Graphics2D restrictedGraphics = (Graphics2D) graphics.create(rectangleToPaint.x, rectangleToPaint.y, rectangleToPaint.width, rectangleToPaint.height);
	// final List<L> rowList = getModelElementsWithinBounds(firstRowIndex, numberOfRows);
	// super.parent.getCellRenderer().renderForList(restrictedGraphics, rowList, column);
	// }
	// }

	private List<L> getModelElementsWithinBounds(final int lowerBound, final int numberOfRows) {
		final List<L> rowList = new ArrayList<L>();
		for (int rowIndex = lowerBound; rowIndex < lowerBound + numberOfRows; rowIndex++) {
			rowList.add(super.parent.getModel().get(rowIndex));
		}
		return rowList;
	}

	private boolean isRowRangeSelected(final int firstRowIndex, final int groupmentSize) {
		boolean areAllRowSelected = true;
		for (int rowIndex = firstRowIndex; rowIndex < firstRowIndex + groupmentSize; rowIndex++) {
			areAllRowSelected = areAllRowSelected && super.parent.isWholeLineSelected(rowIndex);
		}
		return areAllRowSelected;
	}

	@SuppressWarnings("unchecked")
	private <T> boolean compareCellObjectToNthDeepth(final int referenceCellObjectIndex, final int comparedCellObjectIndex, final int maximumDepthOfComparison) {
		boolean isEqual = true;
		final IComponent titleComponent = super.parent.getModel().get(referenceCellObjectIndex).getTitleComponent();
		final IComponent nextTitleComponent = super.parent.getModel().get(comparedCellObjectIndex).getTitleComponent();
		for (int columnIndex = 0; columnIndex <= maximumDepthOfComparison && isEqual; columnIndex++) {
			final IField fieldForCurrentColumn = super.parent.getColumnDefinitionAt(columnIndex);
			final T cellObject = (T) ComponentHelper.getValue(titleComponent, fieldForCurrentColumn.name());
			final T nextCellObject = (T) ComponentHelper.getValue(nextTitleComponent, fieldForCurrentColumn.name());
			final Comparator<T> comparator = (Comparator<T>) super.configurationContext.getComparatorForField(fieldForCurrentColumn);
			if (comparator != null) {
				isEqual = isEqual && (comparator.compare(cellObject, nextCellObject) == 0);
			} else {
				isEqual = isEqual && areEqualsOrNulls(cellObject, nextCellObject);
			}
		}
		return isEqual;
	}

	private boolean areEqualsOrNulls(final Object comparedObject1, final Object comparedObject2) {
		if (comparedObject1 == null) {
			return comparedObject2 == null;
		}
		if (comparedObject2 == null) {
			return comparedObject1 == null;
		}
		return comparedObject1.equals(comparedObject2);
	}

	private Rectangle buildRectangleToPaintWithGroupment(final int rowIndex, final int columnIndex, final int absciss, final int nextRowOrdinate, final int groupmentSize) {
		final Rectangle rectangleToPaint = buildRectangleToPaint(rowIndex, columnIndex, absciss, nextRowOrdinate);
		rectangleToPaint.height = rectangleToPaint.height * groupmentSize;
		return rectangleToPaint;
	}

	private void paintColumnWithoutRegroupment(final int columnIndex, final Graphics2D graphics, final int absciss) {
		final Rectangle visibleRectangle = getVisibleRect();
		int firstLine = super.parent.getRowIndexAtOrdinate(visibleRectangle.y);
		int lastLine = super.parent.getRowIndexAtOrdinate(visibleRectangle.y + visibleRectangle.height - 1);
		int nextRowOrdinate = super.parent.getLineOrdinateAt(firstLine);

		for (int rowIndex = firstLine; rowIndex <= lastLine; rowIndex++) {
			nextRowOrdinate = paintCellWithoutRegroupment(rowIndex, columnIndex, graphics, absciss, nextRowOrdinate);
		}
	}

	private int paintCellWithoutRegroupment(final int rowIndex, final int columnIndex, final Graphics2D graphics, final int absciss, final int nextRowOrdinate) {
		// final IField fieldForCurrentColumn = super.parent.getColumnDefinitionAt(columnIndex);
		// final int columnWidth = super.parent.getColumnSizeAt(columnIndex);
		// final L modelElement = super.parent.getModel().get(rowIndex);
		// final E titleComponent = modelElement.getTitleComponent();
		// final Object cellObject = titleComponent.straightGetProperty(fieldForCurrentColumn.name());
		// final String cellLabel = super.parent.getStringFromObject(cellObject);

		final Rectangle rectangleToPaint = buildRectangleToPaint(rowIndex, columnIndex, absciss, nextRowOrdinate);

		if (super.parent.isWholeLineSelected(rowIndex)) {
			paintSelectedCellBorder(graphics, rectangleToPaint);
			paintSelectedCellBackground(graphics, rectangleToPaint);
		} else {
			paintCellBorder(graphics, rectangleToPaint);
			paintCellBackground(graphics, rectangleToPaint);

		}

		// graphics.setColor(this.colorScheme.getForegroundColor());
		// GraphicsHelper.paintCenterString(graphics, cellLabel, graphics.getFont(), absciss, nextRowOrdinate, columnWidth, super.configurationContext.getCellHeight());
		// performSpecialRenderingForElement(graphics, rectangleToPaint, modelElement, fieldForCurrentColumn);

		paintCellValue(graphics, rectangleToPaint, rowIndex, columnIndex);

		return nextRowOrdinate + super.configurationContext.getCellHeight();
	}

	// private void performSpecialRenderingForElement(final Graphics2D graphics, final Rectangle rectangleToPaint, final L row, final Object column) {
	// if (super.parent.getCellRenderer() != null) {
	// final Graphics2D restrictedGraphics = (Graphics2D) graphics.create(rectangleToPaint.x, rectangleToPaint.y, rectangleToPaint.width, rectangleToPaint.height);
	// super.parent.getCellRenderer().renderForElement(restrictedGraphics, row, column);
	// }
	// }

	@Override
	protected void setSelectionAtPoint(Point point) {
		super.parent.unselectAll();
		addOrRemoveSelectionAtPoint(point);
	}

	@Override
	protected void selectAtPointIfNotSelected(Point point) {
		final int lineIndex = super.parent.getRowIndexAtOrdinate(point.y);
		if (!super.parent.isWholeLineSelected(lineIndex)) {
			setSelectionAtPoint(point);
		}
	}

	@Override
	protected void addOrRemoveSelectionAtPoint(Point point) {
		final int lineIndex = super.parent.getRowIndexAtOrdinate(point.y);
		final int columnIndex = super.parent.getColumnIndexAtAbsciss(point.x);
		if (super.configurationContext.isShouldRegroupModel()) {
			final int selectionLowerBound = calculateLowerBoundIndexOfSelection(lineIndex, columnIndex);
			final int selectionUpperBound = calculateUpperBoundIndexOfSelection(lineIndex, columnIndex);
			super.parent.changeRowSelectionStatusByRange(selectionLowerBound, selectionUpperBound);
		} else {
			super.parent.changeRowSelectionStatusAt(lineIndex);
		}
	}

	private int calculateLowerBoundIndexOfSelection(final int lineIndex, final int columnIndex) {
		int lowerBound = lineIndex;
		boolean firstLineFound = false;
		for (int modelRowIndex = lineIndex - 1; modelRowIndex >= 0 && !firstLineFound; modelRowIndex--) {
			if (compareCellObjectToNthDeepth(lineIndex, modelRowIndex, columnIndex)) {
				lowerBound = modelRowIndex;
			} else {
				firstLineFound = true;
			}
		}
		return lowerBound;
	}

	private int calculateUpperBoundIndexOfSelection(final int lineIndex, final int columnIndex) {
		int upperBound = lineIndex;
		boolean lastLineFound = false;
		for (int modelRowIndex = lineIndex + 1; modelRowIndex < super.parent.getModelSize() && !lastLineFound; modelRowIndex++) {
			if (compareCellObjectToNthDeepth(lineIndex, modelRowIndex, columnIndex)) {
				upperBound = modelRowIndex;
			} else {
				lastLineFound = true;
			}
		}
		return upperBound;
	}

	@Override
	protected void setSelectedValueAtPoint(Point point) {
		final int lineIndex = super.parent.getRowIndexAtOrdinate(point.y);
		if (lineIndex >= 0) {
			final int columnIndex = super.parent.getColumnIndexAtAbsciss(point.x);
			final IField fieldForCurrentColumn = super.parent.getColumnDefinitionAt(columnIndex);
			final E titleComponent = super.parent.getModel().get(lineIndex).getTitleComponent();
			final Object cellObject = ComponentHelper.getValue(titleComponent, fieldForCurrentColumn.name());
			final String cellLabel = super.parent.getStringFromObject(cellObject);
			super.parent.setSelectedValue(cellLabel);
		}
	}

	@Override
	public int getVerticalCellSpan(int rowIndex, int columnIndex) {
		IHierarchicalCell cell = new HierarchicalCellBuilder().rowIndex(rowIndex).columnIndex(columnIndex).build();
		Integer cellGroupmentSize = regroupMap.get(cell);
		return cellGroupmentSize != null ? cellGroupmentSize : 1;
	}

	@Override
	protected Object getHoverObject(int rowIndex, int columnIndex) {
		// E titleComponent = parent.getModel().get(rowIndex).getTitleComponent();
		// ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(titleComponent);
		// @SuppressWarnings("unchecked")
		// E ent = (E) ComponentFactory.getInstance().createInstance(cd.getComponentClass());
		// for (int i = 0; i <= columnIndex; i++) {
		// String propertyName = parent.getColumnDefinitionAt(i).name();
		// ent.straightSetProperty(propertyName, titleComponent.straightGetProperty(propertyName));
		// }
		return parent.getModel().get(rowIndex).getTitleComponent();
	}

	@Override
	public CellInformation getCellInformation(int rowIndex, int columnIndex) {
		final IField fieldForCurrentColumn = super.parent.getColumnDefinitionAt(columnIndex);
		final IComponent titleComponent = super.parent.getModel().get(rowIndex).getTitleComponent();
		final Object cellObject = ComponentHelper.getValue(titleComponent, fieldForCurrentColumn.name());
		// IHierarchicalCell cell = new HierarchicalCellBuilder().rowIndex(rowIndex).columnIndex(columnIndex).build();
		// Integer verticalCellSpan = regroupMap.get(cell);
		// return new CellInformation(cellObject, 1, verticalCellSpan != null ? verticalCellSpan : 1);
		// problems with auto-filter and blanks
		return new CellInformation(cellObject, 1);
	}

	@SynaptixComponent
	public interface IHierarchicalCell extends IComponent {

		@EqualsKey
		public int getRowIndex();

		public void setRowIndex(int rowIndex);

		@EqualsKey
		public int getColumnIndex();

		public void setColumnIndex(int columnIndex);
	}
}
