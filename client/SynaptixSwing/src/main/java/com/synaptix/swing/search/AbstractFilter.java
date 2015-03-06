package com.synaptix.swing.search;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.JComponent;

public abstract class AbstractFilter implements Filter {

	private TypeVisible typeVisible;

	public AbstractFilter() {
		typeVisible = TypeVisible.Visible;
	}

	protected final void initialize() {
		if (getComponent() != null) {
			getComponent().addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON3)) {
						if (getComponent().isEnabled()) {
							setValue(null);
						}
					}
				}
			});
		}
	}

	@Override
	public JComponent getDefaultComponent() {
		return null;
	}

	@Override
	public Serializable getDefaultValue() {
		return null;
	}

	@Override
	public void setDefaultValue(Object o) {
	}

	@Override
	public void copyDefaultValue() {
	}

	public void setTypeVisible(TypeVisible typeVisible) {
		this.typeVisible = typeVisible;
	}

	@Override
	public TypeVisible getTypeVisible() {
		return typeVisible;
	}

	@Override
	public boolean isDefaultVisible() {
		return true;
	}
}
