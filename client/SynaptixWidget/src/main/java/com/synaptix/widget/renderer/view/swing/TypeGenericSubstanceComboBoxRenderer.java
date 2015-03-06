package com.synaptix.widget.renderer.view.swing;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JList;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultComboBoxRenderer;

import com.synaptix.swing.utils.GenericObjectToString;

public class TypeGenericSubstanceComboBoxRenderer<T> extends SubstanceDefaultComboBoxRenderer {

	private static final long serialVersionUID = -2574227069335201208L;

	private GenericObjectToString<T> os;

	public TypeGenericSubstanceComboBoxRenderer(JComboBox comboBox, GenericObjectToString<T> os) {
		super(comboBox);

		this.os = os;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Component res = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		setValue(value);
		return res;
	}

	@SuppressWarnings("unchecked")
	protected void setValue(Object value) {
		T t = (T) value;
		this.setText(os.getString(t));
	}
}
