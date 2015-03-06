package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractNoAction extends AbstractAction {

	private static final long serialVersionUID = -3382073337044300487L;

	public AbstractNoAction() {
		this(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().no());
	}

	public AbstractNoAction(String text) {
		super(text, IconHelper.Icons.NO.getIcon());
	}
}
