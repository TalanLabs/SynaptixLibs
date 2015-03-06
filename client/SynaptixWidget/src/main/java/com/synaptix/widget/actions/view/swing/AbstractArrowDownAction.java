package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractArrowDownAction extends AbstractAction {

	private static final long serialVersionUID = 4421497859266864574L;

	public AbstractArrowDownAction(String text) {
		super(text, IconHelper.Icons.ARROW_DOWN.getIcon());
	}

}
