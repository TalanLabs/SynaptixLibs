package com.synaptix.widget.view.swing;

import java.awt.Color;

import javax.swing.UIManager;

import org.pushingpixels.lafplugin.LafComponentPlugin;

import com.synaptix.widget.table.view.swing.ui.MySubstanceTableUI;
import com.synaptix.widget.vldocking.view.swing.ui.SubstanceDockViewTitleBarUI;
import com.vlsolutions.swing.docking.ui.DockingUISettings;

public class SynaptixSubstancePlugin implements LafComponentPlugin {

	@Override
	public Object[] getDefaults(Object mSkin) {
		Object[] defaults = new Object[] { "DockViewTitleBarUI", SubstanceDockViewTitleBarUI.class.getName(), "VLDocking.highlight", Color.red, "TableUI", MySubstanceTableUI.class.getName() };
		return defaults;
	}

	@Override
	public void initialize() {
		// A faire pour qu'il ne soit pas écrasé par la suite
		DockingUISettings.getInstance().installUI();

		// Effacer les ui qu'on va ecraser
		UIManager.getDefaults().remove("DockViewTitleBarUI");
		// UIManager.getDefaults().remove("DockViewTitleBar.border");

		UIManager.getDefaults().put("TableUI", MySubstanceTableUI.class.getName());
	}

	@Override
	public void uninitialize() {
	}
}
