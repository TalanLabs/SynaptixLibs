package com.synaptix.widget.hierarchical.view.swing.component;

/**
 * Defines all the properties fired within the internal components of the JSyHierarchicalPanel
 */
public enum ChangePropertyName {
	/* Fired from JSyHierarchicalPanel when the model is set */
	MODEL,
	/* Fired from the horizontal scrollbar when its position changes */
	HORIZONTAL_SCROLL,
	/* Fired from the vertical scrollbar when its position changes */
	VERTICAL_SCROLL,
	/* Fired from any panel being able to be shifted horizontally */
	HORIZONTAL_POSITION,
	/* Fired from any panel being able to be shifted vertically */
	VERTICAL_POSITION,
	/* Fired from any panel being able to be scrolled vertically */
	VERTICAL_WHEEL,
	/* Fired from any panel being able to be scrolled horizontally */
	HORIZONTAL_WHEEL,
	/* fired from JSyHierarchicalPanel when the title definition is set */
	TITLE_COLUMNS,
	/* fired from JSyHierarchicalPanel when the title definition is set */
	VALUE_COLUMN,
	/* fired from JSyHierarchicalPanel when the model is set */
	SUMMARY_COLUM,
	/* fired from JSyHierarchicalPanel when the selection model is set */
	SELECTION_MODEL,

	LINE_ORDER_CHANGED
}