package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.HierarchicalPanelKind;
import com.synaptix.widget.hierarchical.view.swing.component.panel.ContentPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.view.swing.component.rule.HierarchicalSummaryRule;
import com.synaptix.widget.hierarchical.writer.CellInformation;

public class SummaryContentPanel<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends ContentPanel<HierarchicalSummaryRule<E, F, L>, E, F, L> {

	private static final long serialVersionUID = 7550109793418391179L;

	private final Map<HierarchicalSummaryRule<E, F, L>, List<Number>> summariesByRule;

	public SummaryContentPanel(final ConfigurationContext<E, F, L> configurationContext, final HierarchicalPanel<HierarchicalSummaryRule<E, F, L>, E, F, L> parent) {
		super(configurationContext, parent);
		this.summariesByRule = new HashMap<HierarchicalSummaryRule<E, F, L>, List<Number>>();
	}

	@Override
	protected HierarchicalPanelKind getPanelKind() {
		return HierarchicalPanelKind.SUMMARY;
	}

	@Override
	protected void computeContents(final List<L> model) {
		this.summariesByRule.clear();
		int columnIndex = 0;
		for (final HierarchicalSummaryRule<E, F, L> rule : super.parent.getColumnDefinitionList()) {
			this.summariesByRule.put(rule, computeSummaryForRule(rule, model, columnIndex));
			columnIndex++;
		}
	}

	private List<Number> computeSummaryForRule(final HierarchicalSummaryRule<E, F, L> rule, final List<L> model, int columnIndex) {
		final List<Number> summary = new ArrayList<Number>();
		if (CollectionHelper.isNotEmpty(model)) {
			for (int rowIndex = 0; rowIndex < model.size(); rowIndex++) {
				// summary.add(0);
				L line = model.get(rowIndex);
				List<L> lines = new ArrayList<L>();
				lines.add(line);
				int verticalCellSpan = rule.getVerticalCellSpan(rowIndex);
				for (int i = 1; i < verticalCellSpan; i++) {
					line = model.get(++rowIndex);
					lines.add(line);
				}
				Number value = rule.getTotalValue(lines);
				for (int i = 0; i < verticalCellSpan; i++) {
					summary.add(value);
				}
			}
		}
		return summary;
	}

	@Override
	protected void paintContents(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);

		g2.setFont(super.configurationContext.getSmallFont());
		int nextColumnAbsciss = 0;

		for (int i = 0; i < super.parent.getColumnDefinitionList().size(); i++) {
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

		HierarchicalSummaryRule<E, F, L> columnDefinition = this.parent.getColumnDefinitionAt(columnIndex);

		for (int rowIndex = firstLine; rowIndex <= lastLine; rowIndex++) {
			nextRowOrdinate = paintCell(rowIndex, columnIndex, graphics, absciss, nextRowOrdinate);
			rowIndex += columnDefinition.getVerticalCellSpan(rowIndex) - 1;
		}
		return absciss + columnWidth;
	}

	private int paintCell(final int rowIndex, final int columnIndex, final Graphics2D graphics, final int absciss, final int nextRowOrdinate) {
		final Rectangle rectangleToPaint = buildRectangleToPaint(rowIndex, columnIndex, absciss, nextRowOrdinate);
		paintCellBorderAndBackground(graphics, rectangleToPaint);
		paintCellValue(graphics, rectangleToPaint, rowIndex, columnIndex);
		return nextRowOrdinate + rectangleToPaint.height;
	}

	@Override
	protected String getCellLabel(int rowIndex, int columnIndex) {
		final HierarchicalSummaryRule<E, F, L> rule = super.parent.getColumnDefinitionAt(columnIndex);
		final Number cellObject = this.summariesByRule.get(rule).get(rowIndex);
		final String cellLabel = super.parent.getStringFromObject(cellObject);
		return cellLabel;
	}

	protected void paintCellBorderAndBackground(final Graphics2D graphics, final Rectangle rectangleToPaint) {
		graphics.setColor(this.colorScheme.getDarkColor());
		graphics.drawRect(rectangleToPaint.x, rectangleToPaint.y, rectangleToPaint.width, rectangleToPaint.height);
	}

	@Override
	protected Rectangle buildRectangleToPaint(final int rowIndex, final int columnIndex, final int absciss, final int nextRowOrdinate) {
		HierarchicalSummaryRule<E, F, L> columnDefinition = this.parent.getColumnDefinitionAt(columnIndex);
		int columnWidth = this.parent.getColumnSizeAt(columnIndex);
		final Rectangle rectangleToPaint = new Rectangle(absciss, nextRowOrdinate, columnWidth, this.configurationContext.getCellHeight() * columnDefinition.getVerticalCellSpan(rowIndex));
		return rectangleToPaint;
	}

	@Override
	protected void setSelectionAtPoint(Point point) {
	}

	@Override
	protected void addOrRemoveSelectionAtPoint(Point point) {
	}

	@Override
	protected void setSelectedValueAtPoint(Point point) {
	}

	@Override
	protected void selectAtPointIfNotSelected(Point point) {
	}

	@Override
	public CellInformation getCellInformation(int rowIndex, int columnIndex) {
		final HierarchicalSummaryRule<E, F, L> rule = super.parent.getColumnDefinitionAt(columnIndex);
		final Number cellObject = this.summariesByRule.get(rule).get(rowIndex);
		return new CellInformation(cellObject, 1, rule.getVerticalCellSpan(rowIndex));
	}
}
