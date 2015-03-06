package com.synaptix.widget.view.swing.extension;

import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;


public interface IBeanExtensionDialog<E> {

	public abstract String getId();

	public abstract String getTitle();

	public abstract Icon getIcon();

	public abstract JComponent getView();

	public abstract void load(E e, Map<String, Object> extensionValue);

	public abstract void commit(E e, Map<String, Object> extensionValue);

	public abstract void setReadOnly(boolean readOnly);

	public abstract void closeDialog();

	public abstract void addValidatorListener(ValidatorListener l);

	public abstract void removeValidatorListener(ValidatorListener l);

}
