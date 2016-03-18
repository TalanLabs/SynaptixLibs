package com.synaptix.widget.model;

import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;

public class RibbonData {

	private final String title;

	private final String ribbonTaskTitle;

	private final int ribbonTaskPriority;

	private final String ribbonBandTitle;

	private final int ribbonBandPriority;

	private final String category;

	private final ResizableIcon icon;

	private final RibbonElementPriority priority;

	/**
	 * Medium priority view
	 *
	 * @param title
	 *            Title of the view
	 * @param ribbonTaskTitle
	 *            Ribbon Task Title
	 * @param ribbonTaskPriority
	 *            Ribbon Task Priority
	 * @param ribbonBandTitle
	 *            Ribbon Band Title
	 * @param ribbonBandPriority
	 *            Ribbon Band Priority
	 * @param category
	 *            Category
	 * @param icon
	 *            Icon
	 */
	public RibbonData(String title, String ribbonTaskTitle, int ribbonTaskPriority, String ribbonBandTitle, int ribbonBandPriority, String category) {
		this(title, ribbonTaskTitle, ribbonTaskPriority, ribbonBandTitle, ribbonBandPriority, category, null, RibbonElementPriority.MEDIUM);
	}

	/**
	 * Top priority view with icon
	 *
	 * @param title
	 *            Title of the view
	 * @param ribbonTaskTitle
	 *            Ribbon Task Title
	 * @param ribbonTaskPriority
	 *            Ribbon Task Priority
	 * @param ribbonBandTitle
	 *            Ribbon Band Title
	 * @param ribbonBandPriority
	 *            Ribbon Band Priority
	 * @param category
	 *            Category
	 * @param icon
	 *            Icon
	 */
	public RibbonData(String title, String ribbonTaskTitle, int ribbonTaskPriority, String ribbonBandTitle, int ribbonBandPriority, String category, ResizableIcon icon) {
		this(title, ribbonTaskTitle, ribbonTaskPriority, ribbonBandTitle, ribbonBandPriority, category, icon, RibbonElementPriority.TOP);
	}

	/**
	 *
	 * @param title
	 *            Title of the view
	 * @param ribbonTaskTitle
	 *            Ribbon Task Title
	 * @param ribbonTaskPriority
	 *            Ribbon Task Priority
	 * @param ribbonBandTitle
	 *            Ribbon Band Title
	 * @param ribbonBandPriority
	 *            Ribbon Band Priority
	 * @param category
	 *            Category
	 * @param icon
	 *            Icon
	 * @param priority
	 *            Priority
	 */
	public RibbonData(String title, String ribbonTaskTitle, int ribbonTaskPriority, String ribbonBandTitle, int ribbonBandPriority, String category, ResizableIcon icon, RibbonElementPriority priority) {
		super();
		this.title = title;
		this.ribbonTaskTitle = ribbonTaskTitle;
		this.ribbonTaskPriority = ribbonTaskPriority;
		this.ribbonBandTitle = ribbonBandTitle;
		this.ribbonBandPriority = ribbonBandPriority;
		this.category = category;
		this.icon = icon;
		this.priority = priority;
	}

	public final String getTitle() {
		return title;
	}

	public final String getRibbonTaskTitle() {
		return ribbonTaskTitle;
	}

	public final int getRibbonTaskPriority() {
		return ribbonTaskPriority;
	}

	public final String getRibbonBandTitle() {
		return ribbonBandTitle;
	}

	public final int getRibbonBandPriority() {
		return ribbonBandPriority;
	}

	public final String getCategory() {
		return category;
	}

	public final ResizableIcon getIcon() {
		return icon;
	}

	public final RibbonElementPriority getPriority() {
		return priority;
	}

	/**
	 * Should the ribbon data be registered in the ribbon?
	 */
	public boolean shouldRegisterInRibbon() {
		return true;
	}
}
