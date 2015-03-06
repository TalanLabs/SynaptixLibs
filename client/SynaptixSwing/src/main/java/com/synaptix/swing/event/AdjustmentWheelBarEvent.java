package com.synaptix.swing.event;

import java.awt.AWTEvent;

public class AdjustmentWheelBarEvent extends AWTEvent {

	private static final long serialVersionUID = -1347729171378619295L;

	private int value;

	private boolean valueIsAdjusting;

	public AdjustmentWheelBarEvent(Object source, int id, int value,
			boolean valueIsAdjusting) {
		super(source, id);
	}

	public int getValue() {
		return value;
	}

	public boolean isValueIsAdjusting() {
		return valueIsAdjusting;
	}
}
