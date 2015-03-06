package com.synaptix.swing.widget;

import javax.swing.AbstractAction;

import com.synaptix.swing.SwingMessages;

public abstract class AbstractCloseAction extends AbstractAction {

	private static final long serialVersionUID = -3382073337044300487L;

	public AbstractCloseAction() {
		super(SwingMessages.getString("AbstractCloseAction.0")); //$NON-NLS-1$
	}
}
