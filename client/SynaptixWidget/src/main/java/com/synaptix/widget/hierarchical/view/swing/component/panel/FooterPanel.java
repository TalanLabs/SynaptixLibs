package com.synaptix.widget.hierarchical.view.swing.component.panel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.List;

import javax.swing.JPanel;

import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.writer.IExportableTable;

public abstract class FooterPanel<U, E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends JPanel implements IExportableTable {

	private static final long serialVersionUID = 3308195799176849823L;

	protected final HierarchicalPanel<U, E, F, L> parent;

	protected final ConfigurationContext<E, F, L> configurationContext;

	public FooterPanel(final ConfigurationContext<E, F, L> configurationContext, final HierarchicalPanel<U, E, F, L> parent) {
		super();
		this.parent = parent;
		this.configurationContext = configurationContext;
	}

	protected void computeContents(final List<L> model) {
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (CollectionHelper.isNotEmpty(this.parent.getColumnDefinitionList())) {
			paintContents(g);
		} else {
			paintEmptyContents(g);
		}
	}

	protected abstract void paintContents(final Graphics g);

	protected abstract void paintEmptyContents(final Graphics g);

	public void updateWidth(final int panelWidth) {
		final Dimension preferredSize = new Dimension(panelWidth, configurationContext.getFooterHeight());
		setPreferredSize(preferredSize);
	}

	@Override
	public int getExportHeight() {
		return 1;
	}

	@Override
	public int getExportWidth() {
		return parent.getColumnDefinitionList().size();
	}

	@Override
	public int getColumnWidth(int columnIndex) {
		return parent.getColumnSizeAt(columnIndex);
	}
}
