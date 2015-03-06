package com.synaptix.swing.search;

import java.io.Serializable;

import javax.swing.JComponent;

import com.synaptix.swing.search.Filter.TypeVisible;

public class SearchFilter implements Serializable {

	private static final long serialVersionUID = -6239432165873197452L;

	protected int modelIndex;

	protected boolean visible;

	protected Filter filter;

	protected Object firstDefaultValue;

	public SearchFilter(int modelIndex, Filter filter) {
		this.modelIndex = modelIndex;
		this.filter = filter;

		initializeLocalVars();
	}

	protected void initializeLocalVars() {
		visible = true;
		firstDefaultValue = filter.getDefaultValue();

		filter.copyDefaultValue();
	}

	public JComponent getComponent() {
		return filter.getComponent();
	}

	public JComponent getDefaultComponent() {
		return filter.getDefaultComponent();
	}

	public void setDefaultValue(Object value) {
		filter.setDefaultValue(value);
	}

	public Serializable getDefaultValue() {
		return filter.getDefaultValue();
	}

	public void setValue(Object value) {
		filter.setValue(value);
	}

	public Serializable getValue() {
		return filter.getValue();
	}

	public void copyDefaultValue() {
		filter.copyDefaultValue();
	}

	public boolean isDefaultVisible() {
		return filter.isDefaultVisible();
	}

	public String getIdentifier() {
		return filter.getId();
	}

	public int getModelIndex() {
		return modelIndex;
	}

	public String getName() {
		return filter.getName();
	}

	public TypeVisible getTypeVisible() {
		return filter.getTypeVisible();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void copyFirstDefaultValue() {
		filter.setDefaultValue(firstDefaultValue);
	}
}
