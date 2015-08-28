package com.synaptix.widget.view.swing.dialog;

import com.synaptix.entity.IEntity;
import com.synaptix.widget.view.dialog.ICRUDBeanDialogView;

public abstract class AbstractCRUDBeanExtensionDialog<E extends IEntity> extends AbstractBeanExtensionDialog<E> {

	private static final long serialVersionUID = -5578233679192509983L;

	public AbstractCRUDBeanExtensionDialog(String title) {
		super(title);
	}

	public AbstractCRUDBeanExtensionDialog(String title, String subtitle) {
		super(title, subtitle);
	}

	protected final void fixOriginal() {
		if (getBeanDialog() instanceof ICRUDBeanDialogView) {
			((ICRUDBeanDialogView<E>) getBeanDialog()).fixOriginal();
		}
	}
}
