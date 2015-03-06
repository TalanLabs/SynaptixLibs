package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation.timeline;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.helper.GraphicsHelper;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HeaderPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.writer.CellInformation;
import com.synaptix.widget.util.StaticWidgetHelper;

public class TimelineHeaderPanel<E extends IComponent, L extends IHierarchicalLine<E, LocalDate>> extends HeaderPanel<LocalDate, E, LocalDate, L> {

	private static final long serialVersionUID = 1972681792050551876L;

	private Border border = BorderFactory.createEtchedBorder();

	private final SubstanceColorScheme colorScheme;

	private int dayRowHeight;

	private int weekRowHeight;

	private int monthRowHeight;

	public TimelineHeaderPanel(final ConfigurationContext<E, LocalDate, L> dimensionProvider, final HierarchicalPanel<LocalDate, E, LocalDate, L> parent) {
		super(dimensionProvider, parent);
		this.colorScheme = SubstanceColorSchemeUtilities.getColorScheme(this, ColorSchemeAssociationKind.FILL, ComponentState.ENABLED);
	}

	@Override
	public void updateWidth(final int panelWidth) {
		super.updateWidth(panelWidth);
		this.dayRowHeight = this.getPreferredSize().height / 2;
		this.weekRowHeight = this.getPreferredSize().height / 4;
		this.monthRowHeight = this.getPreferredSize().height / 4;
	}

	@Override
	protected void paintContents(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		final Rectangle visibleRectangle = getVisibleRect();
		int firstColumn = super.parent.getColumnIndexAtAbsciss(visibleRectangle.x);
		int lastColumn = super.parent.getColumnIndexAtAbsciss(visibleRectangle.x + visibleRectangle.width);

		int nextRowOrdinate = paintMonths(firstColumn, lastColumn, g2);
		nextRowOrdinate = paintWeeks(firstColumn, lastColumn, g2, nextRowOrdinate);
		paintDays(firstColumn, lastColumn, g2, nextRowOrdinate);

		g2.dispose();
	}

	private void paintBackground(final Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(GraphicsHelper.buildVerticalGradientPaint(HeaderPanel.class.getName() + "_normal", this.getHeight(), this.configurationContext.getStandardColor(), this.configurationContext
				.getStandardColor().darker()));
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

	private int paintMonths(final int firstColumn, final int lastColumn, final Graphics2D graphics) {
		graphics.setFont(getMonthFont());
		for (int columnIndex = firstColumn; columnIndex <= lastColumn; columnIndex++) {
			int nextColumnAbsciss = super.parent.getColumnAbscissAt(columnIndex);
			final int firstGroupedColumnIndex = columnIndex;
			int groupmentSize = 1;
			final LocalDate date = super.parent.getColumnDefinitionAt(columnIndex);

			if (columnIndex + 1 <= lastColumn) {
				boolean hasMore = true;
				while (hasMore && compareMonthDateByIndex(columnIndex, columnIndex + 1)) {
					groupmentSize++;
					if (columnIndex + 1 <= lastColumn) {
						columnIndex++;
						hasMore = (columnIndex + 1) <= lastColumn;
					}
				}
			}
			final int columnWidth = super.parent.getColumnSizeAt(columnIndex);

			final Rectangle rectangleToPaint = new Rectangle(nextColumnAbsciss, 0, groupmentSize * columnWidth, this.monthRowHeight);

			if (checkColumnRangeSelection(firstGroupedColumnIndex, groupmentSize)) {
				paintSelectedCellBorder(graphics, rectangleToPaint);
				paintSelectedCellBackground(graphics, rectangleToPaint);
			} else {
				paintCellBackground(graphics, rectangleToPaint);
				this.border.paintBorder(this, graphics, nextColumnAbsciss, 0, groupmentSize * columnWidth, this.monthRowHeight);
			}

			graphics.setColor(this.colorScheme.getForegroundColor());
			GraphicsHelper.paintCenterString(graphics, date.toString(StaticWidgetHelper.getSynaptixDateConstantsBundle().hierarchicalMonthDisplay()), graphics.getFont(), rectangleToPaint.x,
					rectangleToPaint.y, rectangleToPaint.width, rectangleToPaint.height);
		}
		return this.monthRowHeight;
	}

	private boolean compareMonthDateByIndex(final int referenceDateIndex, final int comparedDateIndex) {
		final LocalDate date = super.parent.getColumnDefinitionAt(referenceDateIndex);
		final LocalDate nextDate = super.parent.getColumnDefinitionAt(comparedDateIndex);
		return date.getMonthOfYear() == nextDate.getMonthOfYear();
	}

	private int paintWeeks(final int firstColumn, final int lastColumn, final Graphics2D graphics, final int ordinate) {
		graphics.setFont(getWeekFont());
		for (int columnIndex = firstColumn; columnIndex <= lastColumn; columnIndex++) {
			int nextColumnAbsciss = super.parent.getColumnAbscissAt(columnIndex);
			final int firstGroupedColumnIndex = columnIndex;
			int groupmentSize = 1;
			final LocalDate date = super.parent.getColumnDefinitionAt(columnIndex);

			if (columnIndex + 1 <= lastColumn) {
				boolean hasMore = true;
				while (hasMore && compareWeekDateByIndex(columnIndex, columnIndex + 1)) {
					groupmentSize++;
					if (columnIndex + 1 <= lastColumn) {
						columnIndex++;
						hasMore = (columnIndex + 1) <= lastColumn;
					}
				}
			}
			final int columnWidth = super.parent.getColumnSizeAt(columnIndex);

			final Rectangle rectangleToPaint = new Rectangle(nextColumnAbsciss, ordinate, groupmentSize * columnWidth, this.weekRowHeight);
			if (checkColumnRangeSelection(firstGroupedColumnIndex, groupmentSize)) {
				paintSelectedCellBorder(graphics, rectangleToPaint);
				paintSelectedCellBackground(graphics, rectangleToPaint);
			} else {
				paintCellBackground(graphics, rectangleToPaint);
				this.border.paintBorder(this, graphics, nextColumnAbsciss, ordinate, groupmentSize * columnWidth, this.monthRowHeight);
			}

			graphics.setColor(this.colorScheme.getForegroundColor());
			GraphicsHelper.paintCenterString(graphics, StaticWidgetHelper.getSynaptixDateConstantsBundle().week(date.getWeekOfWeekyear()), graphics.getFont(), rectangleToPaint.x, rectangleToPaint.y,
					rectangleToPaint.width, rectangleToPaint.height);
		}
		return ordinate + this.weekRowHeight;
	}

	private boolean compareWeekDateByIndex(final int referenceDateIndex, final int comparedDateIndex) {
		final LocalDate date = super.parent.getColumnDefinitionAt(referenceDateIndex);
		final LocalDate nextDate = super.parent.getColumnDefinitionAt(comparedDateIndex);
		return date.getWeekOfWeekyear() == nextDate.getWeekOfWeekyear();
	}

	private void paintDays(final int firstColumn, final int lastColumn, final Graphics2D graphics, final int ordinate) {
		int nextColumnAbsciss = super.parent.getColumnAbscissAt(firstColumn);
		for (int i = firstColumn; i <= lastColumn; i++) {
			nextColumnAbsciss = paintDayCellAndReturnEndAbsciss(i, graphics, nextColumnAbsciss, ordinate);
		}
	}

	private int paintDayCellAndReturnEndAbsciss(final int columnIndex, final Graphics2D graphics, final int absciss, final int ordinate) {
		final int columnWidth = super.parent.getColumnSizeAt(columnIndex);

		final Rectangle rectangleToPaint = new Rectangle(absciss, ordinate, columnWidth, this.dayRowHeight);
		if (super.parent.isWholeColumnSelected(columnIndex)) {
			paintSelectedCellBackground(columnIndex, graphics, rectangleToPaint);
			paintSelectedCellBorder(graphics, rectangleToPaint);
		} else {
			paintCellBackground(columnIndex, graphics, rectangleToPaint);
			this.border.paintBorder(this, graphics, absciss, ordinate, columnWidth, this.dayRowHeight);
		}

		graphics.setColor(this.colorScheme.getForegroundColor());
		paintDayName(columnIndex, graphics, absciss, ordinate);
		paintDayNumber(columnIndex, graphics, absciss, ordinate);

		return absciss + columnWidth;
	}

	private void paintCellBackground(final int columnIndex, final Graphics2D graphics, final Rectangle rectangleToPaint) {
		final LocalDate date = super.parent.getColumnDefinitionAt(columnIndex);
		final boolean isWeekend = date.getDayOfWeek() == DateTimeConstants.SATURDAY || date.getDayOfWeek() == DateTimeConstants.SUNDAY;
		Paint paint;
		if (this.isHighlightable && rectangleToPaint.contains(this.mousePosition)) {
			if (isWeekend) {
				paint = GraphicsHelper.buildVerticalGradientPaint(TimelineHeaderPanel.class.getName() + "_highlighted_weekend", this.getHeight(),
						this.configurationContext.getHighlightedEmphaseStandardColor(), this.configurationContext.getHighlightedEmphaseStandardColor().darker());
			} else {
				paint = GraphicsHelper.buildVerticalGradientPaint(TimelineHeaderPanel.class.getName() + "_highlighted", this.getHeight(), this.configurationContext.getHighlightedStandardColor(),
						this.configurationContext.getHighlightedStandardColor().darker());
			}
		} else {
			if (isWeekend) {
				paint = GraphicsHelper.buildVerticalGradientPaint(TimelineHeaderPanel.class.getName() + "_weekend", this.getHeight(), this.configurationContext.getEmphaseStandardColor(),
						this.configurationContext.getEmphaseStandardColor().darker());
			} else {
				paint = GraphicsHelper.buildVerticalGradientPaint(TimelineHeaderPanel.class.getName() + "_normal", this.getHeight(), this.configurationContext.getStandardColor(),
						this.configurationContext.getStandardColor().darker());
			}
		}
		graphics.setPaint(paint);
		graphics.fillRect(rectangleToPaint.x + 1, rectangleToPaint.y + 1, rectangleToPaint.width - 2, rectangleToPaint.height - 2);
	}

	private void paintSelectedCellBackground(final int columnIndex, final Graphics2D graphics, final Rectangle rectangleToPaint) {
		final LocalDate date = super.parent.getColumnDefinitionAt(columnIndex);
		final boolean isWeekend = date.getDayOfWeek() == DateTimeConstants.SATURDAY || date.getDayOfWeek() == DateTimeConstants.SUNDAY;
		Paint paint;
		if (this.isHighlightable && rectangleToPaint.contains(this.mousePosition)) {
			if (isWeekend) {
				paint = GraphicsHelper.buildVerticalGradientPaint(TimelineHeaderPanel.class.getName() + "_selected_highlighted_weekend", this.getHeight(),
						this.configurationContext.getHighlightedEmphaseSelectionColor(), this.configurationContext.getHighlightedEmphaseSelectionColor().darker());
			} else {
				paint = GraphicsHelper.buildVerticalGradientPaint(TimelineHeaderPanel.class.getName() + "_selected_highlighted", this.getHeight(),
						this.configurationContext.getHighlightedSelectionColor(), this.configurationContext.getHighlightedSelectionColor().darker());
			}
		} else {
			if (isWeekend) {
				paint = GraphicsHelper.buildVerticalGradientPaint(TimelineHeaderPanel.class.getName() + "_selected_weekend", this.getHeight(), this.configurationContext.getEmphaseSelectionColor(),
						this.configurationContext.getEmphaseSelectionColor().darker());
			} else {
				paint = GraphicsHelper.buildVerticalGradientPaint(TimelineHeaderPanel.class.getName() + "_selected", this.getHeight(), this.configurationContext.getSelectionColor(),
						this.configurationContext.getSelectionColor().darker());
			}
		}
		graphics.setPaint(paint);
		graphics.fillRect(rectangleToPaint.x + 1, rectangleToPaint.y + 1, rectangleToPaint.width - 2, rectangleToPaint.height - 2);
	}

	private void paintDayName(final int columnIndex, final Graphics2D graphics, final int absciss, final int ordinate) {
		graphics.setFont(getDayNameFont());
		final LocalDate date = super.parent.getColumnDefinitionAt(columnIndex);
		final int columnWidth = super.parent.getColumnSizeAt(columnIndex);
		final String dayName = StaticWidgetHelper.getSynaptixDateConstantsBundle().shortWeekDays().get(String.valueOf(date.getDayOfWeek()));
		final Rectangle2D rect2 = graphics.getFont().getStringBounds(dayName, graphics.getFontRenderContext());
		final int textAbsciss = (int) (absciss + (columnWidth - rect2.getWidth()) / 2);
		final int textOrdinate = (int) (ordinate + this.dayRowHeight / 4 + rect2.getHeight() / 2);
		graphics.drawString(dayName, textAbsciss, textOrdinate);
	}

	private void paintDayNumber(final int columnIndex, final Graphics2D graphics, final int absciss, final int ordinate) {
		graphics.setFont(getDayNumberFont());
		final LocalDate date = super.parent.getColumnDefinitionAt(columnIndex);
		final int columnWidth = super.parent.getColumnSizeAt(columnIndex);
		final String dayNumer = String.valueOf(date.getDayOfMonth());
		final Rectangle2D rect2 = graphics.getFont().getStringBounds(dayNumer, graphics.getFontRenderContext());
		final int textAbsciss = (int) (absciss + (columnWidth - rect2.getWidth()) / 2);
		final int textOrdinate = (int) (ordinate + 3 * this.dayRowHeight / 4 + rect2.getHeight() / 2) - 3;
		graphics.drawString(dayNumer, textAbsciss, textOrdinate);
	}

	@Override
	protected void paintEmptyContents(Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		this.border.paintBorder(this, g2, 0, 0, this.getWidth(), this.getHeight());
		g2.dispose();
	}

	private boolean checkColumnRangeSelection(final int firstColumnIndex, final int groupmentSize) {
		boolean areAllColumnSelected = true;
		for (int columnIndex = firstColumnIndex; columnIndex < firstColumnIndex + groupmentSize; columnIndex++) {
			areAllColumnSelected = areAllColumnSelected && super.parent.isWholeColumnSelected(columnIndex);
		}
		return areAllColumnSelected;
	}

	@Override
	protected void setSelectionAtPoint(Point point) {
		super.parent.unselectAll();
		addOrRemoveSelectionAtPoint(point);
	}

	@Override
	protected void selectAtPointIfNotSelected(Point point) {
		final int columnIndex = super.parent.getColumnIndexAtAbsciss(point.x);
		if (!super.parent.isWholeColumnSelected(columnIndex)) {
			setSelectionAtPoint(point);
		}
	}

	@Override
	protected void addOrRemoveSelectionAtPoint(final Point point) {
		final int columnIndex = super.parent.getColumnIndexAtAbsciss(point.x);
		if (isSelectionOnMonth(point)) {
			final int selectionLowerBound = calculateLowerBoundIndexOfSelectionByMonth(columnIndex);
			final int selectionUpperBound = calculateUpperBoundIndexOfSelectionByMonth(columnIndex);
			super.parent.changeColumnSelectionStatusByRange(selectionLowerBound, selectionUpperBound);
		} else if (isSelectionOnWeek(point)) {
			final int selectionLowerBound = calculateLowerBoundIndexOfSelectionByWeek(columnIndex);
			final int selectionUpperBound = calculateUpperBoundIndexOfSelectionByWeek(columnIndex);
			super.parent.changeColumnSelectionStatusByRange(selectionLowerBound, selectionUpperBound);
		} else {
			super.parent.changeColumnSelectionStatusAt(columnIndex);
		}
	}

	private boolean isSelectionOnMonth(final Point point) {
		return point.y >= 0 && point.y <= this.monthRowHeight;
	}

	private boolean isSelectionOnWeek(final Point point) {
		return point.y > this.monthRowHeight && point.y <= this.monthRowHeight + this.weekRowHeight;
	}

	private int calculateLowerBoundIndexOfSelectionByWeek(final int columnIndex) {
		int lowerBound = columnIndex;
		boolean firstColumnFound = false;
		for (int modelColumnIndex = columnIndex - 1; modelColumnIndex >= 0 && !firstColumnFound; modelColumnIndex--) {
			if (compareWeekDateByIndex(columnIndex, modelColumnIndex)) {
				lowerBound = modelColumnIndex;
			} else {
				firstColumnFound = true;
			}
		}
		return lowerBound;
	}

	private int calculateUpperBoundIndexOfSelectionByWeek(final int columnIndex) {
		int upperBound = columnIndex;
		boolean lastColumnFound = false;
		for (int modelColumnIndex = columnIndex + 1; modelColumnIndex < super.parent.getColumnDefinitionList().size() && !lastColumnFound; modelColumnIndex++) {
			if (compareWeekDateByIndex(columnIndex, modelColumnIndex)) {
				upperBound = modelColumnIndex;
			} else {
				lastColumnFound = true;
			}
		}
		return upperBound;
	}

	private int calculateLowerBoundIndexOfSelectionByMonth(final int columnIndex) {
		int lowerBound = columnIndex;
		boolean firstColumnFound = false;
		for (int modelColumnIndex = columnIndex - 1; modelColumnIndex >= 0 && !firstColumnFound; modelColumnIndex--) {
			if (compareMonthDateByIndex(columnIndex, modelColumnIndex)) {
				lowerBound = modelColumnIndex;
			} else {
				firstColumnFound = true;
			}
		}
		return lowerBound;
	}

	private int calculateUpperBoundIndexOfSelectionByMonth(final int columnIndex) {
		int upperBound = columnIndex;
		boolean lastColumnFound = false;
		for (int modelColumnIndex = columnIndex + 1; modelColumnIndex < super.parent.getColumnDefinitionList().size() && !lastColumnFound; modelColumnIndex++) {
			if (compareMonthDateByIndex(columnIndex, modelColumnIndex)) {
				upperBound = modelColumnIndex;
			} else {
				lastColumnFound = true;
			}
		}
		return upperBound;
	}

	private Font getMonthFont() {
		return super.configurationContext.getSmallEmphaseFont();
	}

	private Font getWeekFont() {
		return super.configurationContext.getSmallFont();
	}

	private Font getDayNameFont() {
		return super.configurationContext.getSmallEmphaseFont();
	}

	private Font getDayNumberFont() {
		return super.configurationContext.getSmallFont();
	}

	@Override
	public int getExportHeight() {
		return 3;
	}

	@Override
	public CellInformation getCellInformation(int rowIndex, int columnIndex) {
		final LocalDate date = super.parent.getColumnDefinitionAt(columnIndex);
		Object obj = null;
		if (rowIndex == 0) {
			obj = date.toString(StaticWidgetHelper.getSynaptixDateConstantsBundle().hierarchicalMonthDisplay());
		} else if (rowIndex == 1) {
			obj = StaticWidgetHelper.getSynaptixDateConstantsBundle().week(date.getWeekOfWeekyear());
		} else if (rowIndex == 2) {
			obj = StaticWidgetHelper.getSynaptixDateConstantsBundle().shortWeekDays().get(String.valueOf(date.getDayOfWeek())) + String.valueOf(date.getDayOfMonth());
		}
		return new CellInformation(obj);
	}
}
