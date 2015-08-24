package com.synaptix.taskmanager.view.swing.renderer;

import java.io.Serializable;

import com.synaptix.entity.IdRaw;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceTableCellRenderer;

public class SerializableTableCellRenderer extends TypeGenericSubstanceTableCellRenderer<Serializable> {

	private static final long serialVersionUID = 3729517623187013850L;

	public SerializableTableCellRenderer() {
		super(new GenericObjectToString<Serializable>() {
			@Override
			public String getString(Serializable t) {
				if (t instanceof IdRaw) {
					return ((IdRaw) t).getHex();
				}
				return null;
			}
		});
	}
}
