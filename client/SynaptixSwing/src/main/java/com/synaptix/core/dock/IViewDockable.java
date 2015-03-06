package com.synaptix.core.dock;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.synaptix.client.view.io.StatsInput;
import com.synaptix.client.view.io.StatsOutput;
import com.synaptix.core.event.ViewDockableListener;
import com.synaptix.core.event.ViewDockableStateListener;

public interface IViewDockable extends ViewDockableStateListener {
	
	public abstract String getId();
	
	public abstract String getName();
	
	public abstract String getCategorie();
	
	public abstract JComponent getView();
	
	public abstract Icon getIcon();
	
	public abstract void readStats(StatsInput in);

	public abstract void writeStats(StatsOutput out);
	
	public abstract void addViewDockableListener(ViewDockableListener l);
	
	public abstract void removeViewDockableListener(ViewDockableListener l);
	
}
