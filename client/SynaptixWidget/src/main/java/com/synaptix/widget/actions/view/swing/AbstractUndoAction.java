package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractUndoAction extends AbstractAction {

	private static final long serialVersionUID = 4421497859266864574L;

	public AbstractUndoAction() {
		this(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().undo());
	}

	public AbstractUndoAction(String text) {
		super(text, IconHelper.Icons.UNDO.getIcon());
	}
}
