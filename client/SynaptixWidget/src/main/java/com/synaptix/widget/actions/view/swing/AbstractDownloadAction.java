package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractDownloadAction extends AbstractAction {

	private static final long serialVersionUID = -3382073337044300487L;

	public AbstractDownloadAction() {
		this(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().downloadHellip());
	}

	public AbstractDownloadAction(String text) {
		super(text, IconHelper.Icons.DOWNLOAD.getIcon()); //$NON-NLS-1$
	}
}
