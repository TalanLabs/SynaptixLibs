package com.synaptix.swing.selection;

import java.util.EventObject;

import org.apache.commons.lang.builder.ToStringBuilder;

public class XYSelectionModelEvent extends EventObject {

	private static final long serialVersionUID = -6079256056767449123L;

	private int firstX;

	private int firstY;

	private int lastX;

	private int lastY;

	private boolean isAdjusting;

	public XYSelectionModelEvent(XYSelectionModel source, int firstX, int firstY, int lastX, int lastY, boolean isAdjusting) {
		super(source);

		this.firstX = firstX;
		this.firstY = firstY;
		this.lastX = lastX;
		this.lastY = lastY;
		this.isAdjusting = isAdjusting;
	}

	public int getFirstX() {
		return firstX;
	}

	public int getFirstY() {
		return firstY;
	}

	public int getLastX() {
		return lastX;
	}

	public int getLastY() {
		return lastY;
	}

	public boolean isAdjusting() {
		return isAdjusting;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("firstX", firstX).append("firstY", firstY).append("lastX", lastX).append("lastY", lastY).append("isAdjusting", isAdjusting).toString();
	}
}
