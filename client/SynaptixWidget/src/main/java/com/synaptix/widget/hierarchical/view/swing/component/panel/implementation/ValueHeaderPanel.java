package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

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

public class ValueHeaderPanel<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends HeaderPanel<F, E, F, L> {

	private static final long serialVersionUID = 6201270278615687401L;

	private Border border = BorderFactory.createEtchedBorder();

	private final SubstanceColorScheme colorScheme;

	public ValueHeaderPanel(final ConfigurationContext<E, F, L> configurationContext, final HierarchicalPanel<F, E, F, L> parent) {
		super(configurationContext, parent);
		this.colorScheme = SubstanceColorSchemeUtilities.getColorScheme(this, ColorSchemeAssociationKind.FILL, ComponentState.ENABLED);
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
		g2.setPaint(GraphicsHelper.buildVerticalGradientPaint(HeaderPanel.class.getName() + "_normal", this.getHeight(), super.configurationContext.getStandardColor(), super.configurationContext
				.getStandardColor().darker()));
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

	private int paintColumnAndReturnEndAbsciss(final int columnIndex, final Graphics2D graphics, final int absciss) {
		final String columnLabel = super.parent.getColumnLabelAt(columnIndex);
		final int columnWidth = super.parent.getColumnSizeAt(columnIndex);

		final Rectangle rectangleToPaint = buildRectangleToPaint(columnIndex, absciss, 0);
		if (super.parent.isWholeColumnSelected(columnIndex)) {
			paintSelectedCellBorder(graphics, rectangleToPaint);
			paintSelectedCellBackground(graphics, rectangleToPaint);
		} else {
			paintCellBackground(graphics, rectangleToPaint);
			this.border.paintBorder(this, graphics, absciss, 0, columnWidth, this.getHeight());
		}

		graphics.setColor(this.colorScheme.getForegroundColor());
		GraphicsHelper.paintCenterString(graphics, columnLabel, graphics.getFont(), absciss, 0, columnWidth, this.getHeight());
		return absciss + columnWidth;
	}

	@Override
	protected void paintEmptyContents(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		this.border.paintBorder(this, g2, 0, 0, this.getWidth(), this.getHeight());
		g2.dispose();
	}

	@Override
	protected void setSelectionAtPoint(final Point point) {
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
		super.parent.changeColumnSelectionStatusAt(columnIndex);
	}

	@Override
	public CellInformation getCellInformation(int rowIndex, int columnIndex) {
		final String columnLabel = super.parent.getColumnLabelAt(columnIndex);
		return new CellInformation(columnLabel);
	}
}
