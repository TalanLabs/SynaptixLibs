package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation.timeline;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.implementation.ValueContentPanel;

public class TimelineContentPanel<E extends IComponent, L extends IHierarchicalLine<E, LocalDate>> extends ValueContentPanel<E, LocalDate, L> {

	private static final long serialVersionUID = -6342403668329347646L;

	public TimelineContentPanel(final ConfigurationContext<E, LocalDate, L> configurationContext, final HierarchicalPanel<LocalDate, E, LocalDate, L> parent) {
		super(configurationContext, parent);
	}

	@Override
	protected void paintContents(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		g2.setFont(super.configurationContext.getSmallEmphaseFont());

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
		paintLeftBorderIfEndOfWeek(columnIndex, graphics, rectangleToPaint);
		if (super.parent.isCellSelected(rowIndex, columnIndex)) {
			paintSelectedCellBackground(columnIndex, graphics, rectangleToPaint);
			paintSelectedCellBorder(graphics, rectangleToPaint);
		} else {
			paintCellBackground(columnIndex, graphics, rectangleToPaint);
			paintCellBorder(graphics, rectangleToPaint);
		}
		paintCellValue(graphics, rectangleToPaint, rowIndex, columnIndex);
		return nextRowOrdinate + super.configurationContext.getCellHeight();
	}

	private void paintCellBackground(final int columnIndex, final Graphics2D graphics, final Rectangle rectangleToPaint) {
		final LocalDate date = super.parent.getColumnDefinitionAt(columnIndex);
		final boolean isWeekend = date.getDayOfWeek() == DateTimeConstants.SATURDAY || date.getDayOfWeek() == DateTimeConstants.SUNDAY;
		Color backgroundColor;
		if (this.mousePosition != null && super.getHoverCell() != null && rectangleToPaint.contains(this.mousePosition)) {
			if (isWeekend) {
				backgroundColor = this.configurationContext.getHighlightedEmphaseStandardColor();
			} else {
				backgroundColor = this.configurationContext.getHighlightedStandardColor();
			}
		} else {
			if (isWeekend) {
				backgroundColor = this.configurationContext.getEmphaseStandardColor();
			} else {
				backgroundColor = this.configurationContext.getStandardColor();
			}
		}
		graphics.setColor(backgroundColor);
		graphics.fillRect(rectangleToPaint.x + 1, rectangleToPaint.y + 1, rectangleToPaint.width - 2, rectangleToPaint.height - 2);
	}

	private void paintSelectedCellBackground(final int columnIndex, final Graphics2D graphics, final Rectangle rectangleToPaint) {
		final LocalDate date = super.parent.getColumnDefinitionAt(columnIndex);
		final boolean isWeekend = date.getDayOfWeek() == DateTimeConstants.SATURDAY || date.getDayOfWeek() == DateTimeConstants.SUNDAY;
		Color backgroundColor;
		if (this.mousePosition != null && super.getHoverCell() != null && rectangleToPaint.contains(this.mousePosition)) {
			if (isWeekend) {
				backgroundColor = this.configurationContext.getHighlightedEmphaseSelectionColor();
			} else {
				backgroundColor = this.configurationContext.getHighlightedSelectionColor();
			}
		} else {
			if (isWeekend) {
				backgroundColor = this.configurationContext.getEmphaseSelectionColor();
			} else {
				backgroundColor = this.configurationContext.getSelectionColor();
			}
		}
		graphics.setColor(backgroundColor);
		graphics.fillRect(rectangleToPaint.x + 1, rectangleToPaint.y + 1, rectangleToPaint.width - 2, rectangleToPaint.height - 2);
	}

	private void paintLeftBorderIfEndOfWeek(final int columnIndex, final Graphics2D graphics, final Rectangle rectangleToPaint) {
		if (columnIndex + 1 < super.parent.getColumnDefinitionList().size()) {
			final LocalDate date = super.parent.getColumnDefinitionAt(columnIndex);
			final LocalDate nextDate = super.parent.getColumnDefinitionAt(columnIndex + 1);
			if (date.getWeekOfWeekyear() != nextDate.getWeekOfWeekyear()) {
				graphics.setColor(this.colorScheme.getDarkColor().darker());
				graphics.fillRect(rectangleToPaint.x + rectangleToPaint.width - 1, rectangleToPaint.y, 1, rectangleToPaint.height);

			}
		}
	}
}
