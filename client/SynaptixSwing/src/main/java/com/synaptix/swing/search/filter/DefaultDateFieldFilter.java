package com.synaptix.swing.search.filter;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.search.AbstractFilter;
import com.synaptix.swing.utils.DateTimeUtils;
import com.synaptix.swing.utils.SwingComponentFactory;

public class DefaultDateFieldFilter extends AbstractFilter {

	private String id;

	private String name;

	private JFormattedTextField dateField;

	private JFormattedTextField defaultDateField;

	private JCheckBox todayBox;

	private JPanel defaultDatePanel;

	public DefaultDateFieldFilter(String name) {
		this(name, name);
	}

	public DefaultDateFieldFilter(String name, boolean useDefault, DefaultDate defaultValue) {
		this(name, name, useDefault, defaultValue);
	}

	public DefaultDateFieldFilter(String id, String name) {
		this(id, name, 75);
	}

	public DefaultDateFieldFilter(String id, String name, boolean useDefault, DefaultDate defaultValue) {
		this(id, name, 75, useDefault, defaultValue);
	}

	public DefaultDateFieldFilter(String id, String name, int width) {
		this(id, name, width, true, null);
	}

	public DefaultDateFieldFilter(String id, String name, int width, boolean useDefault, DefaultDate defaultValue) {
		super();
		this.id = id;
		this.name = name;

		dateField = SwingComponentFactory.createDateField(false, true);
		dateField.setPreferredSize(new Dimension(width, dateField.getPreferredSize().height));

		if (useDefault) {
			todayBox = new JCheckBox("Aujourd'hui");
			todayBox.addActionListener(new TodayActionListener());
			defaultDateField = SwingComponentFactory.createDateField(false, true);
			defaultDateField.setPreferredSize(new Dimension(width, dateField.getPreferredSize().height));
			setDefaultValue(defaultValue);

			FormLayout layout = new FormLayout("p,3dlu,p", "p");
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();

			builder.add(todayBox, cc.xy(1, 1));
			builder.add(defaultDateField, cc.xy(3, 1));

			defaultDatePanel = builder.getPanel();
		}

		initialize();
	}

	private final class TodayActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			defaultDateField.setEnabled(!todayBox.isSelected());
			if (todayBox.isSelected()) {
				defaultDateField.setValue(DateTimeUtils.clearHourForDate(new Date()));
			}
		}
	}

	@Override
	public JComponent getComponent() {
		return dateField;
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
		return (Date) dateField.getValue();
	}

	@Override
	public void setValue(Object o) {
		if (o != null) {
			dateField.setValue(o);
		} else {
			dateField.setValue(null);
		}
	}

	@Override
	public Serializable getDefaultValue() {
		if (defaultDateField != null) {
			return new DefaultDate((Date) defaultDateField.getValue(), todayBox.isSelected());
		}
		return null;
	}

	@Override
	public void copyDefaultValue() {
		if (defaultDateField != null) {
			dateField.setValue(defaultDateField.getValue());
		} else {
			dateField.setValue(null);
		}
	}

	@Override
	public JComponent getDefaultComponent() {
		return defaultDatePanel;
	}

	@Override
	public void setDefaultValue(Object o) {
		if (defaultDateField != null) {
			if (o != null) {
				DefaultDate dd = (DefaultDate) o;
				todayBox.setSelected(dd.isToday());
				defaultDateField.setEnabled(!todayBox.isSelected());

				if (todayBox.isSelected()) {
					defaultDateField.setValue(DateTimeUtils.clearHourForDate(new Date()));
				} else {
					if (dd.getDate() != null) {
						defaultDateField.setValue(DateTimeUtils.clearHourForDate(dd.getDate()));
					} else {
						defaultDateField.setValue(null);
					}
				}
			} else {
				defaultDateField.setValue(null);
			}
		}
	}
}
