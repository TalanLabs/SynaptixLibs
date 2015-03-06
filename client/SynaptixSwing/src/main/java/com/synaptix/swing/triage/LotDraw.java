package com.synaptix.swing.triage;

import java.util.Date;

public abstract class LotDraw {
	
	protected String toolTipText;
	
	public abstract Date getDate();
	
	public abstract Date getDateMin();
	
	public abstract Date getDateMax();
	
	public abstract boolean isSelected();
	
	public String getToolTipText() {
		return toolTipText;
	}
	
	public void setToolTipText(String toolTipText) {
		this.toolTipText = toolTipText;
	}
	
	public abstract TriageInfoLotDrawRenderer getRenderer();
	
}
