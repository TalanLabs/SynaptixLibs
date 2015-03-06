package com.synaptix.swing.triage;


public abstract class VoyageDraw {
		
	protected String toolTipText;
	
	public abstract boolean isSelected();
	
	public String getToolTipText() {
		return toolTipText;
	}
	
	public void setToolTipText(String toolTipText) {
		this.toolTipText = toolTipText;
	}
	
	public abstract TriageVoyageDrawRenderer getRenderer();
	
}
