package com.synaptix.swing;

public abstract class AbstractSimpleTask implements SimpleTask {

	public boolean isNoClipping() {
		return true;
	}

	public boolean isSelected() {
		return true;
	}

	public boolean isShowIntersection() {
		return true;
	}

}
