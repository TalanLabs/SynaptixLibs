package com.synaptix.widget.renderer.view.swing;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import com.synaptix.swing.utils.GenericObjectToString;

public class TypeGenericSubstanceTableCellRenderer<T> extends SubstanceDefaultTableCellRenderer {

	private static final long serialVersionUID = -2574227069335201208L;

	private GenericObjectToString<T> os;

	public TypeGenericSubstanceTableCellRenderer(GenericObjectToString<T> os) {
		super();

		this.os = os;
	}

	@SuppressWarnings("unchecked")
	protected void setValue(Object value) {
		T t = (T) value;
		this.setText(os.getString(t));
	}
}
