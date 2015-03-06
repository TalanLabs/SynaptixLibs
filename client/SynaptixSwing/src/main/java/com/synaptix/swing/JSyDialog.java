package com.synaptix.swing;

import java.awt.Window;

import javax.swing.JDialog;

public class JSyDialog extends JDialog {

	private static final long serialVersionUID = -2732713185421773947L;
	
	private boolean separatedWindow;
	
	public JSyDialog(Window owner, String title) {
		super(owner, title);
		
		separatedWindow = false;
	}
	
	public void setSeparatedWindow(boolean separatedWindow) {
		this.separatedWindow = separatedWindow;
	}
	
	public boolean isSeparatedWindow() {
		return separatedWindow;
	}
	
}
