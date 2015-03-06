package com.synaptix.widget.hierarchical.view.swing.component.panel;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.List;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;

public interface HierarchicalCellRenderer<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> {

	public void renderForList(final Graphics2D graphics, final List<L> rows, final Object column);

	public void renderForElement(final Graphics2D graphics, final L row, final Object column);

}
