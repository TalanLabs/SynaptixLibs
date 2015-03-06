package com.synaptix.widget.perimeter.view.swing;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.synaptix.widget.component.view.swing.DefaultSearchTablePageComponentsPanel;
import com.synaptix.widget.util.StaticWidgetHelper;

public abstract class AbstractPerimeterFilterAction extends AbstractPerimeterAction {

	private static final long serialVersionUID = 9165952562542464541L;

	public AbstractPerimeterFilterAction() {
		super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().doFilter(), new ImageIcon(
				DefaultSearchTablePageComponentsPanel.class.getResource("/com/synaptix/widget/actions/view/swing/iconRefresh.png")));

		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);

		refresh();

		// setEnabled(false);
	}

	@Override
	public void fireValuesChangedAction() {
		super.fireValuesChangedAction();

		setEnabled(areValidatedMandatoryFilters());
	}

	@Override
	public void fireRefreshAction() {
		super.fireRefreshAction();

		refresh();
		// setEnabled(false);
	}

	protected abstract void refresh();

	protected boolean areValidatedMandatoryFilters() {
		return true;
	}
}
