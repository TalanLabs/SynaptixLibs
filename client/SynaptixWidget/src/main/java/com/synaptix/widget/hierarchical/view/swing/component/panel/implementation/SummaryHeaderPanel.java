package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
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
import com.synaptix.widget.hierarchical.view.swing.component.rule.HierarchicalSummaryRule;
import com.synaptix.widget.hierarchical.writer.CellInformation;

public class SummaryHeaderPanel<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends HeaderPanel<HierarchicalSummaryRule<E, F, L>, E, F, L> {

	private static final long serialVersionUID = 6419183351635021277L;

	private final Border border;

	private final SubstanceColorScheme colorScheme;

	public SummaryHeaderPanel(final ConfigurationContext<E, F, L> configurationContext, final HierarchicalPanel<HierarchicalSummaryRule<E, F, L>, E, F, L> parent) {
		super(configurationContext, parent);
		this.border = BorderFactory.createEtchedBorder();
		this.colorScheme = SubstanceColorSchemeUtilities.getColorScheme(this, ColorSchemeAssociationKind.FILL, ComponentState.ENABLED);
	}

	@Override
	protected void paintContents(final Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		g2.setColor(Color.black);
		g2.setFont(super.configurationContext.getBigEmphaseFont());

		int nextColumnAbsciss = 0;

		for (int i = 0; i < super.parent.getColumnDefinitionList().size(); i++) {
			nextColumnAbsciss = paintColumnAndReturnEndAbsciss(i, g2, nextColumnAbsciss);
		}
		g2.dispose();
	}

	private void paintBackground(final Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setPaint(GraphicsHelper.buildVerticalGradientPaint(HeaderPanel.class.getName() + "_normal", this.getHeight(), this.configurationContext.getStandardColor(), this.configurationContext
				.getStandardColor().darker()));
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());

		this.border.paintBorder(this, g2, 0, 0, this.getWidth(), this.getHeight() + 1);
	}

	private int paintColumnAndReturnEndAbsciss(final int columnIndex, final Graphics2D graphics, final int absciss) {
		final String columnLabel = super.parent.getColumnLabelAt(columnIndex);
		final int columnWidth = super.parent.getColumnSizeAt(columnIndex);
		this.border.paintBorder(this, graphics, absciss, 0, columnWidth, this.getHeight());
		graphics.setColor(this.colorScheme.getForegroundColor());
		GraphicsHelper.paintCenterString(graphics, columnLabel, graphics.getFont(), absciss, 0, columnWidth, this.getHeight());
		return absciss + columnWidth;
	}

	@Override
	protected void paintEmptyContents(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		g2.dispose();
	}

	@Override
	protected void setSelectionAtPoint(Point point) {
	}

	@Override
	protected void addOrRemoveSelectionAtPoint(Point point) {
	}

	@Override
	protected void selectAtPointIfNotSelected(Point point) {
	}

	@Override
	public CellInformation getCellInformation(int rowIndex, int columnIndex) {
		final String columnLabel = super.parent.getColumnLabelAt(columnIndex);
		return new CellInformation(columnLabel);
	}
}
