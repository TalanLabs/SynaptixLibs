package com.synaptix.taskmanager.helper;

import java.io.Serializable;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.google.inject.Inject;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.swing.search.filter.DefaultSuperComboBoxFilter;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.objecttype.IObjectTypeManager;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceComboBoxRenderer;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceListCellRenderer;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceTableCellRenderer;
import com.synaptix.widget.search.filter.SubstanceSuperComboBoxFilter;

public class TaskObjectClassViewHelper {

	@Inject
	private static IObjectTypeManager objectTypeManager;

	private static final GenericObjectToString<Class<? extends ITaskObject<?>>> createGenericObjectToString(final String nullText) {
		return new GenericObjectToString<Class<? extends ITaskObject<?>>>() {
			@Override
			public String getString(Class<? extends ITaskObject<?>> t) {
				String objectType = getObjectType(t);
				return objectType != null ? objectType : nullText;
			}
		};
	}

	public static final TypeGenericSubstanceTableCellRenderer<Class<? extends ITaskObject<?>>> createTaskObjectClassTableCellRenderer() {
		GenericObjectToString<Class<? extends ITaskObject<?>>> t = createGenericObjectToString(null);
		return new TypeGenericSubstanceTableCellRenderer<Class<? extends ITaskObject<?>>>(t);
	}

	private static final TypeGenericSubstanceListCellRenderer<Class<? extends ITaskObject<?>>> createObjectTypeListCellRenderer(String nullText) {
		GenericObjectToString<Class<? extends ITaskObject<?>>> t = createGenericObjectToString(nullText);
		return new TypeGenericSubstanceListCellRenderer<Class<? extends ITaskObject<?>>>(t);
	}

	private static final TypeGenericSubstanceComboBoxRenderer<Class<? extends ITaskObject<?>>> createTaskObjectClassComboBoxRenderer(JComboBox comboBox, String nullText) {
		GenericObjectToString<Class<? extends ITaskObject<?>>> t = createGenericObjectToString(nullText);
		return new TypeGenericSubstanceComboBoxRenderer<Class<? extends ITaskObject<?>>>(comboBox, t);
	}

	public static final DefaultSuperComboBoxFilter<Class<? extends ITaskObject<?>>> createTaskObjectClassDefaultSuperComboBoxFilter(String id, String name, boolean addNull) {
		DefaultComboBoxModel listModel = new DefaultComboBoxModel();
		if (addNull) {
			listModel.addElement(null);
		}
		List<Class<? extends ITaskObject<?>>> values = TaskManagerHelper.getObjectTypeManager().getAllTaskObjectClasss();
		if (values != null) {
			for (Class<? extends ITaskObject<?>> value : values) {
				listModel.addElement(value);
			}
		}

		DefaultSuperComboBoxFilter<Class<? extends ITaskObject<?>>> filter = new SubstanceSuperComboBoxFilter<Class<? extends ITaskObject<?>>>(id, name, 75, listModel,
				new DefaultSuperComboBoxFilter.ObjectToKey<Class<? extends ITaskObject<?>>>() {
					@Override
					public Serializable getKey(Class<? extends ITaskObject<?>> e) {
						return e;
					}
				}, createGenericObjectToString(StaticCommonHelper.getCommonConstantsBundle().all()));
		return filter;
	}

	public static final JComboBox createTaskObjectClassComboBox(boolean addNull) {
		DefaultComboBoxModel listModel = new DefaultComboBoxModel();
		if (addNull) {
			listModel.addElement(null);
		}
		List<Class<? extends ITaskObject<?>>> values = TaskManagerHelper.getObjectTypeManager().getAllTaskObjectClasss();
		if (values != null) {
			for (Class<? extends ITaskObject<?>> value : values) {
				listModel.addElement(value);
			}
		}
		JComboBox res = new JComboBox(listModel);
		res.setRenderer(createTaskObjectClassComboBoxRenderer(res, StaticCommonHelper.getCommonConstantsBundle().none()));
		if (!addNull && values != null && values.size() > 0) {
			res.setSelectedIndex(0);
		}
		return res;
	}

	public static String getObjectType(Class<? extends ITaskObject<?>> t) {
		if (t == null) {
			return null;
		}
		return objectTypeManager.getObjectType2(t).getName();
	}
}
