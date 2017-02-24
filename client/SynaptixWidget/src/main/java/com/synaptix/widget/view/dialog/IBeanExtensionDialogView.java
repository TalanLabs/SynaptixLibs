package com.synaptix.widget.view.dialog;

import java.util.Map;

import com.synaptix.client.view.IView;

public interface IBeanExtensionDialogView<E> extends IView {

	public String getTitle();

	public String getSubtitle();

	public void commit(E bean, Map<String, Object> valueMap);

	public void openDialog();

	public void setBean(E bean, Map<String, Object> valueMap, boolean readOnly, boolean creation);

	public boolean closeDialog();

	public void addValidatorListener(BeanValidatorListener<E> l);

	public void removeValidatorListener(BeanValidatorListener<E> l);

	public void setBeanDialog(IBeanDialogView0<E> beanDialog);

	public void setReadOnly(boolean readOnly);
}
