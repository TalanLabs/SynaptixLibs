package com.synaptix.widget.view.swing.helper;

import java.io.Serializable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.synaptix.swing.search.Filter;
import com.synaptix.swing.search.filter.DefaultSuperComboBoxFilter;
import com.synaptix.swing.search.filter.DefaultSuperComboBoxFilter.ObjectToKey;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceListCellRenderer;
import com.synaptix.widget.util.StaticWidgetHelper;

public class BooleanViewHelper {

	public static final Filter createBooleanFilter(String id, String name) {
		ObjectToKey<Boolean> booleanObjectToKey = new ObjectToKey<Boolean>() {
			@Override
			public Serializable getKey(Boolean e) {
				return e;
			}
		};

		return new DefaultSuperComboBoxFilter<Boolean>(id, name, 75, new DefaultComboBoxModel(new Boolean[] { null, true, false }), booleanObjectToKey,
				createBooleanTypeGenericSubstanceListCellRenderer(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().all()));
	}

	public static final JComboBox createBooleanComboBox() {
		JComboBox res = new JComboBox(new Boolean[] { null, true, false });
		res.setRenderer(createBooleanTypeGenericSubstanceListCellRenderer(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().none()));
		return res;
	}

	public static final TypeGenericSubstanceListCellRenderer<Boolean> createBooleanTypeGenericSubstanceListCellRenderer(final String nullText) {
		return new TypeGenericSubstanceListCellRenderer<Boolean>(new GenericObjectToString<Boolean>() {
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
		});
	}

}
