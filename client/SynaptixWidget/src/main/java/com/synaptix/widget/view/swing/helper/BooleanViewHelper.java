package com.synaptix.widget.view.swing.helper;

import java.io.Serializable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.synaptix.swing.search.Filter;
import com.synaptix.swing.search.filter.DefaultSuperComboBoxFilter.ObjectToKey;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceComboBoxRenderer;
import com.synaptix.widget.search.filter.SubstanceSuperComboBoxFilter;
import com.synaptix.widget.util.StaticWidgetHelper;

public class BooleanViewHelper {

	public static final Filter createBooleanFilter(String id, String name) {
		ObjectToKey<Boolean> booleanObjectToKey = new ObjectToKey<Boolean>() {
			@Override
			public Serializable getKey(Boolean e) {
				return e;
			}
		};

		return new SubstanceSuperComboBoxFilter<Boolean>(id, name, 75, new DefaultComboBoxModel(new Boolean[] { null, true, false }), booleanObjectToKey,
				createBooleanGenericObjectToString(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().all()));
	}

	public static final JComboBox createBooleanComboBox() {
		JComboBox res = new JComboBox(new Boolean[] { null, true, false });
		res.setRenderer(createBooleanTypeGenericSubstanceComboBoxCellRenderer(res, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().none()));
		return res;
	}

	public static final TypeGenericSubstanceComboBoxRenderer<Boolean> createBooleanTypeGenericSubstanceComboBoxCellRenderer(JComboBox comboBox, final String nullText) {
		return new TypeGenericSubstanceComboBoxRenderer<Boolean>(comboBox, createBooleanGenericObjectToString(nullText));
	}

	public static final GenericObjectToString<Boolean> createBooleanGenericObjectToString(final String nullText) {
		return new GenericObjectToString<Boolean>() {
			@Override
			public String getString(Boolean t) {
				if (t != null) {
					if (t) {
						return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().yes();
					} else {
						return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().no();
					}
				}
				return nullText;
			}
		};
	}

}
