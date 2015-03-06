package com.synaptix.widget.view.swing.ui;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.action.AbstractActionExt;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.swingx.SubstanceErrorPaneUI;

import com.synaptix.widget.util.StaticWidgetHelper;

public class SynaptixErrorPaneUI extends SubstanceErrorPaneUI {

	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SynaptixErrorPaneUI();
	}

	@Override
	protected void configureReportAction(AbstractActionExt reportAction) {
		super.configureReportAction(reportAction);
		reportAction.setName(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().reportError());
	}
}
