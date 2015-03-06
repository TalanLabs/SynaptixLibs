package com.synaptix.client.view.header;

import com.synaptix.client.view.IView;
import com.synaptix.client.view.io.StatsInput;
import com.synaptix.client.view.io.StatsOutput;

public interface IHeader {

	public abstract String getId();

	public abstract String getName();

	public abstract String getCategorie();

	public abstract IView getView();

	public abstract void readStats(StatsInput in);

	public abstract void writeStats(StatsOutput out);

	public abstract void addHeaderListener(HeaderListener l);

	public abstract void removeHeaderListener(HeaderListener l);

	public abstract void headerStateChanged(HeaderStateEvent e);

}
