package com.synaptix.core.event;

import java.util.EventListener;

public interface ViewDockableListener extends EventListener {

	public abstract void viewDockableChanged(ViewDockableEvent event);

}
