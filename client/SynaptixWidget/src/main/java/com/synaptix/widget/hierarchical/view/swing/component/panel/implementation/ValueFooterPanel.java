package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;

import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.helper.GraphicsHelper;
import com.synaptix.widget.hierarchical.view.swing.component.panel.FooterPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.writer.CellInformation;

public class ValueFooterPanel<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends FooterPanel<F, E, F, L> {

	private static final long serialVersionUID = -2685742573367587224L;

	private final Map<F, Number> totalByColumn;

	protected final SubstanceColorScheme colorScheme;

	public ValueFooterPanel(final ConfigurationContext<E, F, L> configurationContext, final HierarchicalPanel<F, E, F, L> parent) {
		super(configurationContext, parent);
		this.totalByColumn = new HashMap<F, Number>();
		this.colorScheme = SubstanceColorSchemeUtilities.getColorScheme(this, ColorSchemeAssociationKind.FILL, ComponentState.ENABLED);
	}

	@Override
	protected void computeContents(List<L> model) {
		this.totalByColumn.clear();
		if (CollectionHelper.isNotEmpty(model)) {
			for (final L line : model) {
				final Map<F, Serializable> valuesMap = line.getValuesMap();
				for (final F column : valuesMap.keySet()) {
					final Serializable cellValue = valuesMap.get(column);
					if (isSummableValue(cellValue)) {
						final Number numericCellvalue = (Number) cellValue;
						Number currentSubTotal = this.totalByColumn.get(column);
						if (currentSubTotal == null) {
							currentSubTotal = numericCellvalue;
						} else {
							if ((currentSubTotal instanceof Integer) && (numericCellvalue instanceof Integer)) {
								currentSubTotal = (Integer) currentSubTotal + (Integer) numericCellvalue;
							} else if ((currentSubTotal instanceof BigInteger) && (numericCellvalue instanceof BigInteger)) {
								currentSubTotal = ((BigInteger) currentSubTotal).add((BigInteger) numericCellvalue);
							} else {
								currentSubTotal = currentSubTotal.doubleValue() + numericCellvalue.doubleValue();
							}
						}
						this.totalByColumn.put(column, currentSubTotal);
					}
				}
			}
		}
	}

	private boolean isSummableValue(final Serializable value) {
		return value != null && Number.class.isAssignableFrom(value.getClass());
	}

	@Override
	protected void paintContents(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		g2.setFont(super.parent.getColumnTitleFont());

		final Rectangle visibleRectangle = getVisibleRect();
		int firstColumn = super.parent.getColumnIndexAtAbsciss(visibleRectangle.x);
		int lastColumn = super.parent.getColumnIndexAtAbsciss(visibleRectangle.x + visibleRectangle.width);
		int nextColumnAbsciss = super.parent.getColumnAbscissAt(firstColumn);

		for (int i = firstColumn; i <= lastColumn; i++) {
			nextColumnAbsciss = paintColumnAndReturnEndAbsciss(i, g2, nextColumnAbsciss);
		}
		g2.dispose();
	}

	protected void paintBackground(final Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(GraphicsHelper.buildVerticalGradientPaint(ValueHeaderPanel.class.getName(), this.getHeight(), this.colorScheme.getUltraLightColor(), this.colorScheme.getLightColor()));
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

	protected int paintColumnAndReturnEndAbsciss(final int columnIndex, final Graphics2D graphics, final int absciss) {
		final String cellLabel = getCellLabel(columnIndex);
		final Rectangle rectangleToPaint = buildRectangleToPaint(columnIndex, absciss);
		paintCellBorder(graphics, rectangleToPaint);
		paintCellValue(graphics, rectangleToPaint, cellLabel);
		return absciss + rectangleToPaint.width;
	}

	protected Rectangle buildRectangleToPaint(final int columnIndex, final int absciss) {
		int columnWidth = this.parent.getColumnSizeAt(columnIndex);
		if (this.parent.isLastColumn(columnIndex)) {
			columnWidth -= 2;
		}
		final Rectangle rectangleToPaint = new Rectangle(absciss, 0, columnWidth, this.configurationContext.getCellHeight());
		return rectangleToPaint;
	}

	protected void paintCellBorder(final Graphics2D graphics, final Rectangle rectangleToPaint) {
		graphics.setColor(this.colorScheme.getDarkColor());
		graphics.drawRect(rectangleToPaint.x, rectangleToPaint.y, rectangleToPaint.width, rectangleToPaint.height);
		graphics.setColor(this.colorScheme.getDarkColor().darker());
		graphics.drawRect(rectangleToPaint.x, rectangleToPaint.y, rectangleToPaint.width, 1);
	}

	protected String getCellLabel(final int columnIndex) {
		final F fieldForCurrentColumn = super.parent.getColumnDefinitionAt(columnIndex);
		final Number cellObject = this.totalByColumn.get(fieldForCurrentColumn);
		final String cellLabel = super.parent.getStringFromObject(cellObject);
		return cellLabel;
	}

	protected void paintCellValue(final Graphics2D graphics, final Rectangle rectangleToPaint, final String cellLabel) {
		graphics.setColor(this.colorScheme.getForegroundColor());
		GraphicsHelper.paintCenterString(graphics, cellLabel, graphics.getFont(), rectangleToPaint.x, rectangleToPaint.y, rectangleToPaint.width, rectangleToPaint.height);
	}

	@Override
	protected void paintEmptyContents(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		g2.dispose();
	}

	@Override
	public CellInformation getCellInformation(int rowIndex, int columnIndex) {
		final F fieldForCurrentColumn = super.parent.getColumnDefinitionAt(columnIndex);
		final Number cellObject = this.totalByColumn.get(fieldForCurrentColumn);
		return new CellInformation(cellObject);
	}
}
