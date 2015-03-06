package com.synaptix.widget.view.dialog;

import com.jgoodies.validation.ValidationResult;

public interface BeanValidatorListener<E> {

	public void updateValidator(IBeanExtensionDialogView<E> source, ValidationResult result);

}
