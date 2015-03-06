package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractSearchAction extends AbstractAction {

	private static final long serialVersionUID = 4421497859266864574L;

	public AbstractSearchAction() {
		this(""); //$NON-NLS-1$
		this.putValue(Action.SHORT_DESCRIPTION, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().searchEllipsis());
	}

	public AbstractSearchAction(String text) {
		super(text, IconHelper.Icons.SEARCH.getIcon());
	}
}
