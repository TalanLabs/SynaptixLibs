package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractFinishAction extends AbstractAction {

	private static final long serialVersionUID = 4421497859266864574L;

	public AbstractFinishAction() {
		this(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().finish());
	}

	public AbstractFinishAction(String text) {
		super(text, IconHelper.Icons.FINISH.getIcon());
	}

}
