package com.synaptix.widget.perimeter.view.swing;

import java.math.BigDecimal;

import com.synaptix.common.model.Binome;

public abstract class AbstractBinomeBigDecimalStringListPerimeterWidget extends AbstractListPerimeterWidget<Binome<BigDecimal, String>> {

	private static final long serialVersionUID = 6179619240167076798L;

	public AbstractBinomeBigDecimalStringListPerimeterWidget(String title) {
		super(title);

		initialize();
	}

	@Override
	protected String toObjectString(Binome<BigDecimal, String> o) {
		return o.getValue2();
	}
}
