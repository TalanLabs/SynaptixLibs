package com.synaptix.widget.view.swing.tablemodel.field;

import com.synaptix.component.field.DefaultField;
import com.synaptix.component.field.IField;
import com.synaptix.component.helper.ComponentHelper;

/**
 * Composite field ex : "[position,meaning]"
 * 
 * @author Gaby
 * 
 */
public class CompositeField extends DefaultField {

	/**
	 * Create a composite field with array name ex : "position", "meaning" => "[position,meaning]"
	 * 
	 * @param names
	 * @return
	 */
	public static CompositeField with(String... names) {
		return new CompositeField(names, null, null);
	}

	/**
	 * Create a composite field with array field ex : "position", "meaning" => "[position,meaning]"
	 * 
	 * @param fields
	 * @return
	 */
	public static CompositeField with(IField... fields) {
		return new CompositeField(ComponentHelper.PropertyArrayBuilder.build(fields), null, null);
	}

	/**
	 * Create a composite field with name : ex : "[position,meaning]" or "[position,meaning]{%s , %s}"
	 * 
	 * @param name
	 * @throws IllegalArgumentException
	 *             is not composite field
	 * @return
	 */
	public static CompositeField of(String name) {
		if (!is(name)) {
			throw new IllegalArgumentException("name is not composite field");
		}
		return new CompositeField(extractNames(name), null, extractFormat(name));
	}

	/**
	 * true if composite field
	 * 
	 * @param name
	 * @return
	 */
	public static boolean is(String name) {
		return name.startsWith("[") && ((name.endsWith("]") && name.length() > 2) || (name.contains("]{") && name.endsWith("}") && name.length() > 5));
	}

	private final String[] names;

	private final String[] separators;

	private final String format;

	private CompositeField(String[] names, String[] separators, String format) {
		super(buildName(names, format));

		this.names = names;
		this.separators = separators;
		this.format = format;
	}

	public final String[] originalNames() {
		return names;
	}

	public final String[] seprators() {
		return separators;
	}

	public final String format() {
		return format;
	}

	public String toNamesString() {
		return buildName(names, null);
	}

	private static String buildName(String[] names, String format) {
		StringBuilder sb = new StringBuilder("[");
		if (names != null && names.length > 0) {
			boolean first = true;
			for (String name : names) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(name);
			}
		}
		sb.append("]");
		if (format != null && !format.isEmpty()) {
			sb.append("{");
			sb.append(format);
			sb.append("}");
		}
		return sb.toString();
	}

	private static String[] extractNames(String name) {
		String ps = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
		String[] r = ps.split("\\s*,\\s*");
		String[] res = new String[r.length];
		for (int i = 0; i < r.length; i++) {
			res[i] = r[i].trim();
		}
		return res;
	}

	private static String extractFormat(String name) {
		String res = null;
		if (name.contains("]{") && name.endsWith("}")) {
			res = name.substring(name.indexOf("]{") + 2, name.length() - 1);
		}
		return res;
	}

	public static Builder newBuilder(String... names) {
		return new Builder(names);
	}

	public static Builder newBuilder(IField... fields) {
		return new Builder(fields);
	}

	public static class Builder {

		private String[] names;

		private String format;

		private String[] separators;

		private Builder(String... names) {
			super();
			this.names = names;
		}

		private Builder(IField... fields) {
			super();
			this.names = ComponentHelper.PropertyArrayBuilder.build(fields);
		}

		public Builder separators(String[] separators) {
			this.separators = separators;
			return this;
		}

		public Builder format(String format) {
			this.format = format;
			return this;
		}

		public CompositeField build() {
			return new CompositeField(names, separators, format);
		}
	}
}
