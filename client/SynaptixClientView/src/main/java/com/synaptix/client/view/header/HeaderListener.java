package com.synaptix.client.view.header;

import java.util.EventListener;

public interface HeaderListener extends EventListener {

	public abstract void headerChanged(HeaderEvent event);

}
