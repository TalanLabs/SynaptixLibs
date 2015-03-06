package com.synaptix.widget.perimeter.view.swing;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.widget.util.StaticWidgetHelper;

public class DefaultBooleanPerimeterWidget extends AbstractComboBoxPerimeterWidget implements IPerimeterWidget, IComputedPerimeter {

	private static final long serialVersionUID = 8680613037734034103L;

	private static final List<String> defaultBooleanChoiceList = new ArrayList<String>();

	private final String title;

	public DefaultBooleanPerimeterWidget(String title) {
		super(builDefaultList());

		this.title = title;
	}

	private static List<String> builDefaultList() {
		if (defaultBooleanChoiceList.isEmpty()) {
			defaultBooleanChoiceList.add(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().yes());
			defaultBooleanChoiceList.add(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().no());
		}
		return defaultBooleanChoiceList;
	}

	public DefaultBooleanPerimeterWidget(String title, String yesText, String noText) {
		super(buildList(yesText, noText));

		this.title = title;
	}

	private static List<String> buildList(String yesText, String noText) {
		List<String> list = new ArrayList<String>();
		list.add(yesText);
		list.add(noText);
		return list;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public Object getFinalValue() {
		if (comboBox.getSelectedIndex() <= 0) {
			return null;
		}
		return (comboBox.getSelectedIndex() == 1 ? true : false);
	}
}
