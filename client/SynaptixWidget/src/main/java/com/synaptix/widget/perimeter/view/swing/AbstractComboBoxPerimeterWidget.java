package com.synaptix.widget.perimeter.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import com.synaptix.widget.util.StaticWidgetHelper;

public abstract class AbstractComboBoxPerimeterWidget extends AbstractPerimeterWidget implements IPerimeterWidget {

	private static final long serialVersionUID = -3060347771746600611L;

	public final String NONE = "";

	protected final JComboBox comboBox;

	public AbstractComboBoxPerimeterWidget(List<String> choiceList) {
		super();

		List<String> newChoiceList = new ArrayList<String>(choiceList.size() + 1);
		newChoiceList.addAll(choiceList);
		newChoiceList.add(0, NONE);

		comboBox = new JComboBox(newChoiceList.toArray());
		comboBox.addActionListener(new MyActionListener());

		this.addContent(comboBox);
	}

	protected JComboBox getComboBox() {
		return comboBox;
	}

	@Override
	public String getValue() {
		if (comboBox.getSelectedIndex() == 0) {
			return null;
		}
		return (String) comboBox.getSelectedItem();
	}

	@Override
	public void setValue(Object value) {
		if (value == null) {
			comboBox.setSelectedItem(NONE);
		} else {
			comboBox.setSelectedItem(value);
		}
		fireValuesChanged();
	}

	private final class MyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			fireValuesChanged();
		}
	}
}
