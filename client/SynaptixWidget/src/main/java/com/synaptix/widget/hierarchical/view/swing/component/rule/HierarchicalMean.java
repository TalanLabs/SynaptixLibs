package com.synaptix.widget.hierarchical.view.swing.component.rule;

import java.io.Serializable;
import java.util.List;

import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalData;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.util.StaticWidgetHelper;

public class HierarchicalMean<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends HierarchicalSummaryRule<E, F, L> {

	@Override
	public String getRuleName() {
		return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().mean();
	}

	@Override
	public Number getValue(final L data) {
		Double total = 0d;
		int numberOfValues = 0;
		for (final Serializable cellValue : data.getValuesMap().values()) {
			if (isNumericValue(cellValue)) {
				total = total + ((Number) cellValue).doubleValue();
				numberOfValues++;
			}
		}
		if (numberOfValues > 0) {
			return total / numberOfValues;
		}
		return 0;
	}

	@Override
	public Number getTotalValue(final List<L> dataList) {
		Double total = 0d;
		int numberOfValues = 0;
		if (CollectionHelper.isNotEmpty(dataList)) {
			for (final IHierarchicalData<?> data : dataList) {
				for (final Serializable cellValue : data.getValuesMap().values()) {
					if (isNumericValue(cellValue)) {
						total = total + ((Number) cellValue).doubleValue();
						numberOfValues++;
					}
				}
			}
		}
		if (numberOfValues > 0) {
			return total / numberOfValues;
		}
		return 0;
	}

}
