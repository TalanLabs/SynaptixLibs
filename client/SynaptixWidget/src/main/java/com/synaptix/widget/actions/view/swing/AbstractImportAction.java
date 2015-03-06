package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractImportAction extends AbstractAction {

	private static final long serialVersionUID = -3382073337044300487L;

	public AbstractImportAction() {
		this(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().importDatasHellip());
	}

	public AbstractImportAction(String text) {
		super(text, IconHelper.Icons.IMPORT.getIcon()); //$NON-NLS-1$
	}
}
