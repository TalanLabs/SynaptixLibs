package com.synaptix.swing.widget;

import javax.swing.AbstractAction;

import com.synaptix.swing.SwingMessages;

public abstract class AbstractCancelAction extends AbstractAction {

	private static final long serialVersionUID = -3382073337044300487L;

	public AbstractCancelAction() {
		super(SwingMessages.getString("AbstractCancelAction.0")); //$NON-NLS-1$
	}
}
