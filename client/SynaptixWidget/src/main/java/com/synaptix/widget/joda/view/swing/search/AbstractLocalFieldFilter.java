package com.synaptix.widget.joda.view.swing.search;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import org.joda.time.base.BaseLocal;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.search.AbstractFilter;
import com.synaptix.widget.joda.context.DefaultLocal;
import com.synaptix.widget.util.StaticWidgetHelper;

/*protected package*/abstract class AbstractLocalFieldFilter<E extends BaseLocal> extends AbstractFilter {

	private String id;

	private String name;

	private JFormattedTextField localField;

	private JFormattedTextField defaultLocalField;

	private JCheckBox todayBox;

	private JPanel defaultLocalPanel;

	private JComponent localPanel;

	public AbstractLocalFieldFilter(String id, String name, int width, boolean useDefault, DefaultLocal<E> defaultValue) {
		super();
		this.id = id;
		this.name = name;

		localField = new JFormattedTextField();
		localField.setPreferredSize(new Dimension(width, localField.getPreferredSize().height));
		localField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON3)) {
					setValue(null);
				}
			}
		});

		localPanel = decorate(localField);

		if (useDefault) {
			todayBox = new JCheckBox(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().today());
			todayBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					defaultLocalField.setEnabled(!todayBox.isSelected());
					if (todayBox.isSelected()) {
						defaultLocalField.setValue(newTodayLocal());
					}
				}
			});
			defaultLocalField = new JFormattedTextField();
			defaultLocalField.setPreferredSize(new Dimension(width, localField.getPreferredSize().height));
			setDefaultValue(defaultValue);

			FormLayout layout = new FormLayout("p,3dlu,p", "p");
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();

			builder.add(todayBox, cc.xy(1, 1));
			builder.add(decorate(defaultLocalField), cc.xy(3, 1));

			defaultLocalPanel = builder.getPanel();
		}

		initialize();
	}

	protected abstract JComponent decorate(JFormattedTextField localField);

	protected abstract E newTodayLocal();

	@Override
	public JComponent getComponent() {
		return localPanel;
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
		return (Serializable) localField.getValue();
	}

	@Override
	public void setValue(Object o) {
		if (o != null) {
			localField.setValue(o);
		} else {
			localField.setValue(null);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Serializable getDefaultValue() {
		if (defaultLocalField != null) {
			return new DefaultLocal<E>((E) defaultLocalField.getValue(), todayBox.isSelected());
		}
		return null;
	}

	@Override
	public void copyDefaultValue() {
		if (defaultLocalField != null) {
			localField.setValue(defaultLocalField.getValue());
		} else {
			localField.setValue(null);
		}
	}

	@Override
	public JComponent getDefaultComponent() {
		return defaultLocalPanel;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setDefaultValue(Object o) {
		if (defaultLocalField != null) {
			if (o != null) {
				DefaultLocal<E> dd = (DefaultLocal<E>) o;
				todayBox.setSelected(dd.isToday());
				defaultLocalField.setEnabled(!todayBox.isSelected());

				if (todayBox.isSelected()) {
					defaultLocalField.setValue(newTodayLocal());
				} else {
					if (dd.getLocal() != null) {
						defaultLocalField.setValue(dd.getLocal());
					} else {
						defaultLocalField.setValue(null);
					}
				}
			} else {
				defaultLocalField.setValue(null);
			}
		}
	}
}
