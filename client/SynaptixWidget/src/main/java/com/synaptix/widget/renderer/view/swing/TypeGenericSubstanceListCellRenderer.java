package com.synaptix.widget.renderer.view.swing;

import java.awt.Component;

import javax.swing.JList;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;

import com.synaptix.swing.utils.GenericObjectToString;

public class TypeGenericSubstanceListCellRenderer<T> extends SubstanceDefaultListCellRenderer {

	private static final long serialVersionUID = -2574227069335201208L;

	private GenericObjectToString<T> os;

	public TypeGenericSubstanceListCellRenderer(GenericObjectToString<T> os) {
		super();

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
