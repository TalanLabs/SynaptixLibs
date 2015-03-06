package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractSettingsAction extends AbstractAction {

	private static final long serialVersionUID = -3382073337044300487L;

	public AbstractSettingsAction() {
		this(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().settingsEllipsis());
	}

	public AbstractSettingsAction(String text) {
		super(text, IconHelper.Icons.SETTINGS.getIcon());
	}
}
