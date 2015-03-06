package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractCancelAction extends AbstractAction {

	private static final long serialVersionUID = -3382073337044300487L;

	public AbstractCancelAction() {
		this(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().cancel());
	}

	public AbstractCancelAction(String name) {
		super(name, IconHelper.Icons.CANCEL.getIcon());
	}
}
