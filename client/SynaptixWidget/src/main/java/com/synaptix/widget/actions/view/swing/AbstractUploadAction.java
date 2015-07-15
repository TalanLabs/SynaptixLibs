package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractUploadAction extends AbstractAction {

	private static final long serialVersionUID = -3382073337044300487L;

	public AbstractUploadAction() {
		this(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().uploadHellip());
	}

	public AbstractUploadAction(String text) {
		super(text, IconHelper.Icons.UPLOAD.getIcon()); //$NON-NLS-1$
	}
}
