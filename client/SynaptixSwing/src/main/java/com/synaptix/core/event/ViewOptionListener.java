package com.synaptix.core.event;

import java.util.EventListener;

import com.jgoodies.validation.ValidationResult;
import com.synaptix.core.option.IViewOption;

public interface ViewOptionListener extends EventListener {

	public abstract void viewOptionValidationResultChanged(
			IViewOption viewOption, ValidationResult result);

}
