package com.synaptix.widget.component.view.swing.searchmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentDescriptor.PropertyDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.swing.search.AbstractSearchHeaderModel;
import com.synaptix.swing.search.Filter;
import com.synaptix.swing.search.filter.DefaultCheckBoxFilter;
import com.synaptix.swing.search.filter.DefaultTextFieldFilter;
import com.synaptix.widget.joda.view.swing.search.DefaultLocalDateFieldFilter;
import com.synaptix.widget.joda.view.swing.search.DefaultLocalDateTimeFieldFilter;
import com.synaptix.widget.joda.view.swing.search.DefaultLocalTimeFieldFilter;

public class DefaultComponentSearchHeaderModel<E extends IComponent> extends AbstractSearchHeaderModel {

	private final List<Filter> filters;
	private Class<E> componentClass;

	public DefaultComponentSearchHeaderModel(Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle, String... propertyNames) {
		super();
		this.componentClass = componentClass;

		ComponentDescriptor descriptor = ComponentFactory.getInstance().getDescriptor(componentClass);

		List<String> pns = new ArrayList<String>();
		if (propertyNames == null) {
			pns.addAll(descriptor.getPropertyNames());
			Collections.sort(pns);
		} else {
			pns.addAll(Arrays.asList(propertyNames));
		}

		filters = new ArrayList<Filter>();
		for (String propertyName : pns) {
			filters.add(createFilter(propertyName, constantsWithLookingBundle, descriptor.getPropertyClass(propertyName)));
		}
	}

	public void setSpecificFilters(List<Filter> specificFilters) {
		for (int i = 0; i < filters.size(); i++) {
			Filter f1 = filters.get(i);
			Filter f2 = searchFilter(specificFilters, f1.getId());
			if (f2 != null) {
				filters.set(i, f2);
			}
		}
	}

	private Filter searchFilter(List<Filter> filters, String propertyName) {
		Filter res = null;
		if (filters != null && !filters.isEmpty()) {
			Iterator<Filter> it = filters.iterator();
			while (it.hasNext() && res == null) {
				Filter filter = it.next();
				if (propertyName != null && filter.getId() != null && propertyName.equals(filter.getId())) {
					res = filter;
				}
			}
		}
		return res;
	}

	protected Filter createFilter(String propertyName, ConstantsWithLookingBundle constantsWithLookingBundle, Class<?> componentType) {
		Class<?> clazz = Object.class;
		if (componentType != null) {
			clazz = componentType;
		}
		String name = constantsWithLookingBundle.getString(propertyName);
		if (String.class.isAssignableFrom(clazz)) {
			return createTextFilter(propertyName, name);
		} else if (LocalDate.class.isAssignableFrom(clazz)) {
			return createLocalDateFilter(propertyName, name);
		} else if (LocalDateTime.class.isAssignableFrom(clazz)) {
			return createLocalDateTimeFilter(propertyName, name);
		} else if (LocalTime.class.isAssignableFrom(clazz)) {
			return createLocalTimeFilter(propertyName, name);
		} else if (Boolean.class.isAssignableFrom(clazz)) {
			return createBooleanFilter(propertyName, name);
		} else if (boolean.class.isAssignableFrom(clazz)) {
			return createBooleanFilter(propertyName, name);
		} else if (Date.class.isAssignableFrom(clazz)) {
			return createLocalDateFilter(propertyName, name);
		}

		return createTextFilter(propertyName, name);
	}

	protected Filter createBooleanFilter(String propertyName, String name) {
		return new DefaultCheckBoxFilter(propertyName, name);
	}

	protected Filter createTextFilter(String propertyName, String name) {
		boolean forceUpperCase = false;
		int maxLength = 0;

		Class<? extends IComponent> cc = ComponentHelper.getComponentClass(componentClass, propertyName);
		ComponentDescriptor d = ComponentFactory.getInstance().getDescriptor(cc);
		PropertyDescriptor propertyDescriptor = d.getPropertyDescriptor(ComponentHelper.getPropertyName(componentClass, propertyName));
		if (propertyDescriptor != null) {
			DatabasePropertyExtensionDescriptor propertyExtensionDescriptor = (DatabasePropertyExtensionDescriptor) propertyDescriptor
					.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
			if (propertyExtensionDescriptor.getColumn() != null) {
				forceUpperCase = propertyExtensionDescriptor.getColumn().isUpperOnly();
				maxLength = propertyExtensionDescriptor.getColumn().getSqlSize();
				if (maxLength < 0) {
					maxLength = 0;
				}
			}
		}

		return new DefaultTextFieldFilter(propertyName, name, 75, maxLength, forceUpperCase, true, null);
	}

	protected Filter createLocalDateFilter(String propertyName, String name) {
		return new DefaultLocalDateFieldFilter(propertyName, name, 75);
	}

	protected Filter createLocalDateTimeFilter(String propertyName, String name) {
		return new DefaultLocalDateTimeFieldFilter(propertyName, name, 75);
	}

	protected Filter createLocalTimeFilter(String propertyName, String name) {
		return new DefaultLocalTimeFieldFilter(propertyName, name, 75);
	}

	@Override
	public final int getFilterCount() {
		return filters.size();
	}

	@Override
	public final Filter getFilter(int index) {
		return filters.get(index);
	}
}
