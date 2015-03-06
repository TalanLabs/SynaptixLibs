package com.synaptix.widget.view.swing.helper;

import javax.swing.Action;

public interface IActionsBuilder<F extends IActionsBuilder<F>> {

	public F addSeparator();

	public F addAction(Action action);

}
