package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractAcceptAction extends AbstractAction {

	private static final long serialVersionUID = -3382073337044300487L;

	public AbstractAcceptAction() {
		this(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().ok());
	}

	public AbstractAcceptAction(String text) {
		super(text, IconHelper.Icons.CHECK.getIcon());
	}
}
