package com.synaptix.widget.actions.view.swing;

import javax.swing.AbstractAction;

import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

public abstract class AbstractExportExcelAction extends AbstractAction {

	private static final long serialVersionUID = -3382073337044300487L;

	public AbstractExportExcelAction() {
		this(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().exportExcelEllipsis());
	}

	public AbstractExportExcelAction(String text) {
		super(text, IconHelper.Icons.EXPORT_EXCEL.getIcon());
	}
}
