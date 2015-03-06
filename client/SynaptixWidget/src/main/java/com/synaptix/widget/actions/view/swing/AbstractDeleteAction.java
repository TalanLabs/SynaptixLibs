package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractDeleteAction extends AbstractAction {

	private static final long serialVersionUID = 4421497859266864574L;

	public AbstractDeleteAction() {
		this(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().delete());
	}

	public AbstractDeleteAction(String text) {
		super(text, IconHelper.Icons.DELETE.getIcon());
	}

}
