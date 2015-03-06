package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractSaveAction extends AbstractAction {

	private static final long serialVersionUID = 4421497859266864574L;

	public AbstractSaveAction() {
		this(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().save());
	}

	public AbstractSaveAction(String text) {
		super(text, IconHelper.Icons.SAVE.getIcon());
	}
}
