package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation.timeline;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.helper.GraphicsHelper;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.implementation.ValueFooterPanel;

public class TimelineFooterPanel<E extends IComponent, L extends IHierarchicalLine<E, LocalDate>> extends ValueFooterPanel<E, LocalDate, L> {

	private static final long serialVersionUID = 5418600287988126467L;

	public TimelineFooterPanel(ConfigurationContext<E, LocalDate, L> configurationContext, HierarchicalPanel<LocalDate, E, LocalDate, L> parent) {
		super(configurationContext, parent);
	}

	@Override
	protected int paintColumnAndReturnEndAbsciss(final int columnIndex, final Graphics2D graphics, final int absciss) {
		final String cellLabel = getCellLabel(columnIndex);
		final Rectangle rectangleToPaint = buildRectangleToPaint(columnIndex, absciss);
		paintCellBackground(columnIndex, graphics, rectangleToPaint);
		paintCellBorder(graphics, rectangleToPaint);
		paintLeftBorderIfEndOfWeek(columnIndex, graphics, rectangleToPaint);
		paintCellValue(graphics, rectangleToPaint, cellLabel);
		return absciss + rectangleToPaint.width;
	}

	private void paintCellBackground(final int columnIndex, final Graphics2D graphics, final Rectangle rectangleToPaint) {
		final LocalDate date = super.parent.getColumnDefinitionAt(columnIndex);
		if (date.getDayOfWeek() == DateTimeConstants.SATURDAY || date.getDayOfWeek() == DateTimeConstants.SUNDAY) {
			graphics.setPaint(GraphicsHelper.buildVerticalGradientPaint(TimelineFooterPanel.class.getName() + "_day_weekEnd", super.configurationContext.getCellHeight(),
					super.colorScheme.getLightColor(), super.colorScheme.getMidColor()));

		} else {
			graphics.setPaint(GraphicsHelper.buildVerticalGradientPaint(TimelineFooterPanel.class.getName() + "_day_normal", super.configurationContext.getCellHeight(),
					super.colorScheme.getUltraLightColor(), super.colorScheme.getLightColor()));
		}

		graphics.fillRect(rectangleToPaint.x, rectangleToPaint.y, rectangleToPaint.width, rectangleToPaint.height);
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
