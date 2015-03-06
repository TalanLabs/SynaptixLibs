package com.synaptix.swing;

import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.UIManager;

import com.synaptix.swing.plaf.SyListUI;

public class JSyList extends JList {

	private static final long serialVersionUID = 6417477029842077478L;

	private static final String uiClassID = "SyListUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID,
				"com.synaptix.swing.plaf.basic.BasicSyListUI"); //$NON-NLS-1$
	}

	public JSyList() {
		super();

		updateUI();
	}

	public JSyList(ListModel dataModel) {
		super(dataModel);

		updateUI();
	}

	public JSyList(Object[] listData) {
		super(listData);

		updateUI();
	}

	public JSyList(Vector<?> listData) {
		super(listData);

		updateUI();
	}

	public SyListUI getUI() {
		return (SyListUI) ui;
	}

	public void setUI(SyListUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((SyListUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}
}
