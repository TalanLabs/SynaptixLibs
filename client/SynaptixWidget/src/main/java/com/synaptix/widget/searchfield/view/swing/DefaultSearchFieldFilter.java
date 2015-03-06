package com.synaptix.widget.searchfield.view.swing;

import java.io.Serializable;

import javax.swing.JComponent;

import com.synaptix.swing.search.AbstractFilter;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.searchfield.context.ISearchFieldWidgetContext;

public class DefaultSearchFieldFilter<E extends Serializable> extends AbstractFilter {

	private String id;

	private String name;

	private DefaultSearchFieldWidget<E> searchFieldWidget;

	private E defaultValue;

	/**
	 * 
	 * @param id
	 * @param name
	 * @param searchFieldWidgetContext
	 * @param objectToString
	 */
	public DefaultSearchFieldFilter(String id, String name, ISearchFieldWidgetContext<E> searchFieldWidgetContext, GenericObjectToString<E> objectToString) {
		this(id, name, searchFieldWidgetContext, objectToString, false, null, false);
	}

	/**
	 * @param id
	 * @param name
	 * @param searchFieldWidgetContext
	 * @param objectToString
	 * @param editable
	 * @param promptText
	 *            text for text field
	 */
	public DefaultSearchFieldFilter(String id, String name, ISearchFieldWidgetContext<E> searchFieldWidgetContext, GenericObjectToString<E> objectToString, boolean editable, String promptText) {
		this(id, name, searchFieldWidgetContext, objectToString, editable, promptText, false);
	}

	/**
	 * 
	 * @param id
	 * @param name
	 * @param searchFieldWidgetContext
	 * @param objectToString
	 * @param editable
	 * @param promptText
	 *            text for text field
	 * @param maxLength
	 *            Max length of the field. 0 means no maximum length.
	 * @param forceUppercase
	 *            If set to true all text will be forced to uppercase. If false, the widget will behave like a default JTextField.
	 */
	public DefaultSearchFieldFilter(String id, String name, ISearchFieldWidgetContext<E> searchFieldWidgetContext, GenericObjectToString<E> objectToString, boolean editable, String promptText,
			boolean forceUppercase) {
		super();

		this.id = id;
		this.name = name;
		this.searchFieldWidget = new DefaultSearchFieldWidget<E>(searchFieldWidgetContext, objectToString, editable, promptText, false, forceUppercase);

		initialize();
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
	public JComponent getComponent() {
		return searchFieldWidget;
	}

	@Override
	public Serializable getValue() {
		return searchFieldWidget.getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object o) {
		searchFieldWidget.setValue((E) o);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setDefaultValue(Object o) {
		this.defaultValue = (E) o;
	}

	@Override
	public void copyDefaultValue() {
		searchFieldWidget.setValue(defaultValue);
	}
}
