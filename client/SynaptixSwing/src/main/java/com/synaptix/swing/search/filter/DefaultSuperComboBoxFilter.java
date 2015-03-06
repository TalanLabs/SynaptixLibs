package com.synaptix.swing.search.filter;

import java.awt.Dimension;
import java.io.Serializable;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.synaptix.swing.search.AbstractFilter;

public class DefaultSuperComboBoxFilter<E> extends AbstractFilter {

	private String id;

	private String name;

	private JComboBox comboBox;

	private JComboBox defaultComboBox;

	private ListModel listModel;

	private ObjectToKey<E> otk;

	public DefaultSuperComboBoxFilter(String id, String name, int width, ListModel listModel, ObjectToKey<E> otk, ListCellRenderer renderer) {
		super();
		this.id = id;
		this.name = name;

		this.listModel = listModel;
		this.otk = otk;

		comboBox = new JComboBox(new MyComboBoxModel(listModel));
		if (renderer != null) {
			comboBox.setRenderer(renderer);
		}
		comboBox.setPreferredSize(new Dimension(width, comboBox.getPreferredSize().height));

		// if (useDefault) {
		// defaultComboBox = new JComboBox(new MyComboBoxModel(listModel));
		// if (renderer != null) {
		// defaultComboBox.setRenderer(renderer);
		// }
		// defaultComboBox.setPreferredSize(new Dimension(width,
		// defaultComboBox.getPreferredSize().height));
		// defaultComboBox.setSelectedItem(0);
		// }

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
	@SuppressWarnings("unchecked")
	public Serializable getValue() {
		E value = (E) comboBox.getSelectedItem();
		return otk.getKey(value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object key) {
		Object value = null;
		if (key != null) {
			int i = 0;
			while (i < listModel.getSize() && value == null) {
				Object v = listModel.getElementAt(i);
				if (v != null) {
					Serializable s = otk.getKey((E) v);
					if ((s == null & key == null) || (s != null && key != null && s.equals(key))) {
						value = v;
					}
				}
				i++;
			}
		}
		comboBox.setSelectedItem(value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Serializable getDefaultValue() {
		Serializable res = null;
		if (defaultComboBox != null) {
			E value = (E) defaultComboBox.getSelectedItem();
			res = otk.getKey(value);
		}
		return res;
	}

	@Override
	public void setDefaultValue(Object key) {
		if (defaultComboBox != null) {
			Object value = null;
			if (key != null) {
				int i = 0;
				while (i < listModel.getSize() && value == null) {
					Object v = listModel.getElementAt(i);
					if (v != null && otk.getKey((E) v).equals(key)) {
						value = v;
					}
					i++;
				}
			}
			defaultComboBox.setSelectedItem(value);
		}
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

	public interface ObjectToKey<E> {

		public Serializable getKey(E e);

	}

	private static final class MyComboBoxModel extends AbstractListModel implements ComboBoxModel {

		private static final long serialVersionUID = 3568981188221799976L;

		private ListModel listModel;

		private Object selectedObject;

		public MyComboBoxModel(ListModel listModel) {
			super();

			this.listModel = listModel;
			listModel.addListDataListener(new MyListDataListener());
		}

		@Override
		public Object getSelectedItem() {
			return selectedObject;

		}

		@Override
		public void setSelectedItem(Object anObject) {
			if ((selectedObject != null && !selectedObject.equals(anObject)) || selectedObject == null && anObject != null) {
				selectedObject = anObject;
				fireContentsChanged(this, -1, -1);
			}
		}

		@Override
		public Object getElementAt(int index) {
			return listModel.getElementAt(index);
		}

		@Override
		public int getSize() {
			return listModel.getSize();
		}

		private final class MyListDataListener implements ListDataListener {

			@Override
			public void intervalRemoved(ListDataEvent e) {
				fireIntervalRemoved(MyComboBoxModel.this, e.getIndex0(), e.getIndex1());
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				fireIntervalAdded(MyComboBoxModel.this, e.getIndex0(), e.getIndex1());
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				fireContentsChanged(MyComboBoxModel.this, e.getIndex0(), e.getIndex1());
			}
		}
	}
}
