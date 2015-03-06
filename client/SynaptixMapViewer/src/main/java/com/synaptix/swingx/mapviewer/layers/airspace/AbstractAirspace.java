package com.synaptix.swingx.mapviewer.layers.airspace;

import java.awt.Color;

import com.synaptix.swingx.mapviewer.layers.Highlightable;
import com.synaptix.swingx.mapviewer.layers.Selectable;
import com.synaptix.swingx.mapviewer.layers.ToolTipable;

public abstract class AbstractAirspace implements Airspace, Highlightable,
		Selectable, ToolTipable {

	protected Color selectedColor = new Color(0, 0, 255, 128);

	protected Color borderSelectedColor = new Color(0, 0, 255, 192);
	
	private boolean visible;

	private boolean highlighted;

	private boolean highlightable;

	private boolean selected;

	private boolean selectable;

	private String toolTip;

	public AbstractAirspace() {
		super();

		this.visible = true;
		this.highlighted = false;
		this.selected = false;
		this.toolTip = null;

		this.highlightable = true;
		this.selectable = true;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean isHighlighted() {
		return highlighted;
	}

	@Override
	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getToolTip() {
		return toolTip;
	}

	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}

	@Override
	public boolean isHighlightable() {
		return highlightable;
	}

	public void setHighlightable(boolean highlightable) {
		this.highlightable = highlightable;
	}

	@Override
	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
}
