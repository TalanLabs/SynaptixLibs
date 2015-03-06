package com.synaptix.widget.perimeter.view.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentDescriptor.PropertyDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor;
import com.synaptix.entity.extension.IDatabaseComponentExtension;

public class PerimetersFilterModel<E extends IComponent> {

	private final Map<String, IPerimeterWidget> perimeterMap;

	private final List<String> idPerimeters;

	public PerimetersFilterModel(Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle, String... propertyNames) {
		super();

		ComponentDescriptor descriptor = ComponentFactory.getInstance().getDescriptor(componentClass);

		idPerimeters = new ArrayList<String>();
		if (propertyNames == null || propertyNames.length == 0) {
			idPerimeters.addAll(descriptor.getPropertyNames());
			Collections.sort(idPerimeters);
		} else {
			idPerimeters.addAll(Arrays.asList(propertyNames));
		}

		perimeterMap = new HashMap<String, IPerimeterWidget>();
		for (String propertyName : idPerimeters) {
			IPerimeterWidget perimeter = createPerimeter(propertyName, constantsWithLookingBundle, ComponentHelper.getFinalClass(componentClass, propertyName),
					ComponentHelper.getFinalPropertyDescriptor(componentClass, propertyName));
			perimeterMap.put(propertyName, perimeter);
		}
	}

	private IPerimeterWidget createPerimeter(String propertyName, ConstantsWithLookingBundle constantsWithLookingBundle, Class<?> componentType, PropertyDescriptor propertyDescriptor) {
		Class<?> clazz = Object.class;
		if (componentType != null) {
			clazz = componentType;
		}

		int length = 0;
		boolean capsLock = false;
		if (propertyDescriptor != null) {
			DatabasePropertyExtensionDescriptor dped = (DatabasePropertyExtensionDescriptor) propertyDescriptor.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
			if ((dped != null) && (dped.getColumn() != null)) {
				capsLock = dped.getColumn().isUpperOnly();
				length = dped.getColumn().getSqlSize();
			}
		}

		String name = constantsWithLookingBundle.getString(propertyName);
		if (String.class.isAssignableFrom(clazz)) {
			return createTextPerimeterWidget(propertyName, name, length, capsLock);
		} else if (LocalDate.class.isAssignableFrom(clazz)) {
			return createLocalDatePerimeterWidget(propertyName, name);
		} else if (LocalDateTime.class.isAssignableFrom(clazz)) {
			return createLocalDateTimePerimeterWidget(propertyName, name);
		} else if (LocalTime.class.isAssignableFrom(clazz)) {
			return createLocalTimePerimeterWidget(propertyName, name);
		} else if (Boolean.class.isAssignableFrom(clazz)) {
			return createBooleanPerimeterWidget(propertyName, name);
		} else if (boolean.class.isAssignableFrom(clazz)) {
			return createBooleanPerimeterWidget(propertyName, name);
		} else if (Integer.class.isAssignableFrom(clazz)) {
			return createIntegerPerimeterWidget(propertyName, name);
		} else if (int.class.isAssignableFrom(clazz)) {
			return createIntegerPerimeterWidget(propertyName, name);
		} else if (Double.class.isAssignableFrom(clazz)) {
			return createDoublePerimeterWidget(propertyName, name);
		} else if (double.class.isAssignableFrom(clazz)) {
			return createDoublePerimeterWidget(propertyName, name);
		} else if (Float.class.isAssignableFrom(clazz)) {
			return createDoublePerimeterWidget(propertyName, name);
		} else if (float.class.isAssignableFrom(clazz)) {
			return createDoublePerimeterWidget(propertyName, name);
		} else if (Date.class.isAssignableFrom(clazz)) {
			return createLocalDateTimePerimeterWidget(propertyName, name);
		}

		return createTextPerimeterWidget(propertyName, name, length, capsLock);
	}

	private IPerimeterWidget createTextPerimeterWidget(String propertyName, String name, int length, boolean capsLock) {
		return new DefaultStringPasteListPerimeterWidget(name, length, capsLock);
	}

	private IPerimeterWidget createLocalDatePerimeterWidget(String propertyName, String name) {
		return new LocalDateMinMaxPerimeterWidget(name);
	}

	private IPerimeterWidget createLocalDateTimePerimeterWidget(String propertyName, String name) {
		return new LocalDateTimeMinMaxPerimeterWidget(name);
	}

	private IPerimeterWidget createLocalTimePerimeterWidget(String propertyName, String name) {
		return new LocalTimeMinMaxPerimeterWidget(name);
	}

	private IPerimeterWidget createBooleanPerimeterWidget(String propertyName, final String name) {
		return new DefaultBooleanPerimeterWidget(name);
	}

	private IPerimeterWidget createIntegerPerimeterWidget(String propertyName, String name) {
		return new DefaultIntegerMinMaxPerimeterWidget(name);
	}

	private IPerimeterWidget createDoublePerimeterWidget(String propertyName, String name) {
		return new DefaultDoubleMinMaxPerimeterWidget(name);
	}

	public void setSpecificPerimeterFilters(List<IPerimeterWidgetDescriptor> specificPerimeterFilters) {
		for (int i = 0; i < idPerimeters.size(); i++) {
			String f1 = idPerimeters.get(i);
			IPerimeterWidgetDescriptor f2 = searchPerimeterFilter(specificPerimeterFilters, f1);
			if (f2 != null) {
				perimeterMap.put(f2.getId(), f2.getPerimeterWidget());
			}
		}
	}

	private IPerimeterWidgetDescriptor searchPerimeterFilter(List<IPerimeterWidgetDescriptor> specificPerimeterFilters, String propertyName) {
		IPerimeterWidgetDescriptor res = null;
		if (specificPerimeterFilters != null && !specificPerimeterFilters.isEmpty()) {
			Iterator<IPerimeterWidgetDescriptor> it = specificPerimeterFilters.iterator();
			while (it.hasNext() && res == null) {
				IPerimeterWidgetDescriptor perimeterWidgetDescriptor = it.next();
				if (propertyName != null && perimeterWidgetDescriptor.getId() != null && propertyName.equals(perimeterWidgetDescriptor.getId())) {
					res = perimeterWidgetDescriptor;
				}
			}
		}
		return res;
	}

	public final int getPerimeterCount() {
		return perimeterMap.size();
	}

	public final IPerimeterWidget getPerimeter(int index) {
		return perimeterMap.get(idPerimeters.get(index));
	}

	public final IPerimeterWidget findById(String id) {
		return perimeterMap.get(id);
	}

	public final String getIdPerimeter(int index) {
		return idPerimeters.get(index);
	}

	public final void initFilterMap(Map<String, Object> defaultValueFilterMap) {
		if (CollectionHelper.isNotEmpty(defaultValueFilterMap)) {
			for (Entry<String, Object> entry : defaultValueFilterMap.entrySet()) {
				IPerimeterWidget perimeterWidget = perimeterMap.get(entry.getKey());
				if (perimeterWidget != null) {
					perimeterWidget.setValue(entry.getValue());
				}
			}
		}
	}
}
