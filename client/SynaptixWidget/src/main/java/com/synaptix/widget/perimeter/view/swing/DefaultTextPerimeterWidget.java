package com.synaptix.widget.perimeter.view.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;

import com.synaptix.swing.utils.ClipboardHelper;

public class DefaultTextPerimeterWidget extends AbstractPerimeterWidget implements IPerimeterWidget {

	private static final long serialVersionUID = -5097456438605615522L;

	private final String title;

	private final JTextField textField;

	private boolean useJokers;

	/**
	 * Perimeter with a unique input line<br/>
	 * Sql : uses '=' instead of 'LIKE'
	 * 
	 * @see #DefaultTextPerimeterWidget(String, boolean)
	 */
	public DefaultTextPerimeterWidget(String title) {
		this(title, false);
	}

	/**
	 * Perimeter with a unique input line
	 * 
	 * @param title
	 * @param useJokers
	 *            (default: false)
	 */
	public DefaultTextPerimeterWidget(String title, boolean useJokers) {
		super();

		this.title = title;
		this.useJokers = useJokers;

		this.textField = new JTextField();
		ClipboardHelper.installClipboardPasteListener(textField);

		this.textField.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				fireValuesChanged();
			}
		});
		this.textField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				fireValuesChanged();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				fireValuesChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				fireValuesChanged();
			}
		});

		this.addContent(textField);
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public Object getValue() {
		if (StringUtils.isNotBlank(textField.getText())) {
			if (!useJokers) { // default behavior
				List<Object> list = new ArrayList<Object>(1);
				list.add(textField.getText()); // searches: "equals" instead of "begins with"
				return list;
			}
			return textField.getText().replaceAll(" +$", "");
		}
		return null;
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof String) {
			textField.setText((String) value);
		} else {
			textField.setText(null);
		}
	}
}
