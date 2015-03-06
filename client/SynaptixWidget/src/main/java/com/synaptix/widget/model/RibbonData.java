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
		this.title = title;
		this.ribbonTaskTitle = ribbonTaskTitle;
		this.ribbonTaskPriority = ribbonTaskPriority;
		this.ribbonBandTitle = ribbonBandTitle;
		this.ribbonBandPriority = ribbonBandPriority;
		this.category = category;
		this.icon = icon;
		this.priority = priority;
	}

	public String getTitle() {
		return title;
	}

	public String getRibbonTaskTitle() {
		return ribbonTaskTitle;
	}

	public int getRibbonTaskPriority() {
		return ribbonTaskPriority;
	}

	public String getRibbonBandTitle() {
		return ribbonBandTitle;
	}

	public int getRibbonBandPriority() {
		return ribbonBandPriority;
	}

	public String getCategory() {
		return category;
	}

	public ResizableIcon getIcon() {
		return icon;
	}

	public RibbonElementPriority getPriority() {
		return priority;
	}
}
