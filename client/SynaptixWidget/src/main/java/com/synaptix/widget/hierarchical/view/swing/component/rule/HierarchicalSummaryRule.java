package com.synaptix.widget.hierarchical.view.swing.component.rule;

import java.io.Serializable;
import java.util.List;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;

public abstract class HierarchicalSummaryRule<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> {

	public abstract String getRuleName();

	public abstract Number getValue(final L data);

	public abstract Number getTotalValue(final List<L> dataList);

	public int getVerticalCellSpan(final int rowIndex) {
		return 1;
	}

	protected boolean isNumericValue(final Serializable value) {
		return value != null && Number.class.isAssignableFrom(value.getClass());
	}
}
