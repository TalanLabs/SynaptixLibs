package com.synaptix.widget.hierarchical.writer;

public final class CellInformation {

	private Object object;

	private final int horizontalCellSpan;

	private final int verticalCellSpan;

	/**
	 * Create a cell information with a default cell span of 1
	 * 
	 * @param object
	 */
	public CellInformation(Object object) {
		this(object, 1);
	}

	public CellInformation(Object object, int horizontalCellSpan) {
		this(object, horizontalCellSpan, 1);
	}

	public CellInformation(Object object, int horizontalCellSpan, int verticalCellSpan) {
		this.object = object;
		this.horizontalCellSpan = horizontalCellSpan;
		this.verticalCellSpan = verticalCellSpan;
	}

	public final Object getObject() {
		return object;
	}

	public final int getHorizontalCellSpan() {
		return horizontalCellSpan;
	}

	public final int getVerticalCellSpan() {
		return verticalCellSpan;
	}
}
