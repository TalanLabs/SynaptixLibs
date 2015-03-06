package com.synaptix.swing.search.filter;

import java.awt.Dimension;
import java.io.Serializable;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;

import com.synaptix.swing.search.AbstractFilter;

public class DefaultComboBoxFilter extends AbstractFilter {

	private String id;

	private String name;

	private JComboBox comboBox;

	private JComboBox defaultComboBox;

	private Item[] items;

	public DefaultComboBoxFilter(String name, Item[] items) {
		this(name, name, items);
	}

	public DefaultComboBoxFilter(String name, Item[] items, ListCellRenderer renderer) {
		this(name, name, items, renderer);
	}

	public DefaultComboBoxFilter(String name, int width, Item[] items, ListCellRenderer renderer) {
		this(name, name, width, items, renderer);
	}

	public DefaultComboBoxFilter(String name, int width, Item[] items, ListCellRenderer renderer, boolean useDefault, Object defaultValue) {
		this(name, name, width, items, renderer, useDefault, defaultValue);
	}

	public DefaultComboBoxFilter(String id, String name, Item[] items) {
		this(id, name, 75, items, null);
	}

	public DefaultComboBoxFilter(String id, String name, Item[] items, ListCellRenderer renderer) {
		this(id, name, 75, items, renderer);
	}

	public DefaultComboBoxFilter(String id, String name, int width, Item[] items) {
		this(id, name, width, items, null);
	}

	public DefaultComboBoxFilter(String id, String name, int width, Item[] items, ListCellRenderer renderer) {
		this(id, name, width, items, renderer, true, null);
	}

	public DefaultComboBoxFilter(String id, String name, int width, Item[] items, ListCellRenderer renderer, boolean useDefault, Object defaultValue) {
		super();
		this.id = id;
		this.name = name;

		this.items = items;

		Object[] os = new Object[items.length];
		for (int i = 0; i < items.length; i++) {
			os[i] = items[i].getValue();
		}

		comboBox = new JComboBox(os);
		if (renderer != null) {
			comboBox.setRenderer(renderer);
		}
		comboBox.setPreferredSize(new Dimension(width, comboBox.getPreferredSize().height));

		if (useDefault) {
			defaultComboBox = new JComboBox(os);
			if (renderer != null) {
				defaultComboBox.setRenderer(renderer);
			}
			defaultComboBox.setPreferredSize(new Dimension(width, defaultComboBox.getPreferredSize().height));
			defaultComboBox.setSelectedItem(0);
			setDefaultValue(defaultValue);
		}

		initialize();
	}

	@Override
	public JComponent getComponent() {
		return comboBox;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Serializable getValue() {
		Object value = comboBox.getSelectedItem();
		for (Item item : items) {
			if ((item.getValue() == null && value == null) || (item.getValue() != null && item.getValue().equals(value))) {
				return item.getKey();
			}
		}
		return null;
	}

	@Override
	public void setValue(Object o) {
		for (Item item : items) {
			if ((item.getKey() == null && o == null) || (item.getKey() != null && item.getKey().equals(o))) {
				comboBox.setSelectedItem(item.getValue());
			}
		}
	}

	@Override
	public Serializable getDefaultValue() {
		if (defaultComboBox != null) {
			Object value = defaultComboBox.getSelectedItem();
			for (Item item : items) {
				if ((item.getValue() == null && value == null) || (item.getValue() != null && item.getValue().equals(value))) {
					return item.getKey();
				}
			}
		}
		return null;
	}

	@Override
	public void copyDefaultValue() {
		if (defaultComboBox != null) {
			comboBox.setSelectedItem(defaultComboBox.getSelectedItem());
		} else {
			comboBox.setSelectedItem(null);
		}
	}

	@Override
	public JComponent getDefaultComponent() {
		return defaultComboBox;
	}

	@Override
	public void setDefaultValue(Object o) {
		if (defaultComboBox != null) {
			for (Item item : items) {
				if ((item.getKey() == null && o == null) || (item.getKey() != null && item.getKey().equals(o))) {
					defaultComboBox.setSelectedItem(item.getValue());
				}
			}
		}
	}
}
