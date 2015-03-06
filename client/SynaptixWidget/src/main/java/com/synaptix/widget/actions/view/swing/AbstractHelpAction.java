package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractHelpAction extends AbstractAction {

	private static final long serialVersionUID = 4421497859266864574L;

	public AbstractHelpAction() {
		this(""); //$NON-NLS-1$
	}

	public AbstractHelpAction(String text) {
		super(text, IconHelper.Icons.HELP.getIcon());
	}
}
