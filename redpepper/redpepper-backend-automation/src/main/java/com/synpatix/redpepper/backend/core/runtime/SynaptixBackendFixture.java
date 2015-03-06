package com.synpatix.redpepper.backend.core.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.google.inject.Inject;
import com.synaptix.component.IComponent;
import com.synaptix.entity.IdRaw;
import com.synaptix.redpepper.commons.init.ITestManager;
import com.synaptix.service.filter.RootNode;
import com.synaptix.service.filter.builder.AndOperatorBuilder;
import com.synpatix.redpepper.backend.helper.FixtureHelper;

public abstract class SynaptixBackendFixture {

	protected Map<String, Object> userVariables;
	private RepositorySetup autoSetup;

	@Inject
	private ITestManager manager;

	public SynaptixBackendFixture() {
		super();
		userVariables = new HashMap<String, Object>();
	}

	@Inject
	public void setAutoSetup(RepositorySetup autoSetup) {
		this.autoSetup = autoSetup;
	}

	public IComponent getComponent(String componentName) {
		IComponent result = null;
		if (userVariables.get(componentName) instanceof IComponent) {
			result = (IComponent) userVariables.get(componentName);
		}
		return result;
	}

	public <E extends IComponent> void register(String componentName, Object component) {
		userVariables.put(componentName, component);
	}

	public String getProperty(String componentName, String propertyName) {
		if (propertyName == null || componentName == null) {
			return null;
		}
		IComponent component = (IComponent) userVariables.get(componentName);
		if (component == null) {
			return null;
		}
		String value = getValue(propertyName, component);
		if (value == null) {
			value = "null";
		}
		return value;
	}

	private String formatValue(Object value) {
		if (value instanceof LocalDateTime) {
			LocalDateTime result = (LocalDateTime) value;
			return result.toString(FixtureHelper.DateTimePattern);
		} else if (value instanceof LocalDate) {
			LocalDate result = (LocalDate) value;
			return result.toString(FixtureHelper.DatePattern);
		} else if (value instanceof LocalTime) {
			LocalTime result = (LocalTime) value;
			return result.toString(FixtureHelper.TimePattern);
		}
		return value.toString();
	}

	protected String getValue(String propertyName, IComponent component) {
		Object value = getValue(component, propertyName);
		if (value == null) {
			return null;
		}
		if (Enum.class.isAssignableFrom(value.getClass())) {
			return ((Enum<?>) value).name();
		}
		return formatValue(value);
	}

	/**
	 * Gets the value of a property for a component.
	 * 
	 * @param component
	 * @param propertyName
	 * @return Object
	 */
	public Object getValue(IComponent component, String propertyName) {
		int index = propertyName.indexOf('.');
		if (index == -1) {
			return component.straightGetProperty(propertyName);
		} else {
			String head = propertyName.substring(0, index);
			Object temp = null;
			if (head.contains("[")) {
				Pattern regexPattern = Pattern.compile("(\\w+)\\[(\\w+)=(\\w+)\\]");
				Matcher matcher = regexPattern.matcher(head);
				boolean result = matcher.matches();
				if (result == false) {
					return null;
				} else {
					String property = matcher.group(1);
					String findBy = matcher.group(2);
					String value = matcher.group(3);
					System.out.println("Property=" + property + ", findBy=" + findBy + ", value=" + value);
					Collection<?> list = (Collection<?>) component.straightGetProperty(property);
					for (Object object : list) {
						IComponent componentObject = (IComponent.class.isAssignableFrom(object.getClass()) ? (IComponent) object : null);
						String parsedValue = null;
						if (componentObject.straightGetPropertyClass(findBy).isAssignableFrom(IdRaw.class)) {
							parsedValue = FixtureHelper.parseId(value).toString();
						} else {
							parsedValue = value;
						}
						if (componentObject.straightGetProperty(findBy).toString().equals(parsedValue)) {
							System.out.println("Found it !");
							temp = object;
						}
					}
				}
			} else {
				temp = component.straightGetProperty(head);
				if (!(temp instanceof IComponent)) {
					return temp;
				}
			}
			if (temp == null) {
				return null;
			}
			return getValue((IComponent) temp, propertyName.substring(index + 1));
		}
	}

	/**
	 * Find a component and save it.
	 * 
	 * @param componentName
	 *            Test name of the component class
	 * @param idValue
	 *            Value to search (ID, business key,...), the field to search is defined in test configuration
	 * @param variableName
	 *            Name of the variable where the component will be saved.
	 * @return The found component
	 */
	public IComponent findComponent(String componentName, String idValue, String variableName) {
		IComponent component = null;
		Class<? extends IComponent> componentClass = autoSetup.getComponentClassFullNameFromTestName(componentName);
		String searchBy = autoSetup.getComponentClassSearchByFromTestName(componentName);
		if (searchBy.equals("id")) {
			idValue = FixtureHelper.parseId(idValue).toString();
		}
		AndOperatorBuilder andOperatorBuilder = new AndOperatorBuilder();
		andOperatorBuilder.addEqualsPropertyValue(searchBy, idValue);
		component = manager.selectOne(componentClass, new RootNode(andOperatorBuilder.build()));
		if (component != null) {
			userVariables.put(variableName, component);
		}

		return component;
	}
}
