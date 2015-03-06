package com.synaptix.swing.widget;

import javax.swing.AbstractAction;

import com.synaptix.swing.SwingMessages;

public abstract class AbstractPreviousAction extends AbstractAction {

	private static final long serialVersionUID = -3382073337044300487L;

	public AbstractPreviousAction() {
		super(SwingMessages.getString("AbstractPreviousAction.0")); //$NON-NLS-1$
	}
}
