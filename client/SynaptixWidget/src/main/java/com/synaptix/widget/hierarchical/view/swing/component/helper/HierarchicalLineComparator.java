package com.synaptix.widget.hierarchical.view.swing.component.helper;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.synaptix.component.IComponent;
import com.synaptix.component.field.IField;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.title.IHierarchicalTitleColumnElement;

public class HierarchicalLineComparator<L extends IHierarchicalLine<?, ?>> implements Comparator<L> {

	private final Map<IField, Integer> regroupmentCriteriaAndOrder;

	private final ConfigurationContext<?, ?, ?> configurationContext;

	public HierarchicalLineComparator(final ConfigurationContext<?, ?, ?> configurationContext) {
		this.regroupmentCriteriaAndOrder = new LinkedHashMap<IField, Integer>();
		this.configurationContext = configurationContext;
	}

	public void setModelRegroupmentCriteria(final List<IHierarchicalTitleColumnElement> modelRegroupmentCriteria) {
		this.regroupmentCriteriaAndOrder.clear();
		initRegroupmentCriteriaMap(modelRegroupmentCriteria);
	}

	private void initRegroupmentCriteriaMap(final List<IHierarchicalTitleColumnElement> modelRegroupmentCriteria) {
		for (final IHierarchicalTitleColumnElement criteria : modelRegroupmentCriteria) {
			this.regroupmentCriteriaAndOrder.put(criteria.getColumnElement(), 1);
		}
	}

	public void reverseOrderForField(final IField field) {
		if (this.regroupmentCriteriaAndOrder.containsKey(field)) {
			this.regroupmentCriteriaAndOrder.put(field, this.regroupmentCriteriaAndOrder.get(field) * -1);
		}
	}

	@Override
	public int compare(final L line1, final L line2) {
		final IComponent titleComponent1 = line1.getTitleComponent();
		final IComponent titleComponent2 = line2.getTitleComponent();
		int comparaison = -1;
		if ((titleComponent1 == null && titleComponent2 == null) || (titleComponent1 != null && titleComponent1.equals(titleComponent2))) {
			comparaison = 0;
		}
		final Iterator<IField> regroupmentCriteriaIterator = this.regroupmentCriteriaAndOrder.keySet().iterator();
		boolean isDifferenceFound = false;
		while (regroupmentCriteriaIterator.hasNext() && !isDifferenceFound) {
			final IField criteria = regroupmentCriteriaIterator.next();
			final int comparaisonForField = compareTitleComponentsForCriteria(titleComponent1, titleComponent2, criteria);
			if (comparaisonForField != 0) {
				final Integer order = this.regroupmentCriteriaAndOrder.get(criteria);
				comparaison = comparaisonForField * order;
				isDifferenceFound = true;
			}
		}
		return comparaison;
	}

	@SuppressWarnings("unchecked")
	private <T> int compareTitleComponentsForCriteria(final IComponent titleComponent1, final IComponent titleComponent2, final IField criteria) {
		int comparaison = 0;
		final String criteriaName = criteria.name();
		final T fieldValue1 = (T) ComponentHelper.getValue(titleComponent1, criteriaName);
		final T fieldValue2 = (T) ComponentHelper.getValue(titleComponent2, criteriaName);
		if (this.configurationContext.getComparatorForField(criteria) != null) {
			final Comparator<T> comparator = (Comparator<T>) this.configurationContext.getComparatorForField(criteria);
			comparaison = comparator.compare(fieldValue1, fieldValue2);
		} else {
			comparaison = compareFieldValues(fieldValue1, fieldValue2);
		}
		return comparaison;
	}

	@SuppressWarnings("unchecked")
	private int compareFieldValues(final Object fieldValue1, final Object fieldValue2) {
		if (fieldValue1 == fieldValue2) {
			return 0;
		}
		if (fieldValue1 != null && fieldValue2 == null) {
			return 1;
		}
		if (fieldValue2 != null && fieldValue1 == null) {
			return -1;
		}

		if (Comparable.class.isAssignableFrom(fieldValue1.getClass())) {
			return ((Comparable<Object>) fieldValue1).compareTo(fieldValue2);
		}

		return compareToStringValues(fieldValue1, fieldValue2);
	}

	private int compareToStringValues(final Object fieldValue1, final Object fieldValue2) {
		final String string1 = this.configurationContext.getStringFromObject(fieldValue1);
		final String string2 = this.configurationContext.getStringFromObject(fieldValue2);
		return compareFieldValues(string1, string2);
	}

}
