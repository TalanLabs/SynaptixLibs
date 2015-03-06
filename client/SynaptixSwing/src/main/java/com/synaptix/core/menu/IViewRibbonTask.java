package com.synaptix.core.menu;

import javax.swing.JComponent;

public interface IViewRibbonTask {

	public abstract String getId();

	public abstract String getName();

	public abstract JComponent getView();

}
