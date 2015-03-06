package com.synaptix.widget.view.swing;

import javax.swing.JSlider;

import com.synaptix.common.model.Binome;
import com.synaptix.widget.view.swing.ui.SyAreaSliderUI;

public class JSyAreaSlider extends JSlider {

	private static final long serialVersionUID = 6913976010529241511L;

	private int value1;

	private int value2;

	private int selectedThumb = 0;

	public JSyAreaSlider() {
		this(0, 200);
	}

	public JSyAreaSlider(int min, int max) {
		super(HORIZONTAL, min, max, (min + max) / 2);

		value1 = min;
		value2 = max;

		setUI(new SyAreaSliderUI(this));
	}

	public int getValue1() {
		return value1;
	}

	public int getValue2() {
		return value2;
	}

	public Binome<Integer, Integer> getMinMax() {
		Binome<Integer, Integer> minMax = new Binome<Integer, Integer>();
		if (value1 < value2) {
			minMax.setValue1(value1);
			minMax.setValue2(value2);
		} else {
			minMax.setValue1(value2);
			minMax.setValue2(value1);
		}
		return minMax;
	}

	@Override
	public void setValue(int n) {
		Binome<Integer, Integer> oldValue = getMinMax();
		switch (selectedThumb) {
		case 0:
			value1 = n;
			break;
		case 1:
			value2 = n;
			break;
		default:
			break;
		}
		firePropertyChange("value", oldValue, getMinMax());

		super.setValue(n);
	}

	public int getSelectedThumb() {
		return selectedThumb;
	}

	public void setValue1(int n) {
		value1 = n;
		repaint();
	}

	public void setValue2(int n) {
		value2 = n;
		repaint();
	}

	public void setSelectedThumb(int selectedThumb, boolean switchValue) {
		this.selectedThumb = selectedThumb;
		switch (selectedThumb) {
		case 0:
			if (switchValue) {
				value2 = value1;
			}
			super.setValue(getValue1());
			break;
		case 1:
			if (switchValue) {
				value1 = value2;
			}
			super.setValue(getValue2());
			break;
		default:
			break;
		}
	}
}
