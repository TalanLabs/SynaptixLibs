package com.synaptix.swing.search;

import java.awt.event.ActionListener;

public interface ISearchPageControl {
	
	public abstract void setControl(int currentPage, int maxPage);
	
	public abstract void clear();
	
	public abstract void addActionListener(ActionListener l);

	public abstract void removeActionListener(ActionListener l);
	
}
