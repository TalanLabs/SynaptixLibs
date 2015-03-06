package com.synaptix.core.option;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.synaptix.client.view.io.StatsInput;
import com.synaptix.client.view.io.StatsOutput;
import com.synaptix.core.event.ViewOptionListener;
import com.synaptix.core.event.ViewOptionStateListener;

public interface IViewOption extends ViewOptionStateListener {

	public abstract String getId();

	public abstract String getName();

	public abstract String getCategorie();

	public abstract JComponent getView();

	public abstract Icon getIcon();

	public abstract void restoreDefault();

	public abstract void apply();

	public abstract void cancelled();

	public abstract void readStats(StatsInput in);

	public abstract void writeStats(StatsOutput out);

	public abstract void addViewOptionListener(ViewOptionListener l);

	public abstract void removeViewOptionListener(ViewOptionListener l);

}
