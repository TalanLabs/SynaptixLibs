package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractDownAction extends AbstractAction {

	private static final long serialVersionUID = 4421497859266864574L;

	public AbstractDownAction() {
		super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().down(), IconHelper.Icons.DOWN.getIcon()); //$NON-NLS-1$
	}

}
