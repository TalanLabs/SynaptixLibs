package com.synaptix.widget.search.filter;

import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import com.synaptix.swing.search.filter.DefaultSuperComboBoxFilter;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceComboBoxRenderer;

public class SubstanceSuperComboBoxFilter<E> extends DefaultSuperComboBoxFilter<E> {

	private JComboBox comboBox;

	private ListModel listModel;

	public SubstanceSuperComboBoxFilter(String id, String name, int width, ListModel listModel, ObjectToKey<E> otk, GenericObjectToString<E> os) {
		super(id, name, width, listModel, otk, os);
	}

	@Override
	protected ListCellRenderer createRenderer(JComboBox comboBox, GenericObjectToString<E> os) {
		return new TypeGenericSubstanceComboBoxRenderer(comboBox, os);
	}
}
