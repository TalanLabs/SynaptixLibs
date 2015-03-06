package com.synaptix.swing;

import javax.swing.JScrollBar;
import javax.swing.UIManager;

public class JWheelBar extends JScrollBar {

	private static final long serialVersionUID = -5570514321670042688L;
	
	private static final String uiClassID = "WheelBarUI"; //$NON-NLS-1$
	
	static {
		UIManager.getDefaults().put(uiClassID,
				"com.synaptix.swing.plaf.basic.BasicWheelBarUI"); //$NON-NLS-1$
	}
	
	public JWheelBar(int orientation) {
		super(orientation);
	}

	public String getUIClassID() {
		return uiClassID;
	}
}
