package com.synaptix.widget.view.swing.extension;

import java.util.EventListener;

import com.jgoodies.validation.ValidationResult;

public interface ValidatorListener extends EventListener {

	public void updateValidator(Object source, String id,
			ValidationResult result);

}
