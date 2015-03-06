package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractUpAction extends AbstractAction {

	private static final long serialVersionUID = 4421497859266864574L;

	public AbstractUpAction() {
		this(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().up());
	}

	public AbstractUpAction(String text) {
		super(text, IconHelper.Icons.UP.getIcon());
	}

}
