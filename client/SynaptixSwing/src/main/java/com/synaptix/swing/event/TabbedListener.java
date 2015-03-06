package com.synaptix.swing.event;

import java.util.EventListener;

public interface TabbedListener extends EventListener {

	public void maximized(TabbedEvent te);

	public void restore(TabbedEvent te);

	public void allClose(TabbedEvent te);

	public void close(TabbedEvent te);
}
