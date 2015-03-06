package com.synaptix.widget.hierarchical.view.swing.component.rule;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.util.StaticWidgetHelper;

public class HierarchicalSum<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends HierarchicalSummaryRule<E, F, L> {

	@Override
	public String getRuleName() {
		return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().sum();
	}

	@Override
	public Number getValue(final L data) {
		Number total = null;
		for (final Serializable cellValue : data.getValuesMap().values()) {
			if (isNumericValue(cellValue)) {
				total = add(total, (Number) cellValue);
			}
		}
		return total;
	}

	@Override
	public Number getTotalValue(final List<L> dataList) {
		Number total = null;
		if (CollectionHelper.isNotEmpty(dataList)) {
			for (final L data : dataList) {
				total = add(total, getValue(data));
			}
		}
		return total;
	}

	private Number add(Number total, Number value) {
		if (total == null) {
			total = value;
		} else if ((total instanceof Integer) && (value instanceof Integer)) {
			total = (Integer) total + (Integer) value;
		} else if ((total instanceof BigInteger) && (value instanceof BigInteger)) {
			total = ((BigInteger) total).add((BigInteger) value);
		} else if (value != null) {
			total = total.doubleValue() + value.doubleValue();
		}
		return total;
	}
}
