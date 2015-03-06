package com.synaptix.swing;

import java.math.BigDecimal;

import javax.swing.SpinnerNumberModel;

public class SpinnerBigDecimalModel extends SpinnerNumberModel {

	private static final long serialVersionUID = 7772872631115130546L;

	private BigDecimal stepSize, value;

	private BigDecimal minimum, maximum;

	public SpinnerBigDecimalModel() {
		this(BigDecimal.ZERO, null, null, BigDecimal.ONE);
	}

	public SpinnerBigDecimalModel(BigDecimal value, BigDecimal minimum,
			BigDecimal maximum, BigDecimal stepSize) {
		super();
		if ((value == null) || (stepSize == null)) {
			throw new IllegalArgumentException(
					"value and stepSize must be non-null"); //$NON-NLS-1$
		}
		if (!(((minimum == null) || (minimum.compareTo(value) <= 0)) && ((maximum == null) || (maximum
				.compareTo(value) >= 0)))) {
			throw new IllegalArgumentException(
					"(minimum <= value <= maximum) is false"); //$NON-NLS-1$
		}
		this.value = value;
		this.minimum = minimum;
		this.maximum = maximum;
		this.stepSize = stepSize;
	}

	public void setMinimum(BigDecimal minimum) {
		if ((minimum == null) ? (this.minimum != null) : !minimum
				.equals(this.minimum)) {
			this.minimum = minimum;
			value = incrValue(0);
			fireStateChanged();
		}
	}

	public BigDecimal getMinimum() {
		return minimum;
	}

	public void setMaximum(BigDecimal maximum) {
		if ((maximum == null) ? (this.maximum != null) : !maximum
				.equals(this.maximum)) {
			this.maximum = maximum;
			value = incrValue(0);
			fireStateChanged();
		}
	}

	public BigDecimal getMaximum() {
		return maximum;
	}

	public void setStepSize(BigDecimal stepSize) {
		if (stepSize == null) {
			throw new IllegalArgumentException("null stepSize"); //$NON-NLS-1$
		}
		if (!stepSize.equals(this.stepSize)) {
			this.stepSize = stepSize;
			fireStateChanged();
		}
	}

	public BigDecimal getStepSize() {
		return stepSize;
	}

	private BigDecimal incrValue(int dir) {
		BigDecimal newValue = value.add(stepSize.multiply(new BigDecimal(dir)));

		if ((maximum != null) && (maximum.compareTo(newValue) < 0)) {
			return maximum;
		} else if ((minimum != null) && (minimum.compareTo(newValue) > 0)) {
			return minimum;
		} else {
			return newValue;
		}
	}

	public Object getNextValue() {
		return incrValue(+1);
	}

	public Object getPreviousValue() {
		return incrValue(-1);
	}

	public BigDecimal getBigDecimal() {
		return value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		if ((value == null) || !(value instanceof Number)) {
			throw new IllegalArgumentException("illegal value"); //$NON-NLS-1$
		}
		if (!value.equals(this.value)) {
			this.value = (BigDecimal) value;
			fireStateChanged();
		}
	}
}
