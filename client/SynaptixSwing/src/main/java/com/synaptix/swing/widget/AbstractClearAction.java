package com.synaptix.swing.widget;

import javax.swing.AbstractAction;

import com.synaptix.swing.SwingMessages;

public abstract class AbstractClearAction extends AbstractAction {

	private static final long serialVersionUID = -3382073337044300487L;

	public AbstractClearAction() {
		super(SwingMessages.getString("AbstractClearAction.0")); //$NON-NLS-1$
	}
}
