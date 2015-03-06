package com.synaptix.widget.view.dialog;

import java.util.Map;

import com.synaptix.client.view.IView;

public interface IBeanWizardDialogView<E> extends IBeanDialogView0<E> {

	public int showWizardDialog(IView parent, E bean, Map<String, Object> valueMap);

	public void previous();

	public void next();
}
