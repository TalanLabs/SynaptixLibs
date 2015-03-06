package com.synaptix.gwt.shared.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.synaptix.gwt.shared.component.IComponentDto;
import com.synaptix.gwt.shared.field.IFieldDto;

public class ComponentDtoHelper {

	/**
	 * Check equals content of componentDto
	 * 
	 * @param componentDto1
	 * @param componentDto2
	 * @return
	 */
	public static final boolean equalsContent(IComponentDto componentDto1, IComponentDto componentDto2) {
		boolean res = false;
		if (componentDto1.getClass().getName().equals(componentDto2.getClass().getName())) {
			res = true;
			Iterator<String> it = componentDto1.straightGetPropertyNames().iterator();
			while (it.hasNext() && res) {
				String propertyName = it.next();
				Object value1 = componentDto1.straightGetProperty(propertyName);
				Object value2 = componentDto2.straightGetProperty(propertyName);
				res = equalsContent(value1, value2);
			}
		}
		return res;
	}

	private static final boolean equalsContent(Object value1, Object value2) {
		boolean res;
		if (value1 == null && value2 == null) {
			res = true;
		} else if (value1 == null || value2 == null) {
			res = false;
		} else if (value1 instanceof IComponentDto) {
			res = equalsContent((IComponentDto) value1, (IComponentDto) value2);
		} else if (value1 instanceof List) {
			res = equalsContent((List<?>) value1, (List<?>) value2);
		} else {
			res = value1.equals(value2);
		}
		return res;
	}

	private static final boolean equalsContent(List<?> list1, List<?> list2) {
		boolean res = false;
		if (list1.size() == list2.size()) {
			int i = 0;
			res = true;
			while (i < list1.size() && res) {
				Object value1 = list1.get(i);
				Object value2 = list2.get(i);
				res = equalsContent(value1, value2);
				i++;
			}
		}
		return res;
	}

	/**
	 * Clone a componentDto
	 * 
	 * @param componentDto
	 * @param clonner
	 * @return
	 */
	public static final <E extends IComponentDto> E clone(E componentDto, IClonner clonner) {
		E res = clonner.newInstanceComponentDto(componentDto);
		for (String propertyName : componentDto.straightGetPropertyNames()) {
			Object value = componentDto.straightGetProperty(propertyName);
			res.straightSetProperty(propertyName, clone(value, clonner));
		}
		return res;
	}

	private static final Object clone(Object value, IClonner clonner) {
		Object res;
		if (value instanceof IComponentDto) {
			res = clone((IComponentDto) value, clonner);
		} else if (value instanceof List) {
			List<?> list1 = (List<?>) value;
			List<Object> list2 = new ArrayList<Object>();
			for (Object v : list1) {
				list2.add(clone(v, clonner));
			}
			res = list2;
		} else {
			res = value;
		}
		return res;
	}

	public interface IClonner {

		public <E extends IComponentDto> E newInstanceComponentDto(E value);

	}

	/**
	 * Build a dot property name
	 * 
	 * toto.name
	 * 
	 * tata.id
	 * 
	 * toto.tata.id
	 * 
	 * @author Gaby
	 * 
	 */
	public static final class PropertyDotBuilder {

		private List<String> propertyNames;

		public PropertyDotBuilder() {
			super();
			propertyNames = new ArrayList<String>();
		}

		/**
		 * Add array enum
		 * 
		 * @param propertyNames
		 * @return
		 */
		public PropertyDotBuilder addPropertyNames(IFieldDto... propertyNames) {
			for (IFieldDto propertyName : propertyNames) {
				addPropertyName(propertyName);
			}
			return this;
		}

		/**
		 * Add a enum
		 * 
		 * @param propertyName
		 * @return
		 */
		private PropertyDotBuilder addPropertyName(IFieldDto propertyName) {
			assert propertyName != null;
			propertyNames.add(propertyName.name());
			return this;
		}

		/**
		 * Add a array string
		 * 
		 * @param propertyNames
		 * @return
		 */
		public PropertyDotBuilder addPropertyNames(String... propertyNames) {
			for (String propertyName : propertyNames) {
				addPropertyName(propertyName);
			}
			return this;
		}

		/**
		 * Add a string
		 * 
		 * @param propertyName
		 * @return
		 */
		private PropertyDotBuilder addPropertyName(String propertyName) {
			assert propertyName != null;
			propertyNames.add(propertyName);
			return this;
		}

		/**
		 * build
		 * 
		 * @return
		 */
		public String build() {
			return build(propertyNames.toArray(new String[propertyNames.size()]));
		}

		/**
		 * Build a dot property name with string
		 * 
		 * @param propertyNames
		 *            toto, tata, id
		 * @return toto.tata.id
		 */
		public static String build(String... propertyNames) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (String propertyName : propertyNames) {
				if (first) {
					first = false;
				} else {
					sb.append(".");
				}
				sb.append(propertyName);
			}
			return sb.toString();
		}

		/**
		 * Build a dot property name with enum
		 * 
		 * @param propertyNames
		 *            Enum.toto, Enum.tata, Enum.id
		 * @return toto.tata.id
		 */
		public static String build(IFieldDto... propertyNames) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (IFieldDto propertyName : propertyNames) {
				if (first) {
					first = false;
				} else {
					sb.append(".");
				}
				sb.append(propertyName);
			}
			return sb.toString();
		}
	}

	/**
	 * Build array of property name
	 * 
	 * @author Gaby
	 * 
	 */
	public static final class PropertyArrayBuilder {

		private List<String> propertyNames;

		public PropertyArrayBuilder() {
			super();
			propertyNames = new ArrayList<String>();
		}

		/**
		 * Add array enum
		 * 
		 * @param propertyNames
		 * @return
		 */
		public PropertyArrayBuilder addPropertyNames(IFieldDto... propertyNames) {
			for (IFieldDto propertyName : propertyNames) {
				addPropertyName(propertyName);
			}
			return this;
		}

		/**
		 * Add a dot property name
		 * 
		 * @param propertyNames
		 *            Enum.toto, Enum.tata, Enum.id -> toto.tata.id
		 * @return
		 */
		public PropertyArrayBuilder addDotPropertyName(IFieldDto... propertyNames) {
			addPropertyName(PropertyDotBuilder.build(propertyNames));
			return this;
		}

		/**
		 * Add a property name
		 * 
		 * @param propertyName
		 * @return
		 */
		private PropertyArrayBuilder addPropertyName(IFieldDto propertyName) {
			assert propertyName != null;
			propertyNames.add(propertyName.name());
			return this;
		}

		/**
		 * Add a property name
		 * 
		 * @param propertyNames
		 * @return
		 */
		public PropertyArrayBuilder addPropertyNames(String... propertyNames) {
			for (String propertyName : propertyNames) {
				addPropertyName(propertyName);
			}
			return this;
		}

		/**
		 * Add a dot property name
		 * 
		 * @param propertyNames
		 *            toto, tata, id -> toto.tata.id
		 * @return
		 */
		public PropertyArrayBuilder addDotPropertyName(String... propertyNames) {
			addPropertyName(PropertyDotBuilder.build(propertyNames));
			return this;
		}

		/**
		 * Add a property name
		 * 
		 * @param propertyName
		 * @return
		 */
		private PropertyArrayBuilder addPropertyName(String propertyName) {
			assert propertyName != null;
			propertyNames.add(propertyName);
			return this;
		}

		public String[] build() {
			return propertyNames.toArray(new String[propertyNames.size()]);
		}

		/**
		 * Build a array with enum
		 * 
		 * @param es
		 * @return
		 */
		public static String[] build(IFieldDto... es) {
			String[] res = new String[es.length];
			int i = 0;
			for (IFieldDto e : es) {
				res[i] = e.name();
				i++;
			}
			return res;
		}

		/**
		 * Build a array
		 * 
		 * @param ss
		 * @return
		 */
		public static String[] build(String... ss) {
			return ss;
		}
	}
}
