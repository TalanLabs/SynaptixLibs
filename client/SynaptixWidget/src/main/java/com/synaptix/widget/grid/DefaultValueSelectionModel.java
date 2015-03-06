package com.synaptix.widget.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultValueSelectionModel extends AbstractValueSelectionModel {

	private List<Object> values = new ArrayList<Object>();

	private boolean valueIsAdjusting = false;

	@Override
	public Object getSelectionCell() {
		return !this.values.isEmpty() ? values.get(0) : null;
	}

	@Override
	public Object[] getSelectionCells() {
		return this.values.toArray();
	}

	@Override
	public void setSelectionCells(Object... values) {
		Object[] oldValues = this.values.toArray();
		this.values.clear();
		this.values.addAll(Arrays.asList(values));
		fireValueChanged(new ValueSelectionEvent(this, oldValues, this.values.toArray(), valueIsAdjusting));
	}

	@Override
	public void addSelectionCells(Object... values) {
		Object[] oldValues = this.values.toArray();
		this.values.addAll(Arrays.asList(values));
		fireValueChanged(new ValueSelectionEvent(this, oldValues, this.values.toArray(), valueIsAdjusting));
	}

	@Override
	public void removeSelectionCells(Object... values) {
		Object[] oldValues = this.values.toArray();
		this.values.removeAll(Arrays.asList(values));
		fireValueChanged(new ValueSelectionEvent(this, oldValues, this.values.toArray(), valueIsAdjusting));
	}

	@Override
	public void clearSelection() {
		Object[] oldValues = this.values.toArray();
		this.values.clear();
		fireValueChanged(new ValueSelectionEvent(this, oldValues, null, valueIsAdjusting));
	}

	@Override
	public boolean getValueIsAdjusting() {
		return valueIsAdjusting;
	}

	@Override
	public void setValueIsAdjusting(boolean valueIsAdjusting) {
		this.valueIsAdjusting = valueIsAdjusting;
		fireValueChanged(new ValueSelectionEvent(this, null, null, valueIsAdjusting));
	}

	@Override
	public int getSelectionCount() {
		return this.values.size();
	}

	@Override
	public boolean isSelectedCell(Object value) {
		return this.values.contains(value);
	}

	@Override
	public boolean isSelectionEmpty() {
		return this.values.isEmpty();
	}

}
