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

public class DefaultDateComboBoxFilter extends AbstractFilter {

	private String id;

	private String name;

	private JFormattedTextField dateBox;

	private JFormattedTextField defaultDateBox;

	private JCheckBox todayBox;

	private JPanel defaultDatePanel;

	public DefaultDateComboBoxFilter(String name) {
		this(name, name);
	}

	public DefaultDateComboBoxFilter(String name, boolean useDefault, DefaultDate defaultValue) {
		this(name, name, useDefault, defaultValue);
	}

	public DefaultDateComboBoxFilter(String id, String name) {
		this(id, name, 75);
	}

	public DefaultDateComboBoxFilter(String id, String name, boolean useDefault, DefaultDate defaultValue) {
		this(id, name, 75, useDefault, defaultValue);
	}

	public DefaultDateComboBoxFilter(String id, String name, int width) {
		this(id, name, width, false, null);
	}

	public DefaultDateComboBoxFilter(String id, String name, int width, boolean useDefault, DefaultDate defaultValue) {
		super();
		this.id = id;
		this.name = name;

		dateBox = SwingComponentFactory.createDateField(false, true);
		dateBox.setPreferredSize(new Dimension(width, dateBox.getPreferredSize().height));

		if (useDefault) {
			todayBox = new JCheckBox("Aujourd'hui");
			todayBox.addActionListener(new TodayActionListener());
			defaultDateBox = SwingComponentFactory.createDateField(false, true);
			defaultDateBox.setPreferredSize(new Dimension(width, dateBox.getPreferredSize().height));
			setDefaultValue(defaultValue);

			FormLayout layout = new FormLayout("p,3dlu,p", "p");
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();

			builder.add(todayBox, cc.xy(1, 1));
			builder.add(defaultDateBox, cc.xy(3, 1));

			defaultDatePanel = builder.getPanel();
		}

		initialize();
	}

	private final class TodayActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			defaultDateBox.setEnabled(!todayBox.isSelected());
			if (todayBox.isSelected()) {
				defaultDateBox.setValue(DateTimeUtils.clearHourForDate(new Date()));
			}
		}
	}

	@Override
	public JComponent getComponent() {
		return dateBox;
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
		return (Date) dateBox.getValue();
	}

	@Override
	public void setValue(Object o) {
		if (o != null) {
			dateBox.setValue(o);
		} else {
			dateBox.setValue(DateTimeUtils.clearHourForDate(new Date()));
		}
	}

	@Override
	public Serializable getDefaultValue() {
		if (defaultDateBox != null) {
			return new DefaultDate((Date) defaultDateBox.getValue(), todayBox.isSelected());
		}
		return null;
	}

	@Override
	public void copyDefaultValue() {
		if (defaultDateBox != null) {
			dateBox.setValue(defaultDateBox.getValue());
		} else {
			dateBox.setValue(null);
		}
	}

	@Override
	public JComponent getDefaultComponent() {
		return defaultDatePanel;
	}

	@Override
	public void setDefaultValue(Object o) {
		if (defaultDateBox != null) {
			if (o != null) {
				DefaultDate dd = (DefaultDate) o;
				defaultDateBox.setValue(dd.getDate());
				todayBox.setSelected(dd.isToday());
				defaultDateBox.setEnabled(!todayBox.isSelected());

				if (todayBox.isSelected()) {
					defaultDateBox.setValue(DateTimeUtils.clearHourForDate(new Date()));
				}
			} else {
				defaultDateBox.setValue(DateTimeUtils.clearHourForDate(new Date()));
			}
		}
	}
}
