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
import com.synaptix.swing.utils.SwingComponentFactory;

public class DefaultDateHourFieldFilter extends AbstractFilter {

	private String id;

	private String name;

	private JFormattedTextField dateHourField;

	private JFormattedTextField defaultHourDateField;

	private JCheckBox todayBox;

	private JPanel defaultDatePanel;

	public DefaultDateHourFieldFilter(String name) {
		this(name, name);
	}

	public DefaultDateHourFieldFilter(String name, boolean useDefault, DefaultDate defaultValue) {
		this(name, name, useDefault, defaultValue);
	}

	public DefaultDateHourFieldFilter(String id, String name) {
		this(id, name, 75);
	}

	public DefaultDateHourFieldFilter(String id, String name, boolean useDefault, DefaultDate defaultValue) {
		this(id, name, 75, useDefault, defaultValue);
	}

	public DefaultDateHourFieldFilter(String id, String name, int width) {
		this(id, name, width, true, null);
	}

	public DefaultDateHourFieldFilter(String id, String name, int width, boolean useDefault, DefaultDate defaultValue) {
		super();
		this.id = id;
		this.name = name;

		dateHourField = SwingComponentFactory.createDateHourField(false, true);
		dateHourField.setPreferredSize(new Dimension(width, dateHourField.getPreferredSize().height));

		if (useDefault) {
			todayBox = new JCheckBox("Aujourd'hui");
			todayBox.addActionListener(new TodayActionListener());
			defaultHourDateField = SwingComponentFactory.createDateHourField(false, true);
			defaultHourDateField.setPreferredSize(new Dimension(width, dateHourField.getPreferredSize().height));
			setDefaultValue(defaultValue);

			FormLayout layout = new FormLayout("p,3dlu,p", "p");
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();

			builder.add(todayBox, cc.xy(1, 1));
			builder.add(defaultHourDateField, cc.xy(3, 1));

			defaultDatePanel = builder.getPanel();
		}

		initialize();
	}

	private final class TodayActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			defaultHourDateField.setEnabled(!todayBox.isSelected());
			if (todayBox.isSelected()) {
				defaultHourDateField.setValue(new Date());
			}
		}
	}

	@Override
	public JComponent getComponent() {
		return dateHourField;
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
		return (Date) dateHourField.getValue();
	}

	@Override
	public void setValue(Object o) {
		if (o != null) {
			dateHourField.setValue(o);
		} else {
			dateHourField.setValue(null);
		}
	}

	@Override
	public Serializable getDefaultValue() {
		if (defaultHourDateField != null) {
			return new DefaultDate((Date) defaultHourDateField.getValue(), todayBox.isSelected());
		}
		return null;
	}

	@Override
	public void copyDefaultValue() {
		if (defaultHourDateField != null) {
			dateHourField.setValue(defaultHourDateField.getValue());
		} else {
			dateHourField.setValue(null);
		}
	}

	@Override
	public JComponent getDefaultComponent() {
		return defaultDatePanel;
	}

	@Override
	public void setDefaultValue(Object o) {
		if (defaultHourDateField != null) {
			if (o != null) {
				DefaultDate dd = (DefaultDate) o;
				defaultHourDateField.setValue(dd.getDate());
				todayBox.setSelected(dd.isToday());
				defaultHourDateField.setEnabled(!todayBox.isSelected());

				if (todayBox.isSelected()) {
					defaultHourDateField.setValue(new Date());
				}
			} else {
				defaultHourDateField.setValue(null);
			}
		}
	}
}
