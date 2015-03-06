package com.synaptix.widget.perimeter.view.swing;

import com.synaptix.common.model.Binome;

public abstract class AbstractBinomeStringStringListPerimeterWidget extends AbstractListPerimeterWidget<Binome<String, String>> {

	private static final long serialVersionUID = 6179619240167076798L;

	public AbstractBinomeStringStringListPerimeterWidget(String title) {
		super(title);

		initialize();
	}

	@Override
	protected String toObjectString(Binome<String, String> o) {
		return o.getValue2();
	}
}
