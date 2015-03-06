package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.Serializable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;

import com.synaptix.component.IComponent;
import com.synaptix.component.field.IField;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.helper.GraphicsHelper;
import com.synaptix.widget.hierarchical.view.swing.component.panel.FooterPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.writer.CellInformation;
import com.synaptix.widget.util.StaticWidgetHelper;

public class TitleFooterPanel<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends FooterPanel<IField, E, F, L> {

	private static final long serialVersionUID = -2508079004211354734L;

	private static final Font font = new Font("Monospace", Font.BOLD, 14);

	private final Border border;

	private final SubstanceColorScheme colorScheme;

	public TitleFooterPanel(final ConfigurationContext<E, F, L> configurationContext, final HierarchicalPanel<IField, E, F, L> parent) {
		super(configurationContext, parent);
		this.border = BorderFactory.createEtchedBorder();
		this.colorScheme = SubstanceColorSchemeUtilities.getColorScheme(this, ColorSchemeAssociationKind.FILL, ComponentState.ENABLED);
	}

	@Override
	protected void computeContents(List<L> model) {
	}

	@Override
	protected void paintContents(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		g2.setColor(Color.black);
		GraphicsHelper.paintCenterString(g2, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().total(), font, 0, 0, this.getWidth(), this.getHeight());
		g2.dispose();
	}

	private void paintBackground(final Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(GraphicsHelper.buildVerticalGradientPaint(TitleFooterPanel.class.getName(), this.getHeight(), this.colorScheme.getUltraLightColor(), this.colorScheme.getLightColor()));
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		this.border.paintBorder(this, g2, 0, 0, this.getWidth(), this.getHeight() + 1);
	}

	@Override
	protected void paintEmptyContents(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		g2.dispose();
	}

	@Override
	public CellInformation getCellInformation(int rowIndex, int columnIndex) {
		return new CellInformation(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().total(), super.parent.getColumnDefinitionList().size());
	}
}
