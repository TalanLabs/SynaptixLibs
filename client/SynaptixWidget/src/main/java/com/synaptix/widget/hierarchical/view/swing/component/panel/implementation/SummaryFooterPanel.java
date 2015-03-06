package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.synaptix.widget.hierarchical.view.swing.component.panel.FooterPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.view.swing.component.rule.HierarchicalSummaryRule;
import com.synaptix.widget.hierarchical.writer.CellInformation;

public class SummaryFooterPanel<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends FooterPanel<HierarchicalSummaryRule<E, F, L>, E, F, L> {

	private static final long serialVersionUID = -3139875325846692047L;

	private Border border = BorderFactory.createEtchedBorder();

	private final SubstanceColorScheme colorScheme;

	private final Map<HierarchicalSummaryRule<E, F, L>, Number> totalSummariesByRule;

	public SummaryFooterPanel(final ConfigurationContext<E, F, L> configurationContext, final HierarchicalPanel<HierarchicalSummaryRule<E, F, L>, E, F, L> parent) {
		super(configurationContext, parent);
		this.colorScheme = SubstanceColorSchemeUtilities.getColorScheme(this, ColorSchemeAssociationKind.FILL, ComponentState.ENABLED);
		this.totalSummariesByRule = new HashMap<HierarchicalSummaryRule<E, F, L>, Number>();
	}

	@Override
	protected void computeContents(final List<L> model) {
		this.totalSummariesByRule.clear();
		for (final HierarchicalSummaryRule<E, F, L> rule : super.parent.getColumnDefinitionList()) {
			this.totalSummariesByRule.put(rule, rule.getTotalValue(model));
		}
	}

	@Override
	protected void paintContents(final Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		g2.setColor(Color.black);
		g2.setFont(super.configurationContext.getSmallEmphaseFont());

		int nextColumnAbsciss = 0;

		for (int i = 0; i < super.parent.getColumnDefinitionList().size(); i++) {
			nextColumnAbsciss = paintColumnAndReturnEndAbsciss(i, g2, nextColumnAbsciss);
		}
		g2.dispose();
	}

	private int paintColumnAndReturnEndAbsciss(final int columnIndex, final Graphics2D graphics, final int absciss) {

		final int columnWidth = super.parent.getColumnSizeAt(columnIndex);
		this.border.paintBorder(this, graphics, absciss, 0, columnWidth, this.getHeight());
		graphics.setColor(this.colorScheme.getForegroundColor());
		final String cellLabel = getCellLabel(columnIndex, columnIndex);
		GraphicsHelper.paintCenterString(graphics, cellLabel, graphics.getFont(), absciss, 0, columnWidth, this.getHeight());
		return absciss + columnWidth;
	}

	protected String getCellLabel(int rowIndex, int columnIndex) {
		final HierarchicalSummaryRule<E, F, L> rule = super.parent.getColumnDefinitionAt(columnIndex);
		final Number cellValue = this.totalSummariesByRule.get(rule);
		final String cellLabel = super.parent.getStringFromObject(cellValue);
		return cellLabel;
	}

	@Override
	protected void paintEmptyContents(Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		g2.dispose();
	}

	private void paintBackground(final Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(GraphicsHelper.buildVerticalGradientPaint(SummaryFooterPanel.class.getName(), this.getHeight(), this.colorScheme.getUltraLightColor(), this.colorScheme.getLightColor()));
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		this.border.paintBorder(this, g2, 0, 0, this.getWidth(), this.getHeight() + 1);
	}

	@Override
	public CellInformation getCellInformation(int rowIndex, int columnIndex) {
		final HierarchicalSummaryRule<E, F, L> rule = super.parent.getColumnDefinitionAt(columnIndex);
		final Number cellValue = this.totalSummariesByRule.get(rule);
		return new CellInformation(cellValue);
	}
}
