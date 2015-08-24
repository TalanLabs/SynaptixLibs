package com.synaptix.client.common.view.dialog;

import com.synaptix.widget.view.dialog.IBeanDialogView;

public interface IBeanWithValidationDialogView<E> extends IBeanDialogView<E> {

	public static final int VALIDATE = 3;

	/**
	 * Activates or desactivates the validate button
	 * 
	 * @param validateActive
	 */
	public void setValidateActive(boolean validateActive);

	/**
	 * Makes the button visible or invisible<br/>
	 * Must be called before showDialog
	 * 
	 * @param validateVisible
	 */
	public void setValidateVisible(boolean validateVisible);

	public void setBean(E bean);
}
