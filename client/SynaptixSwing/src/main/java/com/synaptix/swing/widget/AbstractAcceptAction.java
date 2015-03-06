package com.synaptix.swing.widget;

import javax.swing.AbstractAction;

import com.synaptix.swing.SwingMessages;

public abstract class AbstractAcceptAction extends AbstractAction {

	private static final long serialVersionUID = -3382073337044300487L;

	public AbstractAcceptAction() {
		super(SwingMessages.getString("AbstractAcceptAction.0")); //$NON-NLS-1$
	}
}
