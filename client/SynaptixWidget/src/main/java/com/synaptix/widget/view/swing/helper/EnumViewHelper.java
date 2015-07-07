package com.synaptix.widget.view.swing.helper;

import java.io.Serializable;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.table.TableModel;

import com.synaptix.swing.search.filter.DefaultSuperComboBoxFilter;
import com.synaptix.swing.search.filter.DefaultSuperComboBoxFilter.ObjectToKey;
import com.synaptix.swing.table.AbstractStringExcelColumnRenderer;
import com.synaptix.swing.table.ExcelColumnRenderer;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceComboBoxRenderer;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceListCellRenderer;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceTableCellRenderer;
import com.synaptix.widget.search.filter.SubstanceSuperComboBoxFilter;
import com.synaptix.widget.util.StaticWidgetHelper;

public class EnumViewHelper {

	/**
	 * Returns a renderer to translate enumeration into understandable local meaning for user. This renderer is not case sensitive: it accepts lowercase and uppercase. If the code is not found, the
	 * renderer returns "?".
	 * 
	 * @param clazz
	 *            column type
	 * @param map
	 *            contains translated meaning
	 * @return
	 */
	public static final <T> TypeGenericSubstanceTableCellRenderer<T> createEnumTableCellRenderer(Class<T> clazz, final Map<String, String> map) {
		return new TypeGenericSubstanceTableCellRenderer<T>(new GenericObjectToString<T>() {
			@Override
			public String getString(T t) {
				return EnumViewHelper.getString(t, map, null);
			}
		});
	}

	public static final <T> TypeGenericSubstanceListCellRenderer<T> createEnumListCellRenderer(Class<T> clazz, final Map<String, String> map) {
		return new TypeGenericSubstanceListCellRenderer<T>(new GenericObjectToString<T>() {
			@Override
			public String getString(T t) {
				return EnumViewHelper.getString(t, map, null);
			}
		});
	}

	public static final <T> ExcelColumnRenderer createExcelColumnRenderer(final Map<String, String> map) {
		return new AbstractStringExcelColumnRenderer() {
			@Override
			public String getString(TableModel tableModel, Object value, int row, int col) {
				return EnumViewHelper.getString(value, map, null);
			}
		};
	}

	public static final <E> String getString(E t, final Map<String, String> map, final String nullText) {
		if (t != null) {
			if (map != null) {
				if (map.containsKey(t.toString().toLowerCase())) {
					return map.get(t.toString().toLowerCase());
				} else if (map.containsKey(t.toString().toUpperCase())) {
					return map.get(t.toString().toUpperCase());
				} else {
					return "!" + t.toString() + "!";
				}
			} else {
				return t.toString();
			}
		}
		return nullText;
	}

	public static final <E extends Enum<E>> DefaultSuperComboBoxFilter<E> createEnumDefaultSuperComboBoxFilter(String id, String name, E[] values, final Map<String, String> map, boolean addNull) {
		return createEnumDefaultSuperComboBoxFilter(id, name, values, map, addNull, false);
	}

	public static final <E extends Enum<E>> DefaultSuperComboBoxFilter<E> createEnumDefaultSuperComboBoxFilter(String id, String name, E[] values, final Map<String, String> map, boolean addNull,
			final boolean displayCode) {
		DefaultComboBoxModel listModel = new DefaultComboBoxModel();
		if (addNull) {
			listModel.addElement(null);
		}
		if (values != null) {
			for (E value : values) {
				listModel.addElement(value);
			}
		}
		SubstanceSuperComboBoxFilter<E> filter = new SubstanceSuperComboBoxFilter<E>(id, name, 75, listModel, new ObjectToKey<E>() {
			@Override
			public Serializable getKey(E e) {
				return e;
			}
		}, new GenericObjectToString<E>() {
			@Override
			public String getString(E t) {
				if ((displayCode) && (t != null)) {
					return String.format("%s - %s", t.name(), EnumViewHelper.getString(t, map, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().all()));
				}
				return EnumViewHelper.getString(t, map, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().all());
			}
		});
		return filter;
	}

	public static final <E extends Enum<E>> JComboBox createEnumComboBox(E[] values, final Map<String, String> map, final boolean addNull) {
		DefaultComboBoxModel listModel = new DefaultComboBoxModel();
		if (addNull) {
			listModel.addElement(null);
		}
		if (values != null) {
			for (E value : values) {
				listModel.addElement(value);
			}
		}
		JComboBox res = new JComboBox(listModel) {

			private static final long serialVersionUID = 9161944672658101351L;

			@Override
			public int getSelectedIndex() {
				if ((addNull) && (getSelectedItem() == null)) {
					return 0;
				}
				return super.getSelectedIndex();
			}
		};
		res.setRenderer(new TypeGenericSubstanceComboBoxRenderer<E>(res, new GenericObjectToString<E>() {
			@Override
			public String getString(E t) {
				return EnumViewHelper.getString(t, map, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().none());
			}
		}));
		if (!addNull && values != null && values.length > 0) {
			res.setSelectedIndex(0);
		}
		return res;
	}

	public static final <E extends Enum<E>> String[] convertArrayString(E[] values, boolean addNull) {
		String[] res = new String[values.length + (addNull ? 1 : 0)];
		if (addNull) {
			res[0] = null;
		}
		for (int i = 0; i < values.length; i++) {
			res[i + (addNull ? 1 : 0)] = values[i] != null ? values[i].name() : null;
		}
		return res;
	}

	public static final <E extends Enum<E>> ComboBoxModel createEnumComboBoxModel(E[] values, boolean addNull) {
		DefaultComboBoxModel listModel = new DefaultComboBoxModel();
		if (addNull) {
			listModel.addElement(null);
		}
		if (values != null) {
			for (E value : values) {
				listModel.addElement(value);
			}
		}
		return listModel;
	}
}
