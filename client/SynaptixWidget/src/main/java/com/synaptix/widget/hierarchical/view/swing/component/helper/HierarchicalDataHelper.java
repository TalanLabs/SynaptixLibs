package com.synaptix.widget.hierarchical.view.swing.component.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.field.IField;
import com.synaptix.service.hierarchical.model.IHierarchicalData;
import com.synaptix.widget.hierarchical.view.swing.component.title.IHierarchicalTitleColumnElement;

public final class HierarchicalDataHelper {

	private HierarchicalDataHelper() {
	}

	public static <F extends Serializable, L extends IHierarchicalData<F>> void consolidateModel(final List<L> originalModel) {
		if (originalModel != null) {
			for (final L line : originalModel) {
				setLineValueMapIfNull(line);
			}
		}
	}

	private static <F extends Serializable, L extends IHierarchicalData<F>> void setLineValueMapIfNull(final L line) {
		if (line.getValuesMap() == null) {
			line.setValuesMap(new HashMap<F, Serializable>());
		}
	}

	public static <C extends IHierarchicalTitleColumnElement> List<IField> convertTitleColumnElementListToFieldList(final List<C> columnDefinitions) {
		final List<IField> columnDefinitionsAsFieldList = new ArrayList<IField>();
		if (CollectionHelper.isNotEmpty(columnDefinitions)) {
			for (final C columnElement : columnDefinitions) {
				if (columnElement.isColumnVisible()) {
					columnDefinitionsAsFieldList.add(columnElement.getColumnElement());
				}
			}
		}
		return columnDefinitionsAsFieldList;
	}

	public static IHierarchicalTitleColumnElement buildTitleElement(final IField field) {
		return buildTitleElement(field, true);
	}

	public static IHierarchicalTitleColumnElement buildTitleElement(final IField field, final boolean isVisible) {
		return buildTitleElement(field, isVisible, false);
	}

	public static IHierarchicalTitleColumnElement buildTitleElement(final IField field, final boolean isVisible, final boolean isLock) {
		final IHierarchicalTitleColumnElement element = ComponentFactory.getInstance().createInstance(IHierarchicalTitleColumnElement.class);
		element.setColumnElement(field);
		element.setColumnVisible(isVisible);
		element.setLock(isLock);
		return element;
	}
}
